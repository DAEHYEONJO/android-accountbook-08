package com.example.accountbook.data.repository

import com.example.accountbook.data.datasource.CategoryDataSource
import com.example.accountbook.data.datasource.HistoryDataSource
import com.example.accountbook.data.datasource.PaymentDataSource
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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.HashMap

@Singleton
class AccountRepositoryImpl @Inject constructor(
    private val historyDataSource: HistoryDataSource,
    private val categoryDataSource: CategoryDataSource,
    private val paymentDataSource: PaymentDataSource,
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
            historyDataSource.getHistoriesTotalData(isExpense, start, end)
        }
    }

    override suspend fun getAllCategories(isExpense: Int): List<Categories> {
        return withContext(dispatcher) {
            categoryDataSource.getAllCategories(isExpense)
        }
    }

    override suspend fun getAllPayments(): List<Payments> {
        return withContext(dispatcher) {
            paymentDataSource.getAllPayments()
        }
    }

    override suspend fun getSumPrice(isExpense: Int, start: Long, end: Long): Long {
        return withContext(dispatcher) {
            historyDataSource.getSumPrice(isExpense, start, end)
        }
    }

    override suspend fun insertHistory(historiesListItem: HistoriesListItem) {
        withContext(dispatcher) {
            historyDataSource.insertHistory(presenterToDomainMapper.getHistories(historiesListItem))
        }
    }

    override suspend fun updateHistory(historiesListItem: HistoriesListItem) {
        withContext(dispatcher) {
            historyDataSource.updateHistory(presenterToDomainMapper.getHistories(historiesListItem))
        }
    }

    override suspend fun deleteHistory(id: Int) {
        withContext(dispatcher) {
            historyDataSource.deleteHistory(id)
        }
    }

    override suspend fun getCalendarHashMap(
        isExpense: Int,
        start: Long,
        end: Long
    ): HashMap<String, CalendarItem> {
        return withContext(dispatcher) {
            historyDataSource.getHistoriesTotalDataGroupByDay(
                isExpense, start, end
            )
        }
    }

    override suspend fun getStatisticsItems(
        isExpense: Int,
        start: Long,
        end: Long
    ): List<StatisticsItem> {
        return withContext(dispatcher) {
            historyDataSource.getHistoriesList(isExpense, start, end)
                .map { domainToPresenterMapper.getStatisticsItem(it) }
        }
    }

    override suspend fun insertPayments(payments: Payments): Boolean {
        return withContext(dispatcher){
            paymentDataSource.insertPayment(payments)
        }
    }

    override suspend fun insertCategories(categories: Categories): Boolean {
        return withContext(dispatcher){
            categoryDataSource.insertCategories(categories)
        }
    }

    override suspend fun updateCategories(categories: Categories) {
        return withContext(dispatcher){
            categoryDataSource.updateCategory(categories)
        }
    }

    override suspend fun updatePayments(payments: Payments) {
        return withContext(dispatcher){
            paymentDataSource.updatePayment(payments)
        }
    }
}