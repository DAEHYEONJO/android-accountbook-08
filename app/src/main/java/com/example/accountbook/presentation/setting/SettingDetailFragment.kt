package com.example.accountbook.presentation.setting

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import com.example.accountbook.R
import com.example.accountbook.data.model.Categories
import com.example.accountbook.data.model.Payments
import com.example.accountbook.databinding.FragmentSettingDetailBinding
import com.example.accountbook.presentation.model.ColorItem
import com.example.accountbook.presentation.adapter.SettingColorAdapter
import com.example.accountbook.presentation.base.BaseFragment
import com.example.accountbook.presentation.viewmodel.HistoryDetailViewModel
import com.example.accountbook.presentation.viewmodel.SettingViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingDetailFragment :
    BaseFragment<FragmentSettingDetailBinding>(
        R.layout.fragment_setting_detail,
        SettingDetailFragment::class.simpleName!!
    ) {

    private val settingViewModel: SettingViewModel by activityViewModels()
    private val historyDetailViewModel: HistoryDetailViewModel by activityViewModels()

    @Inject
    lateinit var settingColorAdapter: SettingColorAdapter
    private val expenseColors: List<Int> by lazy { resources.getIntArray(R.array.expense_category_color_array).toList() }
    private val incomeColors: List<Int> by lazy { resources.getIntArray(R.array.income_category_color_array).toList() }
    private lateinit var expenseColorList: List<ColorItem>
    private lateinit var incomeColorList: List<ColorItem>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = settingViewModel
        initColorProperties()
        initLayout()
        initObserver()
    }

    private fun initColorList(){
        expenseColorList = resources.getIntArray(R.array.expense_category_color_array).toList()
            .mapIndexed { index, i ->
                ColorItem(
                    color = i,
                    isTouched = (index == settingViewModel.selectedExpenseColorPos.value)
                )
            }.toList()
        incomeColorList = resources.getIntArray(R.array.income_category_color_array).toList()
            .mapIndexed { index, i ->
                ColorItem(
                    color = i,
                    isTouched = (index == settingViewModel.selectedIncomeColorPos.value)
                )
            }
    }


    private fun initColorProperties() {
        with(settingViewModel){
            if (isPaymentMode.value == true) {
                initColorList()
                return@with
            }
            if (isUpdateMode.value == true) {
                if (isExpenseMode.value == true) {
                    expenseCategoryItemList.value?.let {
                        val curColor = Color.parseColor(it.find { categories ->
                            categories.categoryId == curCategoryId.value
                        }?.let { it.labelColor })

                        selectedExpenseColorPos.value = expenseColors.run {
                            val index = indexOf(find { it == curColor })
                            if(index == -1) 0 else index
                        }
                    }
                } else {
                    incomeCategoryItemList.value?.let {
                        val curColor = Color.parseColor(it.find { categories ->
                            categories.categoryId == curCategoryId.value
                        }?.let { it.labelColor })

                        selectedIncomeColorPos.value = incomeColors.run {
                            val index = indexOf(find { it == curColor })
                            if(index == -1) 0 else index
                        }
                    }
                }
            }
            initColorList()
        }
    }

    private fun initObserver() {
        with(settingViewModel) {
            isExpenseMode.observe(viewLifecycleOwner) { flag ->
                if (isPaymentMode.value == true) return@observe
                settingColorAdapter.run {
                    preSelectedPos = if (flag) settingViewModel.selectedExpenseColorPos.value!!
                    else settingViewModel.selectedIncomeColorPos.value!!
                    colorList = if (flag) expenseColorList else incomeColorList
                    notifyDataSetChanged()
                }
            }
        }
    }

    private fun initLayout() {
        with(binding) {
            settingDetailAppBarBackIv.setOnClickListener {
                parentFragmentManager.popBackStack()
                settingViewModel.resetProperties()
            }
            settingDetailColorRv.adapter = settingColorAdapter.apply {
                listener = object : SettingColorAdapter.OnItemClickListener {
                    override fun onClick(position: Int, item: ColorItem) {
                        item.isTouched = !item.isTouched
                        settingColorAdapter.notifyItemChanged(position)
                        if (settingViewModel.isExpenseMode.value == true) {
                            settingViewModel.selectedExpenseColorPos.value = position
                        } else {
                            settingViewModel.selectedIncomeColorPos.value = position
                        }
                    }
                }
            }
            settingDetailNameEt.doAfterTextChanged {
                it?.let {
                    settingViewModel.isButtonEnabled.value = it.isNotEmpty()
                }
            }
            settingDetailAddBtn.setOnClickListener {
                with(settingViewModel){
                    if (!isUpdateMode.value!!){ // 추가모드
                        if (isPaymentMode.value!!){ // payment 추가 모드
                            insertPayments(
                                Payments(payment = binding.settingDetailNameEt.text.toString())
                            )
                        }else if (isExpenseMode.value!!){ // 지출 카테고리 추가 모드
                            val intColor = expenseColorList[selectedExpenseColorPos.value!!].color
                            val labelColor = String.format("#%06X", 0xFFFFFF and intColor)
                            insertCategories(
                                Categories(
                                    category = binding.settingDetailNameEt.text.toString(),
                                    labelColor = labelColor,
                                    isExpense = 1
                                )
                            )
                        }else{ // 수입 카테고리 추가 모드
                            val intColor = incomeColorList[selectedIncomeColorPos.value!!].color
                            val labelColor = String.format("#%06X", 0xFFFFFF and intColor)
                            insertCategories(
                                Categories(
                                    category = binding.settingDetailNameEt.text.toString(),
                                    labelColor = labelColor,
                                    isExpense = 0
                                )
                            )
                        }
                    }else{ // 업데이트 모드
                        if (isPaymentMode.value!!){ // payment 업뎃 모드
                            updatePayments(
                                Payments(
                                    paymentId = curPaymentId.value!!,
                                    payment = binding.settingDetailNameEt.text.toString()
                                )
                            )
                        }else if (isExpenseMode.value!!){ // 지출 카테고리 업뎃 모드
                            val intColor = expenseColorList[selectedExpenseColorPos.value!!].color
                            val labelColor = String.format("#%06X", 0xFFFFFF and intColor)
                            updateCategories(
                                Categories(
                                    categoryId = curCategoryId.value!!,
                                    category = binding.settingDetailNameEt.text.toString(),
                                    labelColor = labelColor,
                                    isExpense = 1
                                )
                            )
                        }else{ // 수입 카테고리 업뎃 모드
                            val intColor = incomeColorList[selectedIncomeColorPos.value!!].color
                            val labelColor = String.format("#%06X", 0xFFFFFF and intColor)
                            updateCategories(
                                Categories(
                                    categoryId = curCategoryId.value!!,
                                    category = binding.settingDetailNameEt.text.toString(),
                                    labelColor = labelColor,
                                    isExpense = 0
                                )
                            )
                        }
                    }
                    isButtonEnabled.value = false
                    parentFragmentManager.popBackStack()
                    settingViewModel.resetProperties()
                }
            }
        }
    }

    override fun onDestroyView() {

        super.onDestroyView()
    }
}
