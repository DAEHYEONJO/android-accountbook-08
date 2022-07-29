package com.example.accountbook.presentation

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.accountbook.R
import com.example.accountbook.databinding.FragmentHistoryBinding
import com.example.accountbook.presentation.adapter.HistoryAdapter
import com.example.accountbook.presentation.base.BaseFragment
import com.example.accountbook.presentation.bottomsheet.AppBarBottomSheetFragment
import com.example.accountbook.presentation.viewmodel.MainViewModel
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

    @Inject
    lateinit var historyAdapter: HistoryAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = mainViewModel
        initLayout()
        initRecyclerView()
        initObserver()
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
            adapter = historyAdapter
        }
    }

    private fun initObserver() {
        with(mainViewModel) {
            isExpenseLiveData.observe(viewLifecycleOwner) { isExpense ->
                Log.e(TAG, "isExpenseLiveData: ", )
                mainViewModel.getHistoriesTotalData(isExpense)
            }
            historiesTotalData.observe(viewLifecycleOwner) {
                Log.e(TAG, "historiesTotalData: ${it.historyList}", )
                historyAdapter.submitList(it.historyList)
            }
            curAppbarTitle.observe(viewLifecycleOwner) {
                Log.e(TAG, "curAppbarTitle: $it ${curAppbarYear.value} ${curAppbarMonth.value}", )
                setTotalPrice()
                getHistoriesTotalData(isExpenseLiveData.value!!)
            }
        }
    }
}