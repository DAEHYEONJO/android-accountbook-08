package com.example.accountbook.domain.repository

import com.example.accountbook.data.model.Categories
import com.example.accountbook.data.model.Payments
import com.example.accountbook.domain.model.HistoriesTotalData

interface AccountRepository {

    suspend fun getHistoriesTotalData(isExpense: Int, start: Long, end: Long): HistoriesTotalData
    suspend fun getAllCategories(): List<Categories>
    suspend fun getAllPayments(): List<Payments>

    suspend fun deleteAll(tableName: String)

}