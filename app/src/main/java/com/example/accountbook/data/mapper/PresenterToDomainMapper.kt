package com.example.accountbook.data.mapper

import com.example.accountbook.data.model.Categories
import com.example.accountbook.data.model.Histories
import com.example.accountbook.data.model.Payments
import com.example.accountbook.domain.model.HistoriesListItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PresenterToDomainMapper @Inject constructor() {

    fun getHistories(historiesListItem: HistoriesListItem): Histories{
        return historiesListItem.run {
            Histories(
                id = this.id,
                date = this.date!!.time,
                price = this.price,
                description = this.description,
                payments = this.payments!!,
                categories = this.categories!!
            )
        }
    }

}