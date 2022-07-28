package com.example.accountbook.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.accountbook.R
import com.example.accountbook.databinding.FragmentHistoryBinding
import com.example.accountbook.presentation.base.BaseFragment
import com.example.accountbook.presentation.bottomsheet.AppBarBottomSheetFragment
import com.example.accountbook.presentation.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryFragment : BaseFragment<FragmentHistoryBinding>(R.layout.fragment_history, "HistoryFragment") {

    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = mainViewModel
        initLayout()
    }

    private fun initLayout(){
        with(binding.historyAppBarLayout){
            appBarTitleTv.setOnClickListener {
                AppBarBottomSheetFragment().show(childFragmentManager, AppBarBottomSheetFragment.TAG)
            }
        }
    }

}