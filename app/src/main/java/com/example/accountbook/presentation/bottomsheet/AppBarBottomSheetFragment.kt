package com.example.accountbook.presentation.bottomsheet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.activityViewModels
import com.example.accountbook.R
import com.example.accountbook.databinding.FragmentAppBarBottomSheetBinding
import com.example.accountbook.presentation.viewmodel.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.FragmentScoped
import java.util.*
import javax.inject.Singleton

@AndroidEntryPoint
class AppBarBottomSheetFragment : BottomSheetDialogFragment() {

    companion object{
        const val TAG = "AppBarBottomSheetFragment"
    }

    private var _binding: FragmentAppBarBottomSheetBinding? = null
    private val binding: FragmentAppBarBottomSheetBinding get() = checkNotNull(_binding)
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAppBarBottomSheetBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initNumberPicker()
    }

    private fun initNumberPicker() {
        with(binding){
            with(bottomSheetYearPicker){
                minValue = 2000
                maxValue = 2022
                value = mainViewModel.curAppbarYear.value!!
            }
            with(bottomSheetMonthPicker){
                minValue = 1
                maxValue = 12
                value = mainViewModel.curAppbarMonth.value!!
            }
            bottomSheetSelectBtn.setOnClickListener {
                with(mainViewModel){
                    curAppbarYear.value = bottomSheetYearPicker.value
                    curAppbarMonth.value = bottomSheetMonthPicker.value
                }
                dismiss()
            }
        }
    }


    override fun getTheme(): Int {
        return R.style.BottomSheetDialog_Rounded
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}