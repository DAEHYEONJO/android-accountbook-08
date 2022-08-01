package com.example.accountbook.data.repository

import android.util.Log
import com.example.accountbook.data.db.AccountBookDbHelper
import com.example.accountbook.data.mapper.PresenterToDomainMapper
import com.example.accountbook.data.model.Categories
import com.example.accountbook.data.model.Histories
import com.example.accountbook.data.model.Payments
import com.example.accountbook.di.IoDispatcher
import com.example.accountbook.domain.model.HistoriesListItem
import com.example.accountbook.domain.model.HistoriesTotalData
import com.example.accountbook.domain.repository.AccountRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepositoryImpl @Inject constructor(
    private val dbHelper: AccountBookDbHelper,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val mapper: PresenterToDomainMapper
) : AccountRepository {

    companion object{
        const val TAG = "AccountRepositoryImpl"
    }

    override suspend fun getHistoriesTotalData(isExpense: Int, start: Long, end: Long): HistoriesTotalData {
        return withContext(dispatcher){
            dbHelper.getHistoriesTotalData(isExpense, start, end)
        }
    }

    override suspend fun getAllCategories(isExpense: Int): List<Categories> {
        return withContext(dispatcher){
            dbHelper.getAllCategories(isExpense)
        }
    }

    override suspend fun getAllPayments(): List<Payments> {
        return withContext(dispatcher){
            dbHelper.getAllPayments()
        }
    }

    override suspend fun deleteAll(tableName: String) {
        withContext(dispatcher){
            dbHelper.deleteAll(tableName)
        }
    }

    override suspend fun getSumPrice(isExpense: Int, start: Long, end: Long): Int {
        return withContext(dispatcher){
            dbHelper.getSumPrice(isExpense,start,end)
        }
    }

    override suspend fun insertHistory(historiesListItem: HistoriesListItem) {
        withContext(dispatcher){
            Log.e(TAG, "AccountRepositoryImpl insertHistory: $historiesListItem", )
            dbHelper.insertHistory(mapper.getHistories(historiesListItem))
        }
    }
}