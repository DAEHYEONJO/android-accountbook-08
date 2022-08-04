package com.example.accountbook.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.example.accountbook.data.model.Categories
import com.example.accountbook.data.model.Payments
import com.example.accountbook.domain.model.HistoriesListItem
import com.example.accountbook.domain.repository.AccountRepository
import com.example.accountbook.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.ArrayList

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
        setCategoryList()
    }

    private val _spinnerPaymentsList = MutableLiveData<ArrayList<Payments>>()
    val spinnerPaymentsList: LiveData<ArrayList<Payments>> get() = _spinnerPaymentsList
    private fun setPaymentsList() = viewModelScope.launch {
        // useCase 로 분리 고민 예정
        _spinnerPaymentsList.value = ArrayList<Payments>().apply {
            add(Payments(payment = "선택하세요"))
            addAll(repository.getAllPayments())
            add(Payments(payment = "추가하기"))
        }
    }

    private val _spinnerIncomeCategoryList = MutableLiveData<ArrayList<Categories>>()
    val spinnerIncomeCategoryList: LiveData<ArrayList<Categories>> get() = _spinnerIncomeCategoryList
    private val _spinnerExpenseCategoryList = MutableLiveData<ArrayList<Categories>>()
    val spinnerExpenseCategoryList: LiveData<ArrayList<Categories>> get() = _spinnerExpenseCategoryList
    fun fetchData(){
        setCategoryList()
        setPaymentsList()
    }
    private fun setCategoryList() {
        viewModelScope.launch {
            // useCase 로 분리 고민 예정
            _spinnerExpenseCategoryList.value = ArrayList<Categories>().apply {
                add(Categories(category = "선택하세요"))
                addAll(repository.getAllCategories(isExpense = 1))
                add(Categories(category = "추가하기"))
            }
        }
        viewModelScope.launch {
            _spinnerIncomeCategoryList.value = ArrayList<Categories>().apply {
                add(Categories(category = "선택하세요"))
                addAll(repository.getAllCategories(isExpense = 0))
                add(Categories(category = "추가하기"))
            }
        }
    }

    val isUpdateMode = MutableLiveData(false)
    val updateHistoryId = MutableLiveData<Int>(0)
    val paymentSelectedPos = MutableLiveData(0)
    val selectedPayments = MutableLiveData<Payments>()
    val incomeCategorySelectedPos = MutableLiveData<Int>(0)
    val expenseCategorySelectedPos = MutableLiveData<Int>(0)
    val selectedIncomeCategory = MutableLiveData<Categories>()
    val selectedExpenseCategory = MutableLiveData<Categories>()
    val bottomSheetSelectedYear = MutableLiveData<Int>()
    val bottomSheetSelectedMonth = MutableLiveData<Int>()
    val bottomSheetSelectedDay = MutableLiveData<Int>()
    val inputDescription = MutableLiveData<String>()
    val inputPrice = MutableLiveData<String>()

    val isPriceEntered = MutableLiveData<Boolean>(false)
    val isPaymentEntered = MutableLiveData<Boolean>(false)
    val isCategoryEntered = MutableLiveData<Boolean>(false)
    val isButtonEnabled = combine(
        isPriceEntered.asFlow(),
        isPaymentEntered.asFlow(),
        isCategoryEntered.asFlow(),
    ) { priceFlag, paymentFlag, categoryFlag ->
        Log.d(TAG, ": $priceFlag $paymentFlag $categoryFlag")
        if (isExpenseChecked.value == false) { // 수입인 경우
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
        fetchData()
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
        repository.insertHistory(historyListItem.value!!)
        resetMemberProperties()
    }

    fun updateHistory(
        price: Long,
        description: String,
        payments: Payments,
        categories: Categories
    ) = viewModelScope.launch {
        with(historyListItem.value) {
            this!!.id = updateHistoryId.value!!
            this.date = stringToDate(
                bottomSheetSelectedYear.value!!,
                bottomSheetSelectedMonth.value!!,
                bottomSheetSelectedDay.value!!
            )
            this.price = price
            this.description = description
            this.payments = payments
            this.categories = categories
        }
        repository.updateHistory(historyListItem.value!!)
        resetMemberProperties()
    }

    val addPaymentState = MutableLiveData(false)
    val addCategoryState = MutableLiveData(false)
    val categoryFromExpense = MutableLiveData(false)

    fun setMemberProperties(historyListItem: HistoriesListItem){
        with(historyListItem){
            val (year, month, day) = dateToYearMonthDay(date!!)
            if (categories!!.isExpense == 1){
                expenseCategorySelectedPos.value = spinnerExpenseCategoryList.value!!.indexOf(categories!!)
                paymentSelectedPos.value = spinnerPaymentsList.value!!.indexOf(payments!!)
                isExpenseChecked.value = true
            }else{
                incomeCategorySelectedPos.value = spinnerIncomeCategoryList.value!!.indexOf(categories!!)
                isExpenseChecked.value = false
            }
            updateHistoryId.value = historyListItem.id
            bottomSheetSelectedYear.value = year
            bottomSheetSelectedMonth.value = month - 1
            bottomSheetSelectedDay.value = day
            inputDescription.value = description
            inputPrice.value = getCommaPriceString(price)

        }
    }

    fun resetMemberProperties(){
        isExpenseChecked.value = false
        paymentSelectedPos.value = 0
        incomeCategorySelectedPos.value = 0
        expenseCategorySelectedPos.value = 0
        isPaymentEntered.value = false
        isPriceEntered.value = false
        isCategoryEntered.value = false
        val (year, month, day) = dateToYearMonthDay(date)
        bottomSheetSelectedYear.value = year
        bottomSheetSelectedMonth.value = month - 1
        bottomSheetSelectedDay.value = day
        isUpdateMode.value = false
        inputDescription.value = ""
        inputPrice.value = ""
        updateHistoryId.value = 0
    }
}
