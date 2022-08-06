package com.example.accountbook.presentation.history

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.example.accountbook.R
import com.example.accountbook.data.model.Categories
import com.example.accountbook.data.model.Payments
import com.example.accountbook.databinding.FragmentHistoryDetailBinding
import com.example.accountbook.presentation.ui.CustomSpinner
import com.example.accountbook.presentation.adapter.CategorySpinnerAdapter
import com.example.accountbook.presentation.adapter.PaymentSpinnerAdapter
import com.example.accountbook.presentation.base.BaseFragment
import com.example.accountbook.presentation.bottomsheet.DatePickerBottomSheetFragment
import com.example.accountbook.presentation.setting.SettingDetailFragment
import com.example.accountbook.presentation.viewmodel.HistoryDetailViewModel
import com.example.accountbook.presentation.viewmodel.MainViewModel
import com.example.accountbook.presentation.viewmodel.SettingViewModel
import com.example.accountbook.utils.dpToPx
import com.example.accountbook.utils.getCommaPriceString
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HistoryDetailFragment
    : BaseFragment<FragmentHistoryDetailBinding>(
    R.layout.fragment_history_detail,
    "HistoryDetailFragment"
) {
    private val mainViewModel: MainViewModel by activityViewModels()
    private val historyDetailViewModel: HistoryDetailViewModel by activityViewModels()
    private val settingViewModel: SettingViewModel by activityViewModels()
    @Inject
    lateinit var paymentSpinnerAdapter: PaymentSpinnerAdapter
    @Inject
    lateinit var categorySpinnerAdapter: CategorySpinnerAdapter
    private val spinnerEventsListener = object : CustomSpinner.OnSpinnerEventsListener {
        override fun onPopupWindowOpened(spinner: Spinner?) {
            spinner!!.background = AppCompatResources.getDrawable(
                requireContext(),
                R.drawable.spinner_arrow_up_background
            )
        }

        override fun onPopupWindowClosed(spinner: Spinner?) {
            spinner!!.background = AppCompatResources.getDrawable(
                requireContext(),
                R.drawable.spinner_arrow_down_background
            )
        }
    }
    var commaPrice: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBottomSheet()
        initDataBinding()
        fetchList()
        initPaymentSpinner()
        initCategorySpinner()
        initAppbar()
        initEditText()
        initObserver()
        initBtn()
    }

    private fun initBottomSheet() {
        with(binding.historyDetailDateTv) {
            setOnClickListener {
                DatePickerBottomSheetFragment().show(
                    childFragmentManager,
                    DatePickerBottomSheetFragment.TAG
                )
            }
        }
    }

    private fun initDataBinding() {
        with(binding) {
            this.mvm = mainViewModel
            this.viewModel = historyDetailViewModel
        }
    }

    private fun fetchList() {
        historyDetailViewModel.fetchData()
    }

    private fun initObserver() {
        with(historyDetailViewModel) {
            spinnerPaymentsList.observe(viewLifecycleOwner) {
                paymentSpinnerAdapter.paymentList = it!!
                paymentSpinnerAdapter.notifyDataSetChanged()
            }
            spinnerExpenseCategoryList.observe(viewLifecycleOwner) {
                if (isExpenseChecked.value!!){
                    categorySpinnerAdapter.categoryList = it!!
                    categorySpinnerAdapter.notifyDataSetChanged()
                }
            }
            spinnerIncomeCategoryList.observe(viewLifecycleOwner) {
                if (!isExpenseChecked.value!!) {
                    categorySpinnerAdapter.categoryList = it!!
                    categorySpinnerAdapter.notifyDataSetChanged()
                }
            }
            isExpenseChecked.observe(viewLifecycleOwner){
                categorySpinnerAdapter.categoryList = if (it){
                    spinnerExpenseCategoryList.value!!
                }else{
                    spinnerIncomeCategoryList.value!!
                }
            }
        }
    }

    private fun fragmentTransaction() {
        parentFragmentManager.commit {
            var fragment =
                parentFragmentManager.findFragmentByTag(SettingDetailFragment::class.simpleName)
            addToBackStack(null)
            fragment = if (fragment == null) {
                SettingDetailFragment()
            } else {
                fragment as SettingDetailFragment
            }
            replace(
                R.id.main_fragment_container_view,
                fragment,
                SettingDetailFragment::class.simpleName,
            )
        }
    }

    private fun initPaymentSpinner() {
        with(binding.historyDetailPaymentSpinner) {
            setSpinnerEventsListener(spinnerEventsListener)
            adapter = paymentSpinnerAdapter
            dropDownVerticalOffset = dpToPx(requireContext(), 46).toInt()
            setWillNotDraw(false)
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    p0: AdapterView<*>?,
                    p1: View?,
                    position: Int,
                    id: Long
                ) {
                    with(historyDetailViewModel) {
                        if (position == paymentSpinnerAdapter.paymentList.size-1){
                            fragmentTransaction()
                            addPaymentState.value = true
                            with(settingViewModel){
                                setProperties(
                                    title = resources.getString(R.string.setting_detail_app_bar_title_payment_add),
                                    name = "",
                                    updateMode = false,
                                    paymentMode = true,
                                    expenseMode= true,
                                )
                            }
                        }else{
                            isPaymentEntered.value =
                                position != 0 && position != paymentSpinnerAdapter.paymentList.size - 1
                            paymentSelectedPos.value = position
                            selectedPayments.value = (p0!!.selectedItem as Payments)
                        }

                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }
        }
    }

    private fun setSpinnerSelectedPos(position: Int, categories: Categories) {
        with(historyDetailViewModel) {
            when (isExpenseChecked.value) {
                true -> {
                    selectedExpenseCategory.value = categories
                    expenseCategorySelectedPos.value = position
                }
                false -> {
                    selectedIncomeCategory.value = categories
                    incomeCategorySelectedPos.value = position
                }
                else -> {}
            }
        }
    }

    private fun initCategorySpinner() {
        with(binding.historyDetailCategorySpinner) {
            setSpinnerEventsListener(spinnerEventsListener)
            adapter = categorySpinnerAdapter
            dropDownVerticalOffset = dpToPx(requireContext(), 46).toInt()
            setWillNotDraw(false)
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    p0: AdapterView<*>?,
                    p1: View?,
                    position: Int,
                    id: Long
                ) {
                    with(historyDetailViewModel){
                        if (position == categorySpinnerAdapter.categoryList.size-1){
                            fragmentTransaction()
                            addCategoryState.value = true
                            with(settingViewModel){
                                setProperties(
                                    title = if (historyDetailViewModel.isExpenseChecked.value!!)
                                        resources.getString(R.string.setting_detail_app_bar_title_expense_category_add)
                                    else resources.getString(R.string.setting_detail_app_bar_title_income_category_add),
                                    name = "",
                                    updateMode = false,
                                    paymentMode = false,
                                    expenseMode= historyDetailViewModel.isExpenseChecked.value!!
                                )
                                categoryFromExpense.value = historyDetailViewModel.isExpenseChecked.value!!
                            }
                        }else{
                            setSpinnerSelectedPos(position, p0!!.selectedItem as Categories)
                            historyDetailViewModel.isCategoryEntered.value =
                                position != 0 && position != categorySpinnerAdapter.categoryList.size - 1
                        }
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }
            }
        }
    }

    private fun initBtn() {
        with(binding) {
            historyDetailAddBtn.setOnClickListener {
                val price = historyDetailPriceEt.text.toString().replace(",", "").toLong()
                val description = historyDetailDescriptionEt.text.let {
                    if (it.isNotEmpty()) {
                        it.toString()
                    } else {
                        ""
                    }
                }
                val categories = if (historyDetailViewModel.isExpenseChecked.value!!) {
                    historyDetailViewModel.selectedExpenseCategory.value!!
                } else {
                    historyDetailViewModel.selectedIncomeCategory.value!!
                }
                val payments = historyDetailViewModel.selectedPayments.value?: Payments()
                with(historyDetailViewModel){
                    if (isUpdateMode.value!!){
                        updateHistory(price, description, payments, categories)
                    }else{
                        insertHistory(price, description, payments, categories)
                    }
                }
                parentFragmentManager.popBackStack()
            }
        }

    }

    private fun initEditText() {
        with(binding.historyDetailPriceEt) {
            doAfterTextChanged {
                val curText = it.toString().replace(",", "")
                commaPrice = if (it?.isNotEmpty() == true) {
                    getCommaPriceString(curText.toLong())
                } else {
                    ""
                }
                if (commaPrice != it.toString()) {
                    setText(commaPrice)
                    setSelection(commaPrice.length)
                }
                historyDetailViewModel.isPriceEntered.value = it?.isNotEmpty()
            }
        }
    }

    private fun initAppbar() {
        with(binding.historyDetailAppBar) {
            mainViewModel.curAppbarTitle.value = resources.getString(R.string.history_detail_app_bar_title)
            appBarBackIv.setImageDrawable(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.ic_app_bar_back
                )
            )
            appBarRightIv.visibility = View.INVISIBLE
            appBarBackIv.setOnClickListener {
                parentFragmentManager.popBackStack()
                historyDetailViewModel.resetMemberProperties()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mainViewModel.setTitle(mainViewModel.curAppbarYear.value!!, mainViewModel.curAppbarMonth.value!!)
    }

}