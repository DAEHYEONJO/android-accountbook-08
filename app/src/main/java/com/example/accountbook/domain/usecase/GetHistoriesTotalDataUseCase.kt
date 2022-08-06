package com.example.accountbook.domain.usecase

import com.example.accountbook.domain.model.HistoriesTotalData
import com.example.accountbook.domain.repository.AccountRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetHistoriesTotalDataUseCase @Inject constructor(
    private val repository: AccountRepository
){
    suspend operator fun invoke(isExpense: Int, start: Long, end: Long): HistoriesTotalData {
        return repository.getHistoriesTotalData(isExpense, start, end)
    }
}