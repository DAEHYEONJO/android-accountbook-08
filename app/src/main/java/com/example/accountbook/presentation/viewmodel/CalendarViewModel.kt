package com.example.accountbook.presentation.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.accountbook.domain.model.CalendarItem
import com.example.accountbook.domain.usecase.CalendarItemListUseCase
import com.example.accountbook.utils.getStartEndOfCurMonth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val calendarItemListUseCase: CalendarItemListUseCase
): ViewModel(){

    private val _calendarItemList = MutableLiveData<List<CalendarItem>?>()
    val calendarItemList: LiveData<List<CalendarItem>?> get() = _calendarItemList
    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchCalendarItemList(year: Int, month: Int) = viewModelScope.launch {
        val (start, end) = getStartEndOfCurMonth(year,month)
        _calendarItemList.value = calendarItemListUseCase(
            isExpense = MainViewModel.INCOME_AND_EXPENSE,
            start = start,
            end = end,
            year = year,
            month = month,
            day = 1
        )
    }

}