package com.example.accountbook.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.accountbook.R
import com.example.accountbook.databinding.FragmentHistoryBinding
import com.example.accountbook.presentation.adapter.HistoryAdapter
import com.example.accountbook.presentation.base.BaseFragment
import com.example.accountbook.presentation.bottomsheet.AppBarBottomSheetFragment
import com.example.accountbook.presentation.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HistoryFragment : BaseFragment<FragmentHistoryBinding>(R.layout.fragment_history, "HistoryFragment") {

    private val mainViewModel: MainViewModel by activityViewModels()
    @Inject
    lateinit var historyAdapter: HistoryAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = mainViewModel
        initLayout()
        initRecyclerView()
    }

    private fun initLayout(){
        with(binding.historyAppBarLayout){
            appBarTitleTv.setOnClickListener {
                AppBarBottomSheetFragment().show(childFragmentManager, AppBarBottomSheetFragment.TAG)
            }
        }
    }

    private fun initRecyclerView(){
        with(binding.historyRecyclerView){
            adapter = historyAdapter
        }

        mainViewModel.historiesTotalData.observe(binding.lifecycleOwner!!){
            historyAdapter.submitList(it.historyList)
        }
    }

}