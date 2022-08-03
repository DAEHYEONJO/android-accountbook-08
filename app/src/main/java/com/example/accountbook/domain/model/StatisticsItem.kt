package com.example.accountbook.domain.model

import com.example.accountbook.data.model.Categories

data class StatisticsItem(
    val categories: Categories,
    val expensePrice: Long,
    val percentage: Int,
    var isLast: Boolean = false
)
