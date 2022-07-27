package com.example.accountbook.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.accountbook.domain.repository.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: AccountRepository
) : ViewModel() {


}