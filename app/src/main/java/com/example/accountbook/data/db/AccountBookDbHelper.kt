package com.example.accountbook.data.db

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.accountbook.data.model.*
import com.example.accountbook.data.model.Histories
import com.example.accountbook.data.model.Payments
import com.example.accountbook.data.utils.AccountBookCategories
import com.example.accountbook.data.utils.AccountBookContract
import com.example.accountbook.data.utils.AccountBookContract.getSumPriceSqlWhere
import com.example.accountbook.data.utils.AccountBookHistories
import com.example.accountbook.data.utils.AccountBookPayments
import com.example.accountbook.domain.model.*
import com.example.accountbook.utils.dateToStringMdEEType
import com.example.accountbook.utils.dateToStringYYYYMdType
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.HashMap

@Singleton
class AccountBookDbHelper @Inject constructor(
    @ApplicationContext context: Context
) : SQLiteOpenHelper(
    context, AccountBookContract.DB_NAME, null, AccountBookContract.DB_VERSION
) {

    companion object {
        const val TAG = "AccountBookDbHelper"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(AccountBookContract.SQL_ACTIVE_FOREIGN_KEY)
        db?.execSQL(AccountBookCategories.SQL_CREATE_TABLE_CATEGORY_UNIQUE)
        db?.execSQL(AccountBookPayments.SQL_CREATE_TABLE_PAYMENT_UNIQUE)
        db?.execSQL(AccountBookHistories.SQL_CREATE_TABLE)
        db?.execSQL(AccountBookCategories.SQL_CREATE_INDEX)
        db?.execSQL(AccountBookPayments.SQL_CREATE_INDEX)
        db?.execSQL(AccountBookHistories.SQL_CREATE_INDEX)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        Log.e(TAG, "onUpgrade: $oldVersion $newVersion")
        if (oldVersion != newVersion) {
            db?.execSQL("${AccountBookContract.SQL_DELETE_BASE_QUERY} ${AccountBookCategories.TABLE_NAME}")
            db?.execSQL("${AccountBookContract.SQL_DELETE_BASE_QUERY} ${AccountBookHistories.TABLE_NAME}")
            db?.execSQL("${AccountBookContract.SQL_DELETE_BASE_QUERY} ${AccountBookPayments.TABLE_NAME}")
            onCreate(db)
        }
    }

    //insert function test ok
    fun insertPayment(payments: Payments): Boolean {
        with(payments) {
            readableDatabase.let { db ->
                val contentValue = ContentValues().apply {
                    put(AccountBookPayments.COLUMN_NAME_PAYMENT, payment)
                }
                val ret = db.insert(AccountBookPayments.TABLE_NAME, null, contentValue)
                return ret > 0
            }
        }
    }

    fun insertCategory(categories: Categories): Boolean {
        with(categories) {
            with(readableDatabase) {
                val values = ContentValues().apply {
                    put(AccountBookCategories.COLUMN_NAME_CATEGORY, category)
                    put(AccountBookCategories.COLUMN_NAME_IS_EXPENSE, isExpense)
                    put(AccountBookCategories.COLUMN_NAME_LABEL_COLOR, labelColor)
                }
                val ret = insert(AccountBookCategories.TABLE_NAME, null, values)
                return ret > 0
            }
        }
    }

    fun insertHistory(histories: Histories) {
        with(histories) {
            with(writableDatabase) {
                val values = ContentValues().apply {
                    put(AccountBookHistories.COLUMN_NAME_DATE, date)
                    put(AccountBookHistories.COLUMN_NAME_PRICE, price)
                    put(AccountBookHistories.COLUMN_NAME_DESCRIPTION, description)
                    put(AccountBookHistories.COLUMN_NAME_PAYMENT_ID, payments.paymentId)
                    put(AccountBookHistories.COLUMN_NAME_CATEGORY_ID, categories.categoryId)
                }
                val ret = insert(AccountBookHistories.TABLE_NAME, null, values)
                Log.e(TAG, "insertHistory: $ret")
            }
        }
    }

    private fun getHistoriesList(isExpense: Int, start: Long, end: Long): List<HistoriesListItem> {
        // isExpense == 3 -> 수입 + 지출
        // isExpense == 2 -> 수입
        // isExpense == 1 -> 지출
        // isExpense == 0 -> empty
        if (isExpense == 0) return emptyList()
        with(readableDatabase) {
            val cursor = rawQuery(
                AccountBookContract.getSelectAllSqlWhereOrderBy(
                    tableName = AccountBookHistories.TABLE_NAME,
                    colName = AccountBookHistories.COLUMN_NAME_DATE,
                    order = AccountBookContract.DESC,
                    where = AccountBookHistories.COLUMN_NAME_DATE,
                    start = start,
                    end = end,
                ), null
            )
            cursor.use { c ->
                val filteredList = generateSequence { if (c.moveToNext()) c else null }
                    .map { getHistoriesListItem(it) }
                    .filterNotNull()
                    .toList()
                return when (isExpense) {
                    1 -> filteredList.filter { it.categories!!.isExpense == 1 }
                    2 -> filteredList.filter { it.categories!!.isExpense == 0 }
                    3 -> filteredList
                    else -> emptyList()
                }
            }
        }
    }

    fun getHistoriesTotalDataGroupByDay(
        isExpense: Int,
        start: Long,
        end: Long
    ): HashMap<String, CalendarItem> {
        val historiesList = getHistoriesList(isExpense, start, end)
        val groupByDateList = historiesList
            .groupBy { dateToStringYYYYMdType(it.date!!) }
        val dayCalendarItemHm = HashMap<String, CalendarItem>()
        groupByDateList.forEach { (dateString, historyList) ->
            val (year, month, day) = dateString.split(" ").map { it.toInt() }
            val dayIncomePrice = historyList.filter {
                it.categories!!.isExpense == 0
            }.sumOf { it.price }
            val dayExpensePrice = historyList.filter {
                it.categories!!.isExpense == 1
            }.sumOf { it.price }
            dayCalendarItemHm[dateString] = CalendarItem(
                year = year,
                month = month,
                day = day,
                incomePrice = dayIncomePrice,
                expensePrice = dayExpensePrice,
                totalPrice = dayIncomePrice + -1 * dayExpensePrice
            )
        }
        return dayCalendarItemHm
    }

    fun getHistoriesTotalData(isExpense: Int, start: Long, end: Long): HistoriesTotalData {
        val historiesList = getHistoriesList(isExpense, start, end)
        val groupByDateList = historiesList
            .groupBy { dateToStringMdEEType(it.date!!) }
        var totalIncoming = 0L
        var totalExpense = 0L
        val historyList = mutableListOf<HistoriesListItem>().apply {
            groupByDateList.entries.forEach { (dateString, historyListItem) ->
                val toBeHeaderItem = groupByDateList[dateString]?.first()
                var dayIncoming = 0L
                var dayExpose = 0L
                val header = HistoriesListItem()
                toBeHeaderItem?.let {
                    add(header.apply {
                        date = it.date
                        viewType = HEADER
                    })
                }
                historyListItem.forEach { history ->
                    if (history.categories!!.isExpense == 1) dayExpose += history.price
                    else dayIncoming += history.price
                    add(history.apply {
                        viewType = BODY
                    })
                }
                header.dayIncoming = dayIncoming
                header.dayExpense = dayExpose
                totalIncoming += dayIncoming
                totalExpense += dayExpose
                groupByDateList[dateString]?.last()?.isLastElement = true
            }
        }.toList()
        return HistoriesTotalData(
            totalIncome = totalIncoming,
            totalExpense = totalExpense,
            historyList = historyList
        )
    }

    @SuppressLint("Range")
    fun getHistoriesListItem(cursor: Cursor): HistoriesListItem? = try {
        cursor.run {
            val id = getInt(getColumnIndex(AccountBookHistories.COLUMN_NAME_ID))
            val date = getLong(getColumnIndex(AccountBookHistories.COLUMN_NAME_DATE))
            val price = getLong(getColumnIndex(AccountBookHistories.COLUMN_NAME_PRICE))
            val description =
                getString(getColumnIndex(AccountBookHistories.COLUMN_NAME_DESCRIPTION))
            val paymentId = getInt(getColumnIndex(AccountBookHistories.COLUMN_NAME_PAYMENT_ID))
            val categoryId = getInt(getColumnIndex(AccountBookHistories.COLUMN_NAME_CATEGORY_ID))
            HistoriesListItem(
                id = id,
                date = Date(date),
                price = price,
                description = description,
                payments = getPaymentsById(paymentId) ?: Payments(0, ""),
                categories = getCategoriesById(categoryId) ?: Categories(0, "", "", 0)
            )
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }

    fun getAllCategories(isExpense: Int): List<Categories> {
        with(readableDatabase) {
            val cursor = rawQuery(
                AccountBookCategories.getSelectSqlWhereIsExpense(isExpense),
                null
            )
            return cursor.use { c ->
                generateSequence {
                    if (c.moveToNext()) c else null
                }.map {
                    getCategories(it)
                }.filterNotNull().toList()
            }
        }
    }

    fun getSumPrice(isExpense: Int, start: Long, end: Long): Long {
        with(readableDatabase) {
            val cursor = rawQuery(
                getSumPriceSqlWhere(isExpense, start, end),
                null
            )
            return cursor.use { c ->
                c.moveToFirst()
                c.getLong(0)
            }
        }
    }

    fun getAllPayments(): List<Payments> {
        with(readableDatabase) {
            val cursor = rawQuery(
                AccountBookContract.getSelectAllSql(AccountBookPayments.TABLE_NAME),
                null
            )
            return cursor.use { c ->
                generateSequence {
                    if (c.moveToNext()) c else null
                }.map {
                    getPayments(it)
                }.filterNotNull().toList()
            }
        }
    }

    @SuppressLint("Range")
    private fun getPayments(cursor: Cursor): Payments? = try {
        cursor.run {
            val id = getInt(getColumnIndex(AccountBookPayments.COLUMN_NAME_ID))
            val payment = getString(getColumnIndex(AccountBookPayments.COLUMN_NAME_PAYMENT))
            Payments(paymentId = id, payment = payment)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }

    @SuppressLint("Range")
    private fun getCategories(cursor: Cursor): Categories? = try {
        cursor.run {
            val id = getInt(getColumnIndex(AccountBookCategories.COLUMN_NAME_ID))
            val category = getString(getColumnIndex(AccountBookCategories.COLUMN_NAME_CATEGORY))
            val isExpense = getInt(getColumnIndex(AccountBookCategories.COLUMN_NAME_IS_EXPENSE))
            val labelColor =
                getString(getColumnIndex(AccountBookCategories.COLUMN_NAME_LABEL_COLOR))
            Categories(
                categoryId = id,
                category = category,
                isExpense = isExpense,
                labelColor = labelColor
            )
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }

    @SuppressLint("Range")
    private fun getHistories(cursor: Cursor): Histories? = try {
        cursor.run {
            val id = getInt(getColumnIndex(AccountBookHistories.COLUMN_NAME_ID))
            val date = getLong(getColumnIndex(AccountBookHistories.COLUMN_NAME_DATE))
            val price = getLong(getColumnIndex(AccountBookHistories.COLUMN_NAME_PRICE))
            val description =
                getString(getColumnIndex(AccountBookHistories.COLUMN_NAME_DESCRIPTION))
            val paymentId = getInt(getColumnIndex(AccountBookHistories.COLUMN_NAME_PAYMENT_ID))
            val categoryId = getInt(getColumnIndex(AccountBookHistories.COLUMN_NAME_CATEGORY_ID))
            Histories(
                id = id,
                date = date,
                price = price,
                description = description,
                payments = getPaymentsById(paymentId) ?: Payments(0, ""),
                categories = getCategoriesById(categoryId) ?: Categories(0, "", "", 0)
            )
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }

    @SuppressLint("Range")
    fun getCategoriesById(id: Int): Categories? = try {
        with(readableDatabase) {
            val cursor =
                rawQuery(AccountBookCategories.SQL_SELECT_BY_ID_QUERY, arrayOf(id.toString()))
            cursor.run {
                use {
                    moveToFirst()
                    Categories(
                        categoryId = getInt(getColumnIndex(AccountBookCategories.COLUMN_NAME_ID)),
                        category = getString(getColumnIndex(AccountBookCategories.COLUMN_NAME_CATEGORY)),
                        isExpense = getInt(getColumnIndex(AccountBookCategories.COLUMN_NAME_IS_EXPENSE)),
                        labelColor = getString(getColumnIndex(AccountBookCategories.COLUMN_NAME_LABEL_COLOR))
                    )
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }

    @SuppressLint("Range")
    fun getPaymentsById(id: Int): Payments? = try {
        with(readableDatabase) {
            val cursor =
                rawQuery(AccountBookPayments.SQL_SELECT_BY_ID_QUERY, arrayOf(id.toString()))
            cursor.run {
                use {
                    moveToFirst()
                    Payments(
                        paymentId = getInt(getColumnIndex(AccountBookPayments.COLUMN_NAME_ID)),
                        payment = getString(getColumnIndex(AccountBookPayments.COLUMN_NAME_PAYMENT))
                    )
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }

    //update function -> test ok
    fun updatePayment(payments: Payments) {
        with(payments) {
            with(readableDatabase) {
                val values = ContentValues().apply {
                    put(AccountBookPayments.COLUMN_NAME_PAYMENT, payment)
                }
                update(
                    AccountBookPayments.TABLE_NAME,
                    values,
                    "${AccountBookPayments.COLUMN_NAME_ID} = ?",
                    arrayOf(paymentId.toString())
                )
            }
        }
    }

    // test ok
    fun updateCategory(categories: Categories) {
        with(categories) {
            with(readableDatabase) {
                val values = ContentValues().apply {
                    put(AccountBookCategories.COLUMN_NAME_CATEGORY, category)
                    put(AccountBookCategories.COLUMN_NAME_LABEL_COLOR, labelColor)
                }
                update(
                    AccountBookCategories.TABLE_NAME,
                    values,
                    "${AccountBookCategories.COLUMN_NAME_ID} = ?",
                    arrayOf(categoryId.toString())
                )
            }
        }
    }

    fun updateHistory(histories: Histories) {
        with(histories) {
            with(readableDatabase) {
                val values = ContentValues().apply {
                    put(AccountBookHistories.COLUMN_NAME_DATE, date)
                    put(AccountBookHistories.COLUMN_NAME_PRICE, price)
                    put(AccountBookHistories.COLUMN_NAME_DESCRIPTION, description)
                    put(AccountBookHistories.COLUMN_NAME_PAYMENT_ID, payments.paymentId)
                    put(AccountBookHistories.COLUMN_NAME_CATEGORY_ID, categories.categoryId)
                }
                val ret = update(
                    AccountBookHistories.TABLE_NAME,
                    values,
                    "${AccountBookHistories.COLUMN_NAME_ID} = ?",
                    arrayOf(id.toString())
                )
                Log.e(TAG, "updateHistory: update ret $ret")
            }
        }
    }

    //delete function
    fun deleteAll(tableName: String) {
        with(readableDatabase) {
            execSQL(AccountBookContract.getDeleteAllSql(tableName))
        }
    }

    fun deleteHistory(id: Int) {
        with(readableDatabase) {
            delete(
                AccountBookHistories.TABLE_NAME,
                "${AccountBookHistories.COLUMN_NAME_ID} = ?",
                arrayOf(id.toString())
            )
        }
    }


}