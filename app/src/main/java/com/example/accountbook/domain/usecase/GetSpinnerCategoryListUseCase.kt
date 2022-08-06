package com.example.accountbook.domain.usecase

import com.example.accountbook.data.model.Categories
import com.example.accountbook.domain.repository.AccountRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetSpinnerCategoryListUseCase @Inject constructor(
    private val repository: AccountRepository
) {
    suspend operator fun invoke(isExpense: Int) = ArrayList<Categories>().apply {
        add(Categories(category = "선택하세요"))
        addAll(repository.getAllCategories(isExpense = 1))
        add(Categories(category = "추가하기"))
    }

}