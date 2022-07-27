package com.example.accountbook.data.repository

import com.example.accountbook.data.db.AccountBookDbHelper
import com.example.accountbook.data.model.Histories
import com.example.accountbook.domain.repository.AccountRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepositoryImpl @Inject constructor(
    private val dbHelper: AccountBookDbHelper
) : AccountRepository {

}