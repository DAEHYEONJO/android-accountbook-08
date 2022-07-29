package com.example.accountbook.utils

import java.text.DecimalFormat

fun getCommaPriceString(price: Int): String{
    val formatter = DecimalFormat("###,###")
    return formatter.format(price)
}