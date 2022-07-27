package com.example.accountbook.utils

import java.text.SimpleDateFormat
import java.util.*

fun dateToStringMdEEType(date: Date): String{
    val pattern = "M월 d일 EE"
    val formatter = SimpleDateFormat(pattern)
    return formatter.format(date)
}

fun dateToStringYYYYMdEEType(date: Date): String{
    val pattern = "yyyy. M. d E요일"
    val formatter = SimpleDateFormat(pattern)
    return formatter.format(date)
}