package com.example.accountbook.data.datasource

import android.annotation.SuppressLint
import android.content.ContentValues
import android.database.Cursor
import com.example.accountbook.data.db.AccountBookDbHelper
import com.example.accountbook.data.model.Categories
import com.example.accountbook.data.model.Histories
import com.example.accountbook.data.model.Payments
import com.example.accountbook.data.utils.AccountBookCategories
import com.example.accountbook.data.utils.AccountBookContract
import com.example.accountbook.data.utils.AccountBookHistories
import com.example.accountbook.data.utils.AccountBookPayments
import com.example.accountbook.domain.model.*
import com.example.accountbook.utils.dateToStringMdEEType
import com.example.accountbook.utils.dateToStringYYYYMdType
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.HashMap

@Singleton
class HistoryDataSource @Inject constructor(
    private val dbHelper: AccountBookDbHelper
) {

    fun insertHistory(histories: Histories) {
        with(histories) {
            with(dbHelper.writableDatabase) {
                val values = ContentValues().apply {
                    put(AccountBookHistories.COLUMN_NAME_DATE, date)
                    put(AccountBookHistories.COLUMN_NAME_PRICE, price)
                    put(AccountBookHistories.COLUMN_NAME_DESCRIPTION, description)
                    put(AccountBookHistories.COLUMN_NAME_PAYMENT_ID, payments.paymentId)
                    put(AccountBookHistories.COLUMN_NAME_CATEGORY_ID, categories.categoryId)
                }
                insert(AccountBookHistories.TABLE_NAME, null, values)
            }
        }
    }

    fun getHistoriesList(isExpense: Int, start: Long, end: Long): List<HistoriesListItem> {
        if (isExpense == 0) return emptyList()
        with(dbHelper.readableDatabase) {
            val cursor = rawQuery(
                AccountBookContract.getSelectAllSqlJoin(
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
            val paymentPayment = getString(getColumnIndex(AccountBookPayments.COLUMN_NAME_PAYMENT))
            val categoryId = getInt(getColumnIndex(AccountBookHistories.COLUMN_NAME_CATEGORY_ID))
            val categoryName = getString(getColumnIndex(AccountBookCategories.COLUMN_NAME_CATEGORY))
            val categoryIsExpense = getInt(getColumnIndex(AccountBookCategories.COLUMN_NAME_IS_EXPENSE))
            val categoryLabelColor = getString(getColumnIndex(AccountBookCategories.COLUMN_NAME_LABEL_COLOR))
            HistoriesListItem(
                id = id,
                date = Date(date),
                price = price,
                description = description,
                payments = Payments(paymentId, paymentPayment),
                categories = Categories(categoryId, categoryName, categoryLabelColor, categoryIsExpense)
            )
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }

    fun updateHistory(histories: Histories) {
        with(histories) {
            with(dbHelper.readableDatabase) {
                val values = ContentValues().apply {
                    put(AccountBookHistories.COLUMN_NAME_DATE, date)
                    put(AccountBookHistories.COLUMN_NAME_PRICE, price)
                    put(AccountBookHistories.COLUMN_NAME_DESCRIPTION, description)
                    put(AccountBookHistories.COLUMN_NAME_PAYMENT_ID, payments.paymentId)
                    put(AccountBookHistories.COLUMN_NAME_CATEGORY_ID, categories.categoryId)
                }
                update(
                    AccountBookHistories.TABLE_NAME,
                    values,
                    "${AccountBookHistories.COLUMN_NAME_ID} = ?",
                    arrayOf(id.toString())
                )
            }
        }
    }

    fun deleteHistory(id: Int) {
        with(dbHelper.readableDatabase) {
            delete(
                AccountBookHistories.TABLE_NAME,
                "${AccountBookHistories.COLUMN_NAME_ID} = ?",
                arrayOf(id.toString())
            )
        }
    }

    fun getSumPrice(isExpense: Int, start: Long, end: Long): Long {
        with(dbHelper.readableDatabase) {
            val cursor = rawQuery(
                AccountBookContract.getSumPriceSqlWhere(
                    isExpense,
                    start,
                    end
                ),
                null
            )
            return cursor.use { c ->
                c.moveToFirst()
                c.getLong(0)
            }
        }
    }
}