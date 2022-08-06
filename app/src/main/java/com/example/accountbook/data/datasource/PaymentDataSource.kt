package com.example.accountbook.data.datasource

import android.annotation.SuppressLint
import android.content.ContentValues
import android.database.Cursor
import com.example.accountbook.data.db.AccountBookDbHelper
import com.example.accountbook.data.model.Payments
import com.example.accountbook.data.utils.AccountBookContract
import com.example.accountbook.data.utils.AccountBookPayments
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymentDataSource @Inject constructor(
    private val dbHelper: AccountBookDbHelper
) {

    fun insertPayment(payments: Payments): Boolean {
        with(payments) {
            dbHelper.readableDatabase.let { db ->
                val contentValue = ContentValues().apply {
                    put(AccountBookPayments.COLUMN_NAME_PAYMENT, payment)
                }
                val ret = db.insert(AccountBookPayments.TABLE_NAME, null, contentValue)
                return ret > 0
            }
        }
    }

    fun getAllPayments(): List<Payments> {
        with(dbHelper.readableDatabase) {
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

    fun updatePayment(payments: Payments) {
        with(payments) {
            with(dbHelper.readableDatabase) {
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
}