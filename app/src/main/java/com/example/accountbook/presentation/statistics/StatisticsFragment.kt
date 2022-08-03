package com.example.accountbook.presentation.statistics

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.example.accountbook.R
import com.example.accountbook.databinding.FragmentStatisticsBinding
import com.example.accountbook.presentation.base.BaseFragment
import com.example.accountbook.presentation.bottomsheet.AppBarBottomSheetFragment
import com.example.accountbook.presentation.viewmodel.MainViewModel
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StatisticsFragment :
    BaseFragment<FragmentStatisticsBinding>(R.layout.fragment_statistics, "StatisticsFragment") {

    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = mainViewModel
        initObserver()
        initPieChart()
        initAppbar()
    }

    private fun initAppbar() {
        with(binding.statisticsAppBarLayout){
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
    }

    private fun initObserver() {
        with(mainViewModel) {
            curAppbarTitle.observe(viewLifecycleOwner){ title ->
                Log.e(TAG, "initObserver: $title", )
                setTotalPrice()
            }
        }
    }

    private fun initPieChart() {
        with(binding.statisticsPieChart) {

            setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.primary_off_white_100
                )
            )
            setHoleColor(ContextCompat.getColor(
                    requireContext(),R.color.primary_off_white_100
                )
            )
            setExtraOffsets(0f,0f,0f,0f)
            transparentCircleRadius = 0f // 가운데 그림자 부분 지름
            this.holeRadius = 62.3f
            val pieDataList = ArrayList<PieEntry>()

            repeat(5) {
                val value = (it+1) * 10.0f
                val pieEntry = PieEntry(value, "")
                pieDataList.add(pieEntry)
            }
            val colorArray = intArrayOf(
                R.color.secondary_green1_100,
                R.color.primary_yellow_100,
                R.color.primary_red_100,
                R.color.black,
                R.color.secondary_blue1_100
            )

            val pieDataSet = PieDataSet(pieDataList,"")
            pieDataSet.setColors(colorArray, requireContext())
            // 그래프 value 삭제
            pieDataSet.setDrawValues(false)
            // 오른쪽 description 삭제
            description.isEnabled = false
            // 왼쪽 아래 color들 삭제
            legend.isEnabled = false

            animateXY(1000, 1000)
            val pieData = PieData(pieDataSet)
            data = pieData

        }
    }


}