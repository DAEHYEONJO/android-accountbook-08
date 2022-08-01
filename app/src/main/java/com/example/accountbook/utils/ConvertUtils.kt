package com.example.accountbook.utils

import android.content.Context
import android.util.DisplayMetrics
import java.text.DecimalFormat

fun getCommaPriceString(price: Long): String {
    val formatter = DecimalFormat("###,###")
    return formatter.format(price)
}

fun Boolean.toInt() = if (this) 1 else 0

fun dpToPx(context: Context, dp: Int): Float {
    val resources = context.resources
    val metrics = resources.displayMetrics
    return dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}