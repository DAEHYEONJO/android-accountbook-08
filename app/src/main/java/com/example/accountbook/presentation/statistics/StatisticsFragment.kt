package com.example.accountbook.presentation.statistics

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.example.accountbook.R
import com.example.accountbook.databinding.FragmentStatisticsBinding
import com.example.accountbook.domain.model.StatisticsItem
import com.example.accountbook.presentation.adapter.StatisticsAdapter
import com.example.accountbook.presentation.base.BaseFragment
import com.example.accountbook.presentation.bottomsheet.AppBarBottomSheetFragment
import com.example.accountbook.presentation.viewmodel.MainViewModel
import com.example.accountbook.presentation.viewmodel.StatisticsViewModel
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class StatisticsFragment :
    BaseFragment<FragmentStatisticsBinding>(R.layout.fragment_statistics, "StatisticsFragment") {

    private val mainViewModel: MainViewModel by activityViewModels()
    private val statisticsViewModel: StatisticsViewModel by activityViewModels()
    @Inject lateinit var statisticsAdapter: StatisticsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = mainViewModel
        initObserver()
        initLayout()
    }

    private fun initLayout() {
        with(binding){
            with(statisticsAppBarLayout){
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
            statisticsRv.adapter = statisticsAdapter
        }
    }

    private fun initObserver() {
        with(mainViewModel) {
            curAppbarTitle.observe(viewLifecycleOwner) { title ->
                Log.e(TAG, "initObserver: $title")
                setTotalPrice()
                val (start, end) = getStartEndLongValue()
                statisticsViewModel.getStatisticsItemList(start = start, end = end)
            }
        }
        with(statisticsViewModel) {
            statisticsItem.observe(viewLifecycleOwner) { statisticsItemList ->
                statisticsItemList!!.forEach {
                    Log.e(TAG, "initObserver: $it")
                }
                statisticsAdapter.apply {
                    this.statisticsItemList = statisticsItemList
                    notifyDataSetChanged()
                }
                initPieChart(statisticsItemList)
            }
        }
    }

    private fun initPieDataSet(statisticsItemList: List<StatisticsItem>): PieData {

        val pieDataList = statisticsItemList.map {
            PieEntry(it.percentage.toFloat(), "")
        }.toList()

        val colorList = statisticsItemList.map {
            Color.parseColor(it.categories.labelColor)
        }.toList()

        val pieDataSet = PieDataSet(pieDataList, "").apply {
            colors = colorList
            setDrawValues(false)
        }
        return PieData(pieDataSet)
    }

    private fun initPieChart(statisticsItemList: List<StatisticsItem>) {
        with(binding.statisticsPieChart) {

            setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.primary_off_white_100
                )
            )
            setHoleColor(ContextCompat.getColor(requireContext(), R.color.primary_off_white_100))
            setExtraOffsets(0f, 0f, 0f, 0f)
            transparentCircleRadius = 0f // 가운데 그림자 부분 지름
            this.holeRadius = 62.3f
            description.isEnabled = false
            // 왼쪽 아래 color들 삭제
            legend.isEnabled = false
            animateXY(1000, 1000)
            data = initPieDataSet(statisticsItemList)

        }
    }


}