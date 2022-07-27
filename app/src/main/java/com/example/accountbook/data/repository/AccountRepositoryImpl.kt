package com.example.accountbook.data.repository

import com.example.accountbook.data.db.AccountBookDbHelper
import com.example.accountbook.data.model.Categories
import com.example.accountbook.data.model.Histories
import com.example.accountbook.data.model.Payments
import com.example.accountbook.di.IoDispatcher
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
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : AccountRepository {
    override suspend fun getHistoriesTotalData(): HistoriesTotalData {
        return withContext(dispatcher){
            dbHelper.getHistoriesTotalData()
        }
    }

    override suspend fun getAllCategories(): List<Categories> {
        return withContext(dispatcher){
            dbHelper.getAllCategories()
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


}