package com.example.accountbook.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.example.accountbook.R
import com.example.accountbook.domain.model.CalendarItem
import com.example.accountbook.domain.model.HistoriesTotalData
import com.example.accountbook.domain.repository.AccountRepository
import com.example.accountbook.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashSet

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: AccountRepository
) : ViewModel() {

    companion object {
        const val TAG = "MainViewModel"
        const val INCOME_AND_EXPENSE = 3
        const val INCOME = 2
        const val EXPENSE = 1
        const val NOTHING = 0
    }

    val curSelectedMenuItemId = MutableLiveData(R.id.bottom_nav_item_history)

    private val _historiesTotalData = MutableLiveData<HistoriesTotalData>()
    val historiesTotalData: LiveData<HistoriesTotalData> get() = _historiesTotalData
    fun getHistoriesTotalData(isExpense: Int) = viewModelScope.launch {
        val (start, end) = getStartEndOfCurMonth(
            curAppbarYear.value!!,
            curAppbarMonth.value!!
        )
        _historiesTotalData.value = repository.getHistoriesTotalData(
            isExpense = isExpense,
            start = start,
            end = end
        )
    }

    private val _historyIncomeChecked = MutableLiveData(true)
    val historyIncomeChecked: LiveData<Boolean> get() = _historyIncomeChecked
    fun onClickIncomeLayout() {
        if (!isDeleteMode.value!!) _historyIncomeChecked.value = !_historyIncomeChecked.value!!
    }

    private val _historyExpenseChecked = MutableLiveData(true)
    val historyExpenseChecked: LiveData<Boolean> get() = _historyExpenseChecked
    fun onClickExposeLayout() {
        if (!isDeleteMode.value!!) _historyExpenseChecked.value = !_historyExpenseChecked.value!!
    }

    val curAppbarYear = MutableLiveData<Int>()
    val curAppbarMonth = MutableLiveData<Int>()
    val curMonthIncome = MutableLiveData<Long>()
    val curMonthExpense = MutableLiveData<Long>()
    val curTotalPrice = combine(curMonthIncome.asFlow(), curMonthExpense.asFlow()){ income, expense ->
        -1*expense + income
    }.asLiveData()
    val curAppbarTitle = MutableLiveData<String>()
    fun setTitle(year: Int, month: Int) {
        curAppbarTitle.value = "${year}년 ${month}월"
    }

    val isExpenseLiveData =
        combine(historyIncomeChecked.asFlow(), historyExpenseChecked.asFlow()) { income, expense ->
            val incomeBit = income.toInt()
            val expenseBit = expense.toInt()
            (incomeBit shl 1) or expenseBit
        }.asLiveData()

    fun setTotalPrice() {
        val (start, end) = getStartEndOfCurMonth(
            curAppbarYear.value!!,
            curAppbarMonth.value!!
        )
        with(viewModelScope) {
            launch {
                curMonthIncome.value = repository.getSumPrice(0, start, end)
            }
            launch {
                curMonthExpense.value = repository.getSumPrice(1, start, end)
            }
        }
    }

    val isDeleteMode = MutableLiveData<Boolean>(false)
    val selectedDeleteItems = MutableLiveData<HashSet<Int>>(HashSet())
    fun setDeleteModeTitle(size: Int = selectedDeleteItems.value!!.size){
        curAppbarTitle.value = "${size}개 선택"
    }
    fun setDeleteModeProperties(id: Int){
        isDeleteMode.value = true // delete mode 전환
        selectedDeleteItems.value!!.add(id) // 첫 아이템 넣기
        setDeleteModeTitle()
    }
    fun resetDeleteModeProperties(){
        isDeleteMode.value = false
        selectedDeleteItems.value!!.clear()
        setTitle(curAppbarYear.value!!, curAppbarMonth.value!!)
    }
    fun deleteHistories() = viewModelScope.launch {
        selectedDeleteItems.value?.let {
            it.forEach { id ->
                repository.deleteHistory(id)
            }
        }
        getHistoriesTotalData(isExpenseLiveData.value!!)
        resetDeleteModeProperties()
    }
    private val _calendarItemList = MutableLiveData<List<CalendarItem>>()
    val calendarItemList: LiveData<List<CalendarItem>> get() = _calendarItemList
    fun fetchCalendarItemList() = viewModelScope.launch {
        val (start, end) = getStartEndOfCurMonth(
            curAppbarYear.value!!,
            curAppbarMonth.value!!
        )
        _calendarItemList.value = repository.getCalendarItems(
            start = start,
            end = end,
            year = curAppbarYear.value!!,
            month = curAppbarMonth.value!!,
            day = 1
        )
    }

    init {
        val (year, month) = dateToYearMonth(date)
        curAppbarYear.value = year
        curAppbarMonth.value = month
        setTitle(year, month)
        Log.e(TAG, "MainViewModel Init: ", )
    }

    fun onClickNextMonthBtn() {
        var curMonth = curAppbarMonth.value!!
        curMonth = (curMonth + 1) % 13

        curAppbarMonth.value = if (curMonth == 0) {
            plusYear()
            curMonth + 1
        } else curMonth
        setTitle(curAppbarYear.value!!, curAppbarMonth.value!!)

    }

    private fun plusYear() {
        var curYear = curAppbarYear.value!!
        curYear = (curYear + 1) % 2023
        curAppbarYear.value = if (curYear == 0) 2000 else curYear
    }

    fun onClickPreMonthBtn() {
        var curMonth = curAppbarMonth.value!!
        curMonth = (curMonth - 1) % 13
        curAppbarMonth.value = if (curMonth == 0) {
            minusYear()
            12
        } else curMonth
        setTitle(curAppbarYear.value!!, curAppbarMonth.value!!)
    }

    private fun minusYear() {
        var curYear = curAppbarYear.value!!
        curYear = (curYear - 1) % 2023
        curAppbarYear.value = if (curYear == 1999) 2022 else curYear
    }

}