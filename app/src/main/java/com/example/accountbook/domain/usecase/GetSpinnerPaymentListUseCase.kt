package com.example.accountbook.domain.usecase

import com.example.accountbook.data.model.Payments
import com.example.accountbook.domain.repository.AccountRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetSpinnerPaymentListUseCase @Inject constructor(
    private val repository: AccountRepository
) {
    suspend operator fun invoke(): ArrayList<Payments> = ArrayList<Payments>().apply {
        add(Payments(payment = "선택하세요"))
        addAll(repository.getAllPayments())
        add(Payments(payment = "추가하기"))
    }

}