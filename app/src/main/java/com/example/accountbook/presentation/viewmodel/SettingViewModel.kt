package com.example.accountbook.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.accountbook.data.model.Categories
import com.example.accountbook.data.model.Payments
import com.example.accountbook.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val categoryItemListUseCase: CategoryItemListUseCase,
    private val paymentItemListUseCase: PaymentItemListUseCase,
    private val insertCategoryUseCase: InsertCategoryUseCase,
    private val insertPaymentUseCase: InsertPaymentUseCase,
    private val updateCategoryUseCase: UpdateCategoryUseCase,
    private val updatePaymentUseCase: UpdatePaymentUseCase
): ViewModel() {

    private val _expenseCategoryItemList = MutableLiveData<List<Categories>?>()
    val expenseCategoryItemList: LiveData<List<Categories>?> get() = _expenseCategoryItemList
    private fun getExpenseCategoryItemList(isExpense: Int = 1) = viewModelScope.launch {
        _expenseCategoryItemList.value = categoryItemListUseCase(isExpense)
    }

    private val _incomeCategoryItemList = MutableLiveData<List<Categories>?>()
    val incomeCategoryItemList: LiveData<List<Categories>?> get() = _incomeCategoryItemList
    private fun getIncomeCategoryItemList(isExpense: Int = 0) = viewModelScope.launch {
        _incomeCategoryItemList.value = categoryItemListUseCase(isExpense)
    }

    private val _paymentItemList = MutableLiveData<List<Payments>?>()
    val paymentItemList: LiveData<List<Payments>?> get() = _paymentItemList
    private fun getPaymentItemList() = viewModelScope.launch {
        _paymentItemList.value = paymentItemListUseCase()
    }

    fun fetchItem(){
        getPaymentItemList()
        getExpenseCategoryItemList()
        getIncomeCategoryItemList()
    }

    private val _isInsertPaymentSuccess = MutableStateFlow(false)
    val isInsertPaymentSuccess: StateFlow<Boolean> get() = _isInsertPaymentSuccess
    fun insertPayments(payments: Payments)= viewModelScope.launch{
        _isInsertPaymentSuccess.emit(insertPaymentUseCase(payments))
    }

    private val _isInsertCategoriesSuccess = MutableStateFlow(false)
    val isInsertCategoriesSuccess: StateFlow<Boolean> get() = _isInsertCategoriesSuccess
    fun insertCategories(categories: Categories) = viewModelScope.launch {
        _isInsertCategoriesSuccess.emit(insertCategoryUseCase(categories))
    }

    fun updateCategories(categories: Categories) = viewModelScope.launch {
        updateCategoryUseCase(categories)
    }

    fun updatePayments(payments: Payments) = viewModelScope.launch {
        updatePaymentUseCase(payments)
    }

    val appBarTitle = MutableLiveData<String>("") // fragment transaction 마다 세팅
    val isButtonEnabled = MutableLiveData(false) // 이름, 색 선택시(기본값 적용 됨) true -> 이름 입력시 true
    val isUpdateMode = MutableLiveData(false) // 아이템 리스트 클릭해서 왔을때 true, 추가하기 눌러서 왔을때 false
    val isPaymentMode = MutableLiveData(false) // 결제 수단 모드일 경우 true, 아닐경우 false
    val inputName = MutableLiveData("") // 이름 edittext에 입력했던 내용
    val isExpenseMode = MutableLiveData(false) // 지출 카테고리 모드인지
    val incomeLabelColorString = MutableLiveData("")
    val expenseLabelColorString = MutableLiveData("")
    val selectedIncomeColorPos = MutableLiveData(0)
    val selectedExpenseColorPos = MutableLiveData(0)
    val curCategoryId = MutableLiveData(0)
    val curPaymentId = MutableLiveData(0)
    var bottomNavigationHeight = 0

    fun setProperties(
        title: String,
        updateMode: Boolean,
        paymentMode: Boolean,
        name: String,
        expenseMode: Boolean,
        labelColor: String = "",
        categoryId: Int = 0,
        paymentId: Int = 0
    ){
        appBarTitle.value = title
        isUpdateMode.value = updateMode
        isPaymentMode.value = paymentMode
        inputName.value = name
        isExpenseMode.value = expenseMode
        if (expenseMode){
            expenseLabelColorString.value = labelColor
        }else{
            incomeLabelColorString.value = labelColor
        }
        curCategoryId.value = categoryId
        curPaymentId.value = paymentId
    }

    fun resetProperties(){
        appBarTitle.value = ""
        isButtonEnabled.value = false
        isUpdateMode.value = false
        isPaymentMode.value = false
        inputName.value = ""
        isExpenseMode.value = false
        selectedExpenseColorPos.value = 0
        selectedIncomeColorPos.value = 0
        expenseLabelColorString.value = ""
        incomeLabelColorString.value = ""
    }
}