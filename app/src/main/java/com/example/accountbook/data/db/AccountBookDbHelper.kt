package com.example.accountbook.data.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.accountbook.data.utils.AccountBookCategories
import com.example.accountbook.data.utils.AccountBookContract
import com.example.accountbook.data.utils.AccountBookHistories
import com.example.accountbook.data.utils.AccountBookPayments
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

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
        if (oldVersion != newVersion) {
            db?.execSQL("${AccountBookContract.SQL_DELETE_BASE_QUERY} ${AccountBookCategories.TABLE_NAME}")
            db?.execSQL("${AccountBookContract.SQL_DELETE_BASE_QUERY} ${AccountBookHistories.TABLE_NAME}")
            db?.execSQL("${AccountBookContract.SQL_DELETE_BASE_QUERY} ${AccountBookPayments.TABLE_NAME}")
            onCreate(db)
        }
    }

}