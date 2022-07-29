package com.example.accountbook.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.example.accountbook.R
import com.example.accountbook.domain.model.HistoriesTotalData
import com.example.accountbook.domain.repository.AccountRepository
import com.example.accountbook.utils.dateToYearMonth
import com.example.accountbook.utils.getStartEndOfCurMonth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: AccountRepository
) : ViewModel() {
    
    companion object{
        const val TAG = "MainViewModel"
    }
    private val date = Calendar.getInstance().time
    val curSelectedMenuItemId = MutableLiveData(R.id.bottom_nav_item_history)

    private val _historiesTotalData = MutableLiveData<HistoriesTotalData>()
    val historiesTotalData: LiveData<HistoriesTotalData> get() = _historiesTotalData
    fun getHistoriesTotalData(isExpense: Int, start: Long, end: Long) = viewModelScope.launch {
        _historiesTotalData.value = repository.getHistoriesTotalData(
            isExpense = isExpense,
            start = start,
            end = end
        )
        curMonthIncome.value = _historiesTotalData.value!!.totalIncome
        curMonthExpense.value = _historiesTotalData.value!!.totalExpense

    }

    private val _historyIncomeChecked = MutableLiveData(true)
    val historyIncomeChecked: LiveData<Boolean> get() = _historyIncomeChecked
    fun onClickIncomeLayout() {
        Log.e(TAG, "onClickIncomeLayout: ", )
        _historyIncomeChecked.value = !_historyIncomeChecked.value!!
    }

    private val _historyExpenseChecked = MutableLiveData(true)
    val historyExpenseChecked: LiveData<Boolean> get() = _historyExpenseChecked
    fun onClickExposeLayout() {
        Log.e(TAG, "onClickExposeLayout: ", )
        _historyExpenseChecked.value = !_historyExpenseChecked.value!!
    }

    val curAppbarYear = MutableLiveData<Int>()
    val curAppbarMonth = MutableLiveData<Int>()
    val curMonthIncome = MutableLiveData<Int>()
    val curMonthExpense = MutableLiveData<Int>()
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
        val (start, end) = getStartEndOfCurMonth(year, month)
        getHistoriesTotalData(-1, start, end)
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