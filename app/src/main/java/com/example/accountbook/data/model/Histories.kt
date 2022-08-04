package com.example.accountbook.data.model

data class Histories(
    val id: Int = 0,
    val date: Long,
    val price: Long,
    val description: String,
    val payments: Payments,
    val categories: Categories
)
