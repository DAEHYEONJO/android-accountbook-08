package com.example.accountbook.domain.repository

import com.example.accountbook.data.model.Categories
import com.example.accountbook.data.model.Histories
import com.example.accountbook.data.model.Payments
import com.example.accountbook.domain.model.CalendarItem
import com.example.accountbook.domain.model.HistoriesListItem
import com.example.accountbook.domain.model.HistoriesTotalData
import com.example.accountbook.domain.model.StatisticsItem

interface AccountRepository {

    suspend fun getHistoriesTotalData(isExpense: Int, start: Long, end: Long): HistoriesTotalData
    suspend fun getAllCategories(isExpense: Int): List<Categories>
    suspend fun getAllPayments(): List<Payments>
    suspend fun deleteAll(tableName: String)
    suspend fun getSumPrice(isExpense: Int, start: Long, end: Long): Long
    suspend fun insertHistory(historiesListItem: HistoriesListItem)
    suspend fun updateHistory(historiesListItem: HistoriesListItem)
    suspend fun deleteHistory(id: Int)
    suspend fun getCalendarHashMap(
        isExpense: Int = 3,
        start: Long,
        end: Long
    ): HashMap<String, CalendarItem>
    suspend fun getStatisticsItems(
        isExpense: Int = 1,
        start: Long,
        end: Long
    ): List<StatisticsItem>
    suspend fun insertPayments(payments: Payments): Boolean
    suspend fun insertCategories(categories: Categories): Boolean
    suspend fun updateCategories(categories: Categories)
    suspend fun updatePayments(payments: Payments)
}