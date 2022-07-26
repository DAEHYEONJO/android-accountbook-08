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
import com.example.accountbook.data.utils.AccountBookHistories
import com.example.accountbook.data.utils.AccountBookPayments

class AccountBookDbHelper(context: Context) : SQLiteOpenHelper(
    context, AccountBookContract.DB_NAME, null, AccountBookContract.DB_VERSION
) {

    companion object {
        const val TAG = "AccountBookDbHelper"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        Log.d(TAG, "onCreate: ")

        db?.execSQL(AccountBookContract.SQL_ACTIVE_FOREIGN_KEY)
        db?.execSQL(AccountBookCategories.SQL_CREATE_TABLE)
        db?.execSQL(AccountBookPayments.SQL_CREATE_TABLE)
        db?.execSQL(AccountBookHistories.SQL_CREATE_TABLE)
        db?.execSQL(AccountBookCategories.SQL_CREATE_INDEX)
        db?.execSQL(AccountBookPayments.SQL_CREATE_INDEX)
        db?.execSQL(AccountBookHistories.SQL_CREATE_INDEX)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        Log.d(TAG, "onUpgrade: ")
        if (oldVersion != newVersion) {
            db?.execSQL("${AccountBookContract.SQL_DELETE_BASE_QUERY} ${AccountBookCategories.TABLE_NAME}")
            db?.execSQL("${AccountBookContract.SQL_DELETE_BASE_QUERY} ${AccountBookHistories.TABLE_NAME}")
            db?.execSQL("${AccountBookContract.SQL_DELETE_BASE_QUERY} ${AccountBookPayments.TABLE_NAME}")
            onCreate(db)
        }
    }

    //insert function test ok
    fun insertPayment(payments: Payments) {
        with(payments) {
            if (getPaymentCount(payment) != 0) return

            writableDatabase.use { db ->
                val contentValue = ContentValues().apply {
                    put(AccountBookPayments.COLUMN_NAME_PAYMENT, payment)
                }
                db.insert(AccountBookPayments.TABLE_NAME, null, contentValue)
            }
        }
    }

    fun insertCategory(categories: Categories) {
        with(categories) {
            if (getCategoryCount(category) != 0) return

            writableDatabase.use { db ->
                val values = ContentValues().apply {
                    put(AccountBookCategories.COLUMN_NAME_CATEGORY, category)
                    put(AccountBookCategories.COLUMN_NAME_IS_EXPENSE, isExpense)
                    put(AccountBookCategories.COLUMN_NAME_LABEL_COLOR, labelColor)
                }
                db.insert(AccountBookCategories.TABLE_NAME, null, values)
            }
        }
    }

    fun insertHistory(histories: Histories) {
        with(histories) {
            writableDatabase.use { db ->
                val values = ContentValues().apply {
                    put(AccountBookHistories.COLUMN_NAME_DATE, date)
                    put(AccountBookHistories.COLUMN_NAME_PRICE, price)
                    put(AccountBookHistories.COLUMN_NAME_DESCRIPTION, description)
                    put(AccountBookHistories.COLUMN_NAME_PAYMENT_ID, payments.paymentId)
                    put(AccountBookHistories.COLUMN_NAME_CATEGORY_ID, categories.categoryId)
                }
                db.insert(AccountBookHistories.TABLE_NAME, null, values)
            }
        }
    }

    //find function
    private fun getPaymentCount(payment: String): Int {
        readableDatabase.use { db ->
            val cursor = db.rawQuery(AccountBookPayments.SQL_SELECT_BY_PAYMENT_QUERY, arrayOf(payment))
            return cursor.use { c ->
                c?.count?:0
            }
        }
    }

    private fun getCategoryCount(category: String): Int {
        readableDatabase.use { db ->
            val cursor = db.rawQuery(AccountBookCategories.SQL_SELECT_BY_CATEGORY_QUERY, arrayOf(category))
            return cursor.use { c ->
                c?.count?:0
            }
        }
    }

    fun getAllHistories(): List<Histories> {
        readableDatabase.use { db ->
            val cursor = db.rawQuery(AccountBookContract.getSelectAllSql(AccountBookHistories.TABLE_NAME), null)
            return cursor.use { cursor ->
                generateSequence {
                    if (cursor.moveToNext()) cursor else null
                }.map {
                    getHistories(it)
                }.filterNotNull().toList()
            }
        }
    }

    fun getAllCategories(): List<Categories> {
        readableDatabase.use { db ->
            val cursor = db.rawQuery(AccountBookContract.getSelectAllSql(AccountBookCategories.TABLE_NAME), null)
            return cursor.use { c ->
                generateSequence {
                    if (c.moveToNext()) c else null
                }.map {
                    getCategories(it)
                }.filterNotNull().toList()
            }
        }
    }

    fun getAllPayments(): List<Payments> {
        readableDatabase.use { db ->
            val cursor = db.rawQuery(AccountBookContract.getSelectAllSql(AccountBookPayments.TABLE_NAME), null)
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
    }catch (e: Exception){
        e.printStackTrace()
        null
    }

    @SuppressLint("Range")
    private fun getCategories(cursor: Cursor): Categories? = try {
        cursor.run {
            val id = getInt(getColumnIndex(AccountBookCategories.COLUMN_NAME_ID))
            val category = getString(getColumnIndex(AccountBookCategories.COLUMN_NAME_CATEGORY))
            val isExpense = getInt(getColumnIndex(AccountBookCategories.COLUMN_NAME_IS_EXPENSE))
            val labelColor = getString(getColumnIndex(AccountBookCategories.COLUMN_NAME_LABEL_COLOR))
            Categories(categoryId = id, category = category, isExpense = isExpense, labelColor = labelColor)
        }
    }catch (e: Exception){
        e.printStackTrace()
        null
    }

    @SuppressLint("Range")
    private fun getHistories(cursor: Cursor): Histories? = try {
        cursor.run {
            val id = getInt(getColumnIndex(AccountBookHistories.COLUMN_NAME_ID))
            val date = getLong(getColumnIndex(AccountBookHistories.COLUMN_NAME_DATE))
            val price = getInt(getColumnIndex(AccountBookHistories.COLUMN_NAME_PRICE))
            val description = getString(getColumnIndex(AccountBookHistories.COLUMN_NAME_DESCRIPTION))
            val paymentId = getInt(getColumnIndex(AccountBookHistories.COLUMN_NAME_PAYMENT_ID))
            val categoryId = getInt(getColumnIndex(AccountBookHistories.COLUMN_NAME_CATEGORY_ID))
            Histories(
                id = id,
                date = date,
                price = price,
                description = description,
                payments = getPaymentsById(paymentId)?: Payments(0,""),
                categories = getCategoriesById(categoryId)?: Categories(0,"","",0)
            )
        }
    }catch (e: Exception){
        e.printStackTrace()
        null
    }

    @SuppressLint("Range")
    fun getCategoriesById(id: Int): Categories? = try{
        readableDatabase.use { db ->
            val cursor = db.rawQuery(AccountBookCategories.SQL_SELECT_BY_ID_QUERY, arrayOf(id.toString()))
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
    }catch (e: Exception){
        e.printStackTrace()
        null
    }

    @SuppressLint("Range")
    fun getPaymentsById(id: Int): Payments? = try {
        readableDatabase.use { db ->
            val cursor = db.rawQuery(AccountBookPayments.SQL_SELECT_BY_ID_QUERY, arrayOf(id.toString()))
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
    }catch (e: Exception){
        e.printStackTrace()
        null
    }

    //update function -> test ok
    fun updatePayment(payments: Payments) {
        with(payments) {
            writableDatabase.use { db ->
                val values = ContentValues().apply {
                    put(AccountBookPayments.COLUMN_NAME_PAYMENT, payment)
                }
                db.update(
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
            writableDatabase.use { db ->
                val values = ContentValues().apply {
                    put(AccountBookCategories.COLUMN_NAME_CATEGORY, category)
                    put(AccountBookCategories.COLUMN_NAME_LABEL_COLOR, labelColor)
                }
                db.update(
                    AccountBookCategories.TABLE_NAME,
                    values,
                    "${AccountBookCategories.COLUMN_NAME_ID} = ?",
                    arrayOf(categoryId.toString())
                )
            }
        }
    }

    fun updateHistory(histories: Histories){
        with(histories){
            writableDatabase.use { db ->
                val values = ContentValues().apply {
                    put(AccountBookHistories.COLUMN_NAME_DATE, date)
                    put(AccountBookHistories.COLUMN_NAME_PRICE, price)
                    put(AccountBookHistories.COLUMN_NAME_DESCRIPTION, description)
                    put(AccountBookHistories.COLUMN_NAME_PAYMENT_ID, payments.paymentId)
                    put(AccountBookHistories.COLUMN_NAME_CATEGORY_ID, categories.categoryId)
                }
                db.update(
                    AccountBookHistories.TABLE_NAME,
                    values,
                    "${AccountBookHistories.COLUMN_NAME_ID} = ?",
                    arrayOf(id.toString())
                )
            }
        }
    }

    //delete function
    fun deleteAll(tableName: String) {
        writableDatabase.use { db ->
            db.execSQL(AccountBookContract.getDeleteAllSql(tableName))
        }
    }

    fun deleteHistory(id: Int) {
        writableDatabase.use { db ->
            db.delete(
                AccountBookHistories.TABLE_NAME,
                "${AccountBookHistories.COLUMN_NAME_ID} = ?",
                arrayOf(id.toString())
            )
        }
    }


}