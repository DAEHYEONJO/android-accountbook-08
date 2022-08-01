package com.example.accountbook.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.example.accountbook.data.model.Categories
import com.example.accountbook.data.model.Payments
import com.example.accountbook.domain.model.HistoriesListItem
import com.example.accountbook.domain.repository.AccountRepository
import com.example.accountbook.utils.date
import com.example.accountbook.utils.dateToYearMonthDay
import com.example.accountbook.utils.stringToDate
import com.example.accountbook.utils.toInt
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryDetailViewModel @Inject constructor(
    private val repository: AccountRepository // use case 로 분리 고민 예정
) : ViewModel() {

    companion object {
        const val TAG = "HistoryDetailViewModel"
    }

    val isExpenseChecked = MutableLiveData<Boolean>()
    fun checkedChanged() {
        isExpenseChecked.value = !isExpenseChecked.value!!
        setCategoryList(isExpenseChecked.value!!.toInt())
    }

    private val _spinnerPaymentsList = MutableLiveData<ArrayList<Payments>>()
    val spinnerPaymentsList: LiveData<ArrayList<Payments>> get() = _spinnerPaymentsList
    fun setPaymentsList() = viewModelScope.launch {
        // useCase 로 분리 고민 예정
        _spinnerPaymentsList.value = ArrayList<Payments>().apply {
            add(Payments(payment = "선택하세요"))
            addAll(repository.getAllPayments())
            add(Payments(payment = "추가하기"))
        }
    }

    private val _spinnerCategoryList = MutableLiveData<ArrayList<Categories>>()
    val spinnerCategoryList: LiveData<ArrayList<Categories>> get() = _spinnerCategoryList
    fun setCategoryList(isExpense: Int) = viewModelScope.launch {
        // useCase 로 분리 고민 예정
        _spinnerCategoryList.value = ArrayList<Categories>().apply {
            add(Categories(category = "선택하세요"))
            addAll(repository.getAllCategories(isExpense))
            add(Categories(category = "추가하기"))
        }
    }

    val paymentSelectedPos = MutableLiveData(0)
    val selectedPayments = MutableLiveData<Payments>()
    val incomeCategorySelectedPos = MutableLiveData<Int>(0)
    val expenseCategorySelectedPos = MutableLiveData<Int>(0)
    val selectedIncomeCategory = MutableLiveData<Categories>()
    val selectedExpenseCategory = MutableLiveData<Categories>()
    val bottomSheetSelectedYear = MutableLiveData<Int>()
    val bottomSheetSelectedMonth = MutableLiveData<Int>()
    val bottomSheetSelectedDay = MutableLiveData<Int>()

    val isPriceEntered = MutableLiveData<Boolean>(false)
    val isPaymentEntered = MutableLiveData<Boolean>(false)
    val isCategoryEntered = MutableLiveData<Boolean>(false)
    val isButtonEnabled = combine(
        isPriceEntered.asFlow(),
        isPaymentEntered.asFlow(),
        isCategoryEntered.asFlow(),
    ) { priceFlag, paymentFlag, categoryFlag ->
        Log.d(TAG, ": $priceFlag $paymentFlag $categoryFlag")
        if (isExpenseChecked.value == true) { // 수입인 경우
            priceFlag and categoryFlag
        } else {
            priceFlag and categoryFlag and paymentFlag
        }
    }.asLiveData()

    init {
        val (year, month, day) = dateToYearMonthDay(date)
        bottomSheetSelectedYear.value = year
        bottomSheetSelectedMonth.value = month - 1
        bottomSheetSelectedDay.value = day
        Log.e(TAG, "init: ${bottomSheetSelectedYear.value}", )
        Log.e(TAG, "init: ${bottomSheetSelectedMonth.value}", )
        Log.e(TAG, "init: ${bottomSheetSelectedDay.value}", )
    }

    val historyListItem = MutableLiveData(HistoriesListItem())
    fun insertHistory(
        price: Long,
        description: String,
        payments: Payments,
        categories: Categories
    ) = viewModelScope.launch {
        with(historyListItem.value) {
            this!!.date = stringToDate(
                bottomSheetSelectedYear.value!!,
                bottomSheetSelectedMonth.value!!,
                bottomSheetSelectedDay.value!!
            )
            this.price = price
            this.description = description
            this.payments = payments
            this.categories = categories
        }
        Log.e(TAG, "viewmodel insertHistory: ${historyListItem.value}", )
        repository.insertHistory(historyListItem.value!!)
        resetMemberProperties()
    }

    private fun resetMemberProperties(){
        paymentSelectedPos.value = 0
        incomeCategorySelectedPos.value = 0
        expenseCategorySelectedPos.value = 0
        isPaymentEntered.value = false
        isPriceEntered.value = false
        isCategoryEntered.value = false
    }
}
