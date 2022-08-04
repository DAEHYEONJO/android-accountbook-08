package com.example.accountbook.data.mapper

import com.example.accountbook.domain.model.HistoriesListItem
import com.example.accountbook.domain.model.StatisticsItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DomainToPresenterMapper @Inject constructor() {
    fun getStatisticsItem(historiesListItem: HistoriesListItem): StatisticsItem{
        return StatisticsItem(
            categories = historiesListItem.categories!!,
            expensePrice = historiesListItem.price,
            percentage = 0
        )
    }
}