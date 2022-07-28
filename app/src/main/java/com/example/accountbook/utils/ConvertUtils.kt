package com.example.accountbook.utils

import java.text.SimpleDateFormat
import java.util.*

fun dateToStringMdEEType(date: Date): String {
    val pattern = "M월 d일 EE"
    val formatter = SimpleDateFormat(pattern)
    return formatter.format(date)
}

fun dateToYearMonth(date: Date): List<Int> {
    val pattern = "yyyy MM"
    val formatter = SimpleDateFormat(pattern)
    return formatter.format(date)
        .split(" ")
        .map { it.toInt() }
}

fun dateToStringYYYYMdEEType(date: Date): String {
    val pattern = "yyyy. M. d E요일"
    val formatter = SimpleDateFormat(pattern)
    return formatter.format(date)
}