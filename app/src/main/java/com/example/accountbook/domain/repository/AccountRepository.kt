package com.example.accountbook.domain.repository

import com.example.accountbook.data.model.Categories
import com.example.accountbook.data.model.Histories
import com.example.accountbook.data.model.Payments
import com.example.accountbook.domain.model.CalendarItem
import com.example.accountbook.domain.model.HistoriesListItem
import com.example.accountbook.domain.model.HistoriesTotalData

interface AccountRepository {

    suspend fun getHistoriesTotalData(isExpense: Int, start: Long, end: Long): HistoriesTotalData
    suspend fun getAllCategories(isExpense: Int): List<Categories>
    suspend fun getAllPayments(): List<Payments>
    suspend fun deleteAll(tableName: String)
    suspend fun getSumPrice(isExpense: Int, start: Long, end: Long): Long
    suspend fun insertHistory(historiesListItem: HistoriesListItem)
    suspend fun updateHistory(historiesListItem: HistoriesListItem)
    suspend fun deleteHistory(id: Int)
    suspend fun getCalendarItems(
        isExpense: Int = 3,
        start: Long,
        end: Long,
        year: Int,
        month: Int,
        day: Int
    ): List<CalendarItem>
}