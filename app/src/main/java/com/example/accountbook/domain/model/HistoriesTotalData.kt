package com.example.accountbook.domain.model


data class HistoriesTotalData(
    val totalIncome: Long,
    val totalExpense: Long,
    val historyList: List<HistoriesListItem>
)
