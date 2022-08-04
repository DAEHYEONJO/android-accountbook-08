package com.example.accountbook.domain.usecase

import com.example.accountbook.data.model.Categories
import com.example.accountbook.domain.repository.AccountRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateCategoryUseCase @Inject constructor(
    private val repository: AccountRepository
) {
    suspend operator fun invoke(categories: Categories){
        repository.updateCategories(categories)
    }
}