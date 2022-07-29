package com.example.accountbook.domain.model


data class HistoriesTotalData(
    val totalIncome: Int,
    val totalExpense: Int,
    val historyList: List<HistoriesListItem>
)
