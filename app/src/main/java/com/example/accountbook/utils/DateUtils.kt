package com.example.accountbook.utils

import java.text.SimpleDateFormat
import java.util.*

fun dateToStringMdEEType(date: Date): String {
    val pattern = "M월 d일 E"
    val formatter = SimpleDateFormat(pattern, Locale.KOREAN)
    return formatter.format(date)
}

fun dateToYearMonth(date: Date): List<Int> {
    val pattern = "yyyy MM"
    val formatter = SimpleDateFormat(pattern)
    return formatter.format(date)
        .split(" ")
        .map { it.toInt() }
}

fun getStartEndOfCurMonth(startYear: Int, startMonth: Int): List<Long>{
    val startDay = 1
    val startHour = 0
    val startMin = 0
    val lastHour = 23
    val lastMin = 59
    val startInstance = Calendar.getInstance()
    startInstance.set(startYear,startMonth-1,startDay,startHour,startMin)
    val lastDay = startInstance.getActualMaximum(Calendar.DAY_OF_MONTH)
    val lastInstance = Calendar.getInstance()
    lastInstance.set(startYear,startMonth-1,lastDay,lastHour,lastMin)
    return listOf(startInstance.time.time, lastInstance.time.time)
}



fun dateToStringYYYYMdEEType(date: Date): String {
    val pattern = "yyyy. M. d E요일"
    val formatter = SimpleDateFormat(pattern)
    return formatter.format(date)
}