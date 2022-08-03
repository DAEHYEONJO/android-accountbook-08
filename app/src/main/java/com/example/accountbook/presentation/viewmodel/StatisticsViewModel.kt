package com.example.accountbook.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.accountbook.domain.model.StatisticsItem
import com.example.accountbook.domain.usecase.StatisticsItemListUseCase
import com.example.accountbook.presentation.viewmodel.MainViewModel.Companion.EXPENSE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val statisticsItemListUseCase: StatisticsItemListUseCase
): ViewModel() {

    private val _statisticsItemList = MutableLiveData<List<StatisticsItem>?>()
    val statisticsItem: LiveData<List<StatisticsItem>?> get() = _statisticsItemList
    fun getStatisticsItemList(start: Long, end: Long) = viewModelScope.launch{
        _statisticsItemList.value = statisticsItemListUseCase(
            isExpense = EXPENSE,
            start = start,
            end = end
        )
    }
}