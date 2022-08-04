package com.example.accountbook.data.model

data class Categories(
    val categoryId: Int = 0,
    val category: String,
    val labelColor: String = "#000000",
    val isExpense: Int = 0
)
