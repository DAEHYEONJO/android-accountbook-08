package com.example.accountbook.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.accountbook.domain.model.HistoriesTotalData
import com.example.accountbook.domain.repository.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: AccountRepository
) : ViewModel() {

    private val _historiesTotalData = MutableLiveData<HistoriesTotalData>()
    val historiesTotalData: LiveData<HistoriesTotalData> get() = _historiesTotalData

    fun getHistoriesTotalData() = viewModelScope.launch {
        _historiesTotalData.value = repository.getHistoriesTotalData()
    }

}