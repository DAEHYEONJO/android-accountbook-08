package com.example.accountbook.presentation.history

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.accountbook.R
import com.example.accountbook.databinding.FragmentHistoryBinding
import com.example.accountbook.domain.model.HEADER
import com.example.accountbook.domain.model.HistoriesListItem
import com.example.accountbook.presentation.adapter.HistoryAdapter
import com.example.accountbook.presentation.base.BaseFragment
import com.example.accountbook.presentation.bottomsheet.AppBarBottomSheetFragment
import com.example.accountbook.presentation.viewmodel.HistoryDetailViewModel
import com.example.accountbook.presentation.viewmodel.MainViewModel
import com.example.accountbook.utils.dateToYearMonthDay
import com.example.accountbook.utils.getStartEndOfCurMonth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HistoryFragment :
    BaseFragment<FragmentHistoryBinding>(R.layout.fragment_history, "HistoryFragment") {

    private val mainViewModel: MainViewModel by activityViewModels()
    private val historyDetailViewModel: HistoryDetailViewModel by activityViewModels ()

    @Inject lateinit var historyAdapter: HistoryAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = mainViewModel
        initAppbar()
        initLayout()
        initRecyclerView()
        initObserver()
        initFab()
    }

    private fun initAppbar() {
        with(binding.historyAppBarLayout) {
            appBarBackIv.setOnClickListener {
                mainViewModel.onClickPreMonthBtn()
            }
            appBarRightIv.setOnClickListener {
                mainViewModel.onClickNextMonthBtn()
            }
        }
    }

    private fun initFab() {
        binding.historyAddFab.setOnClickListener {
            fragmentTransaction()
        }
    }

    private fun fragmentTransaction(){
        parentFragmentManager.commit {
            addToBackStack("HistoryFragment")
            var fragment = parentFragmentManager.findFragmentByTag(HistoryDetailFragment::class.simpleName)
            fragment = if (fragment == null){
                HistoryDetailFragment()
            }else{
                fragment as HistoryDetailFragment
            }
            replace(R.id.main_fragment_container_view, fragment, HistoryDetailFragment::class.simpleName)
        }
    }

    private fun initLayout() {
        with(binding.historyAppBarLayout) {
            appBarTitleTv.setOnClickListener {
                AppBarBottomSheetFragment().show(
                    childFragmentManager,
                    AppBarBottomSheetFragment.TAG
                )
            }
        }
    }

    private fun initRecyclerView() {
        with(binding.historyRecyclerView) {
            val animator = itemAnimator
            if (animator is SimpleItemAnimator) {
                animator.supportsChangeAnimations = false
            }
            adapter = historyAdapter.apply {
                onBodyItemClickListener = object : HistoryAdapter.OnBodyItemClickListener{
                    override fun onBodyItemClick(historyListItem: HistoriesListItem) {
                        fragmentTransaction()
                        Log.e(TAG, "onBodyItemClick: $historyListItem", )
                        historyDetailViewModel.setMemberProperties(historyListItem)
                        historyDetailViewModel.isUpdateMode.value = true
                    }
                }
            }
        }
    }

    private fun initObserver() {
        with(mainViewModel) {
            isExpenseLiveData.observe(viewLifecycleOwner) { isExpense ->
                Log.e(TAG, "isExpenseLiveData: ")
                mainViewModel.getHistoriesTotalData(isExpense)
            }
            historiesTotalData.observe(viewLifecycleOwner) { it ->
                it.historyList.forEach {
                    if (it.viewType!= HEADER) Log.e(TAG, "historyData: $it", )
                }
                historyAdapter.submitList(it.historyList)
            }
            curAppbarTitle.observe(viewLifecycleOwner) {
                Log.e(TAG, "curAppbarTitle: $it ${curAppbarYear.value} ${curAppbarMonth.value}")
                binding.historyAppBarLayout.appBarTitleTv.text = it
                setTotalPrice()
                getHistoriesTotalData(isExpenseLiveData.value!!)
            }
        }
    }
}