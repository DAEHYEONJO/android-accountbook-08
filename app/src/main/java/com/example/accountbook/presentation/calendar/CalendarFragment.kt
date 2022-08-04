package com.example.accountbook.presentation.calendar

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.window.layout.WindowMetrics
import androidx.window.layout.WindowMetricsCalculator
import com.example.accountbook.R
import com.example.accountbook.databinding.FragmentCalendarBinding
import com.example.accountbook.presentation.adapter.CalendarAdapter
import com.example.accountbook.presentation.base.BaseFragment
import com.example.accountbook.presentation.bottomsheet.AppBarBottomSheetFragment
import com.example.accountbook.presentation.viewmodel.CalendarViewModel
import com.example.accountbook.presentation.viewmodel.MainViewModel
import com.example.accountbook.utils.getCommaPriceString
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CalendarFragment : BaseFragment<FragmentCalendarBinding>(R.layout.fragment_calendar, "CalendarFragment") {

    private val mainViewModel: MainViewModel by activityViewModels ()
    private val calendarViewModel: CalendarViewModel by activityViewModels()
    @Inject lateinit var calendarAdapter: CalendarAdapter

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = mainViewModel

        initLayout()
        initObserver()
    }

    private fun getDeviceWidth(): Int {
        val windowMetrics: WindowMetrics = WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(requireActivity())
        return windowMetrics.bounds.width()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initObserver() {
        with(mainViewModel){
            curAppbarTitle.observe(viewLifecycleOwner){ title ->
                binding.calendarAppBarLayout.appBarTitleTv.text = title
                setTotalPrice()
                calendarViewModel.fetchCalendarItemList(curAppbarYear.value!!, curAppbarMonth.value!!)
            }
            calendarViewModel.calendarItemList.observe(viewLifecycleOwner){ calendarItemList ->
                calendarItemList!!.forEach {
                    Log.d(TAG, "initObserver: $it")
                    calendarAdapter.dayList = calendarItemList
                    calendarAdapter.notifyDataSetChanged()
                }
            }
            curMonthExpense.observe(viewLifecycleOwner){
                binding.calendarExpenseTv.text = if (it!=0L) "-${getCommaPriceString(it)}" else "0"
            }
        }
    }

    private fun initLayout() {
        with(binding){
            with(calendarAppBarLayout){
                appBarRightIv.setOnClickListener {
                    mainViewModel.onClickNextMonthBtn()
                }
                appBarBackIv.setOnClickListener {
                    mainViewModel.onClickPreMonthBtn()
                }
                appBarTitleTv.setOnClickListener {
                    AppBarBottomSheetFragment().show(
                        childFragmentManager,
                        AppBarBottomSheetFragment.TAG
                    )
                }
            }
            with(calendarRv){
                layoutManager = GridLayoutManager(requireContext(), 7)
                isNestedScrollingEnabled = false
                adapter = calendarAdapter.apply {
                    deviceWidth = getDeviceWidth()
                }
            }

        }
    }

}