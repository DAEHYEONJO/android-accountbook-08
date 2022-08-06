package com.example.accountbook.domain.usecase

import com.example.accountbook.domain.model.StatisticsItem
import com.example.accountbook.domain.repository.AccountRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.round

@Singleton
class StatisticsItemListUseCase @Inject constructor(
    private val repository: AccountRepository
) {
    suspend operator fun invoke(isExpense: Int, start: Long, end: Long): List<StatisticsItem> {
        val categoryGroupedList = repository.getStatisticsItems(isExpense, start, end)
            .groupBy { it.categories }

        var totalExpense = 0L
        categoryGroupedList.values.forEach { statisticsList ->
            totalExpense += statisticsList.sumOf { it.expensePrice }
        }

        return categoryGroupedList.entries.map {
            val sumExpensePrice = it.value.sumOf { statisticsItem ->
                statisticsItem.expensePrice
            }
            StatisticsItem(
                categories = it.key,
                expensePrice = sumExpensePrice,
                percentage = round(sumExpensePrice.toDouble()*100 / totalExpense).toInt()
            )
        }.sortedBy { -it.percentage }.toList().apply {
            if (this.isNotEmpty()) last().isLast = true
        }
    }
}