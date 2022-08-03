package com.example.accountbook.data.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.accountbook.data.db.AccountBookDbHelper
import com.example.accountbook.data.mapper.DomainToPresenterMapper
import com.example.accountbook.data.mapper.PresenterToDomainMapper
import com.example.accountbook.data.model.Categories
import com.example.accountbook.data.model.Payments
import com.example.accountbook.di.IoDispatcher
import com.example.accountbook.domain.model.CalendarItem
import com.example.accountbook.domain.model.HistoriesListItem
import com.example.accountbook.domain.model.HistoriesTotalData
import com.example.accountbook.domain.model.StatisticsItem
import com.example.accountbook.domain.repository.AccountRepository
import com.example.accountbook.utils.dateToYearMonthDay
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@Singleton
class AccountRepositoryImpl @Inject constructor(
    private val dbHelper: AccountBookDbHelper,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val presenterToDomainMapper: PresenterToDomainMapper,
    private val domainToPresenterMapper: DomainToPresenterMapper
) : AccountRepository {

    companion object {
        const val TAG = "AccountRepositoryImpl"
    }

    override suspend fun getHistoriesTotalData(
        isExpense: Int,
        start: Long,
        end: Long
    ): HistoriesTotalData {
        return withContext(dispatcher) {
            dbHelper.getHistoriesTotalData(isExpense, start, end)
        }
    }

    override suspend fun getAllCategories(isExpense: Int): List<Categories> {
        return withContext(dispatcher) {
            dbHelper.getAllCategories(isExpense)
        }
    }

    override suspend fun getAllPayments(): List<Payments> {
        return withContext(dispatcher) {
            dbHelper.getAllPayments()
        }
    }

    override suspend fun deleteAll(tableName: String) {
        withContext(dispatcher) {
            dbHelper.deleteAll(tableName)
        }
    }

    override suspend fun getSumPrice(isExpense: Int, start: Long, end: Long): Long {
        return withContext(dispatcher) {
            dbHelper.getSumPrice(isExpense, start, end)
        }
    }

    override suspend fun insertHistory(historiesListItem: HistoriesListItem) {
        withContext(dispatcher) {
            dbHelper.insertHistory(presenterToDomainMapper.getHistories(historiesListItem))
        }
    }

    override suspend fun updateHistory(historiesListItem: HistoriesListItem) {
        withContext(dispatcher) {
            dbHelper.updateHistory(presenterToDomainMapper.getHistories(historiesListItem))
        }
    }

    override suspend fun deleteHistory(id: Int) {
        withContext(dispatcher) {
            dbHelper.deleteHistory(id)
        }
    }

    override suspend fun getCalendarHashMap(
        isExpense: Int,
        start: Long,
        end: Long
    ): HashMap<String, CalendarItem> {
        return withContext(dispatcher){
            dbHelper.getHistoriesTotalDataGroupByDay(
                isExpense, start, end
            )
        }
    }

    override suspend fun getStatisticsItems(
        isExpense: Int,
        start: Long,
        end: Long
    ): List<StatisticsItem> {
        return withContext(dispatcher){

            emptyList()
        }
    }


}