package com.example.accountbook.domain.usecase

import com.example.accountbook.data.model.Payments
import com.example.accountbook.domain.repository.AccountRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InsertPaymentUseCase @Inject constructor(
    private val repository: AccountRepository
){
    suspend operator fun invoke(payments: Payments): Boolean{
        return repository.insertPayments(payments)
    }
}