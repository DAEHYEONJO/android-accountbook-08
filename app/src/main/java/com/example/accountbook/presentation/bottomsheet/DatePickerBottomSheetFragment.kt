package com.example.accountbook.presentation.bottomsheet

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.core.view.marginBottom
import androidx.fragment.app.activityViewModels
import com.example.accountbook.R
import com.example.accountbook.databinding.FragmentAppBarBottomSheetBinding
import com.example.accountbook.databinding.FragmentDatePickerBottomSheetBinding
import com.example.accountbook.presentation.viewmodel.HistoryDetailViewModel
import com.example.accountbook.presentation.viewmodel.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DatePickerBottomSheetFragment : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "DatePickerBottomSheetFragment"
    }

    private var _binding: FragmentDatePickerBottomSheetBinding? = null
    private val binding: FragmentDatePickerBottomSheetBinding get() = checkNotNull(_binding)
    private val historyDetailViewModel: HistoryDetailViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDatePickerBottomSheetBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDatePicker()
    }

    private fun initDatePicker() {
        with(binding.bottomSheetDatePicker) {
            with(historyDetailViewModel) {
                if (bottomSheetSelectedYear.value!=0){
                    updateDate(bottomSheetSelectedYear.value!!, bottomSheetSelectedMonth.value!!, bottomSheetSelectedDay.value!!)
                }
                binding.bottomSheetDatePickerAddBtn.setOnClickListener {
                    bottomSheetSelectedYear.value = year
                    bottomSheetSelectedMonth.value = month
                    bottomSheetSelectedDay.value = dayOfMonth
                    dismiss()
                }
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            setupRatio(bottomSheetDialog)
        }
        return dialog
    }

    @SuppressLint("RestrictedApi", "VisibleForTests")
    private fun setupRatio(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet =
            bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as View
        val behavior = BottomSheetBehavior.from(bottomSheet)
        with(behavior) {
            disableShapeAnimations()
            state = BottomSheetBehavior.STATE_EXPANDED
            BottomSheetBehavior.from(bottomSheet).skipCollapsed = true
            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {}
                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    binding.bottomSheetDatePickerAddBtn.y =
                        ((bottomSheet.parent as View).height - bottomSheet.top - binding.bottomSheetDatePickerAddBtn.height
                                - binding.bottomSheetDatePickerAddBtn.marginBottom).toFloat()
                }
            }) // footer 적용을 위함
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