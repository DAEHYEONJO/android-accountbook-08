package com.example.accountbook.data.datasource

import android.annotation.SuppressLint
import android.content.ContentValues
import android.database.Cursor
import com.example.accountbook.data.db.AccountBookDbHelper
import com.example.accountbook.data.model.Categories
import com.example.accountbook.data.utils.AccountBookCategories
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryDataSource @Inject constructor(
    private val dbHelper: AccountBookDbHelper
) {

    fun insertCategories(categories: Categories): Boolean {
        with(categories) {
            with(dbHelper.readableDatabase) {
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

    fun getAllCategories(isExpense: Int): List<Categories> {
        with(dbHelper.readableDatabase) {
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

    fun updateCategory(categories: Categories) {
        with(categories) {
            with(dbHelper.readableDatabase) {
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

}