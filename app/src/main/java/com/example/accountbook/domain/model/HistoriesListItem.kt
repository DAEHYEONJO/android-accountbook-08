package com.example.accountbook.domain.model

import com.example.accountbook.data.model.Categories
import com.example.accountbook.data.model.Payments
import java.util.*

const val HEADER = 0
const val BODY = 1

data class HistoriesListItem(
    val id: Int = 0,
    var date: Date? = null,
    val price: Int = 0,
    val description: String = "",
    val payments: Payments? = null,
    val categories: Categories? = null
){
    var viewType: Int = BODY
    var isLastElement: Boolean = false
    var dayIncoming = 0
    var dayExpense = 0

    override fun toString(): String {
        return "id$id type$viewType date$date price$price des$description dI$dayIncoming dE$dayExpense $payments $categories"
    }
}
