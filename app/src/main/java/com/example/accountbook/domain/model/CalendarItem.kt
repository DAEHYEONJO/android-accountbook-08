package com.example.accountbook.domain.model

import java.util.*

data class CalendarItem(
    val date: Date? = null,
    val year: Int = 0,
    val month: Int = 0,
    val day: Int = 0,
    var incomePrice: Long = 0L,
    var expensePrice: Long = 0L,
    var totalPrice: Long = 0L,
    val isThisMonth: Boolean = true,
    val isSaturday: Boolean = false,
    val isToday: Boolean = false
) {

}
