package com.example.accountbook.domain.usecase

import com.example.accountbook.data.model.Categories
import com.example.accountbook.domain.repository.AccountRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InsertCategoryUseCase @Inject constructor(
    private val repository: AccountRepository
){
    suspend operator fun invoke(categories: Categories): Boolean{
        return repository.insertCategories(categories)
    }
}