package com.example.accountbook.presentation.viewmodel

import androidx.lifecycle.*
import com.example.accountbook.R
import com.example.accountbook.domain.model.HistoriesTotalData
import com.example.accountbook.domain.repository.AccountRepository
import com.example.accountbook.domain.usecase.GetHistoriesTotalDataUseCase
import com.example.accountbook.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.HashSet

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: AccountRepository,
    private val getHistoriesTotalDataUseCase: GetHistoriesTotalDataUseCase
) : ViewModel() {

    companion object {
        const val TAG = "MainViewModel"
        const val INCOME_AND_EXPENSE = 3
        const val INCOME = 2
        const val EXPENSE = 1
        const val NOTHING = 0
    }

    val curSelectedMenuItemId = MutableLiveData(R.id.bottom_nav_item_history)

    private val _historiesTotalData = MutableLiveData<HistoriesTotalData?>()
    val historiesTotalData: LiveData<HistoriesTotalData?> get() = _historiesTotalData
    fun getHistoriesTotalData(isExpense: Int) = viewModelScope.launch {
        val (start, end) = getStartEndLongValue()
        _historiesTotalData.value = getHistoriesTotalDataUseCase(
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
    val curTotalPrice =
        combine(curMonthIncome.asFlow(), curMonthExpense.asFlow()) { income, expense ->
            -1 * expense + income
        }.asLiveData()
    val curAppbarTitle = MutableLiveData<String>()
    fun setTitle(year: Int, month: Int) {
        curAppbarTitle.value = "${year}??? ${month}???"
    }

    val isExpenseLiveData =
        combine(
            historyIncomeChecked.asFlow(),
            historyExpenseChecked.asFlow()
        )
        { income, expense ->
            val incomeBit = income.toInt()
            val expenseBit = expense.toInt()
            (incomeBit shl 1) or expenseBit
        }.asLiveData()


    fun setTotalPrice() {
//        val (start, end) = getStartEndOfCurMonth(
//            curAppbarYear.value!!,
//            curAppbarMonth.value!!
//        )
        val (start, end) = getStartEndLongValue()
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
    fun setDeleteModeTitle(size: Int = selectedDeleteItems.value!!.size) {
        curAppbarTitle.value = "${size}??? ??????"
    }

    fun setDeleteModeProperties(id: Int) {
        isDeleteMode.value = true // delete mode ??????
        selectedDeleteItems.value!!.add(id) // ??? ????????? ??????
        setDeleteModeTitle()
    }

    fun resetDeleteModeProperties() {
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

    fun getStartEndLongValue(): List<Long> {
        return getStartEndOfCurMonth(
            curAppbarYear.value!!,
            curAppbarMonth.value!!
        )
    }


    init {
        val (year, month) = dateToYearMonth(date)
        curAppbarYear.value = year
        curAppbarMonth.value = month
        setTitle(year, month)
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