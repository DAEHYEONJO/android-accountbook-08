package com.example.accountbook.presentation.history

import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import com.example.accountbook.R
import com.example.accountbook.data.model.Categories
import com.example.accountbook.data.model.Payments
import com.example.accountbook.databinding.FragmentHistoryDetailBinding
import com.example.accountbook.presentation.CustomSpinner
import com.example.accountbook.presentation.adapter.CategorySpinnerAdapter
import com.example.accountbook.presentation.adapter.PaymentSpinnerAdapter
import com.example.accountbook.presentation.base.BaseFragment
import com.example.accountbook.presentation.bottomsheet.AppBarBottomSheetFragment
import com.example.accountbook.presentation.bottomsheet.DatePickerBottomSheetFragment
import com.example.accountbook.presentation.viewmodel.HistoryDetailViewModel
import com.example.accountbook.presentation.viewmodel.MainViewModel
import com.example.accountbook.utils.dpToPx
import com.example.accountbook.utils.getCommaPriceString
import com.example.accountbook.utils.toInt
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
        initRadioGroup()
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

    private fun initRadioGroup() {
        with(binding) {
            this.viewModel = historyDetailViewModel
            val checkedValue = if (historyDetailViewModel.isExpenseChecked.value == null) {
                mainViewModel.isExpenseLiveData.value!!
            } else {
                historyDetailViewModel.isExpenseChecked.value!!.toInt()
            }

            if (checkedValue == 1) {
                historyDetailExpenseBtn.isChecked = true
            } else {
                historyDetailIncomeBtn.isChecked = true
            }
            historyDetailViewModel.isExpenseChecked.value = historyDetailExpenseBtn.isChecked
        }
    }

    private fun fetchList() {
        with(historyDetailViewModel) {
            setPaymentsList()
            setCategoryList(historyDetailViewModel.isExpenseChecked.value!!.toInt())
        }
    }

    private fun initObserver() {
        with(historyDetailViewModel) {
            spinnerPaymentsList.observe(viewLifecycleOwner) {
                it.forEach {
                    Log.e(TAG, "initObserver: $it")
                }
                paymentSpinnerAdapter.paymentList = it
                paymentSpinnerAdapter.notifyDataSetChanged()
            }
            spinnerCategoryList.observe(viewLifecycleOwner) {
                it.forEach {
                    Log.e(TAG, "spinnerCategoryList: $it")
                }
                categorySpinnerAdapter.categoryList = it
                categorySpinnerAdapter.notifyDataSetChanged()
            }
            isButtonEnabled.observe(viewLifecycleOwner) {
                Log.d(TAG, "initObserver: isButtonEnabled $it")
            }
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
                        isPaymentEntered.value =
                            position != 0 && position != paymentSpinnerAdapter.paymentList.size - 1
                        paymentSelectedPos.value = position
                        selectedPayments.value = (p0!!.selectedItem as Payments)
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    Log.d(TAG, "category onNothingSelected: $p0")
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
                    setSpinnerSelectedPos(position, p0!!.selectedItem as Categories)
                    historyDetailViewModel.isCategoryEntered.value =
                        position != 0 && position != categorySpinnerAdapter.categoryList.size - 1
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    Log.d(TAG, "category onNothingSelected: $p0")
                }
            }
        }
    }

    private fun initBtn() {
        with(binding) {
            historyDetailAddBtn.setOnClickListener {
                Log.e(TAG, "initBtn: ${historyDetailPriceEt.text.toString().replace(",", "").toLong()}", )
                Log.e(TAG, "initBtn: ${historyDetailDescriptionEt.text.let {
                    if (it.isNotEmpty()) {
                        it.toString()
                    } else {
                        ""
                    }
                }}", )
                Log.e(TAG, "initBtn: ${if (historyDetailViewModel.isExpenseChecked.value!!) {
                    historyDetailViewModel.selectedExpenseCategory.value!!
                } else {
                    historyDetailViewModel.selectedIncomeCategory.value!!
                }}", )
                Log.e(TAG, "initBtn: ${historyDetailViewModel.selectedPayments.value!!}", )
                historyDetailViewModel.insertHistory(
                    price = historyDetailPriceEt.text.toString().replace(",", "").toLong(),
                    description = historyDetailDescriptionEt.text.let {
                        if (it.isNotEmpty()) {
                            it.toString()
                        } else {
                            ""
                        }
                    },
                    categories = if (historyDetailViewModel.isExpenseChecked.value!!) {
                        historyDetailViewModel.selectedExpenseCategory.value!!
                    } else {
                        historyDetailViewModel.selectedIncomeCategory.value!!
                    },
                    payments = historyDetailViewModel.selectedPayments.value!!
                )
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
            appBarTitleTv.text = resources.getString(R.string.history_detail_app_bar_title)
            appBarBackIv.setImageDrawable(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.ic_app_bar_back
                )
            )
            appBarRightIv.visibility = View.INVISIBLE
            appBarBackIv.setOnClickListener {
                parentFragmentManager.popBackStack()
            }
        }
    }

}