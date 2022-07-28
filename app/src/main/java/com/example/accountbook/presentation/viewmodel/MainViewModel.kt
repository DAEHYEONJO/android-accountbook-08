package com.example.accountbook.presentation.viewmodel

import androidx.lifecycle.*
import com.example.accountbook.R
import com.example.accountbook.domain.model.HistoriesTotalData
import com.example.accountbook.domain.repository.AccountRepository
import com.example.accountbook.utils.dateToYearMonth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: AccountRepository
) : ViewModel() {
    private val date = Calendar.getInstance().time
    val curSelectedMenuItemId = MutableLiveData(R.id.bottom_nav_item_history)

    private val _historiesTotalData = MutableLiveData<HistoriesTotalData>()
    val historiesTotalData: LiveData<HistoriesTotalData> get() = _historiesTotalData
    fun getHistoriesTotalData() = viewModelScope.launch {
        _historiesTotalData.value = repository.getHistoriesTotalData()
    }

    private val _historyIncomeChecked = MutableLiveData(false)
    val historyIncomeChecked: LiveData<Boolean> get() = _historyIncomeChecked
    fun onClickIncomeLayout() {
        _historyIncomeChecked.value = !_historyIncomeChecked.value!!
    }

    private val _historyExpenseChecked = MutableLiveData(false)
    val historyExpenseChecked: LiveData<Boolean> get() = _historyExpenseChecked
    fun onClickExposeLayout() {
        _historyExpenseChecked.value = !_historyExpenseChecked.value!!
    }

    val curAppbarYear = MutableLiveData<Int>()
    val curAppbarMonth = MutableLiveData<Int>()
    val curAppbarTitle = MediatorLiveData<String>()
    private fun setTitle(year: Int?, month: Int?){
        curAppbarTitle.value = "${year}년 ${month}월"
    }

    init {
        val (year, month) = dateToYearMonth(date)
        curAppbarYear.value = year
        curAppbarMonth.value = month
        curAppbarTitle.addSource(curAppbarYear){
            setTitle(it, curAppbarMonth.value)
        }
        curAppbarTitle.addSource(curAppbarMonth){
            setTitle(curAppbarYear.value, it)
        }
    }

    fun onClickNextMonthBtn(){
        var curMonth = curAppbarMonth.value!!
        curMonth = (curMonth+1)%13

        curAppbarMonth.value = if (curMonth == 0){
            plusYear()
            curMonth+1
        } else curMonth
    }

    private fun plusYear(){
        var curYear = curAppbarYear.value!!
        curYear = (curYear+1)%2023
        curAppbarYear.value = if (curYear == 0) 2000 else curYear
    }

    fun onClickPreMonthBtn(){
        var curMonth = curAppbarMonth.value!!
        curMonth = (curMonth-1)%13
        curAppbarMonth.value = if (curMonth == 0) {
            minusYear()
            12
        } else curMonth
    }

    private fun minusYear(){
        var curYear = curAppbarYear.value!!
        curYear = (curYear-1)%2023
        curAppbarYear.value = if (curYear == 1999) 2022 else curYear
    }

}