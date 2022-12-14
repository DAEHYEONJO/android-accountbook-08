package com.example.accountbook.presentation.history

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.accountbook.R
import com.example.accountbook.databinding.FragmentHistoryBinding
import com.example.accountbook.domain.model.HistoriesListItem
import com.example.accountbook.presentation.adapter.HistoryAdapter
import com.example.accountbook.presentation.base.BaseFragment
import com.example.accountbook.presentation.bottomsheet.AppBarBottomSheetFragment
import com.example.accountbook.presentation.viewmodel.HistoryDetailViewModel
import com.example.accountbook.presentation.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HistoryFragment :
    BaseFragment<FragmentHistoryBinding>(R.layout.fragment_history, "HistoryFragment") {

    private val mainViewModel: MainViewModel by activityViewModels()
    private val historyDetailViewModel: HistoryDetailViewModel by activityViewModels()

    @Inject
    lateinit var historyAdapter: HistoryAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = mainViewModel
        initLayout()
        initRecyclerView()
        initObserver()
        initFab()
    }


    private fun initFab() {
        binding.historyAddFab.setOnClickListener {
            historyDetailViewModel.isExpenseChecked.value = mainViewModel.isExpenseLiveData.value!! == 1
            fragmentTransaction()
        }
    }

    private fun fragmentTransaction() {
        parentFragmentManager.commit {
            addToBackStack("HistoryFragment")
            var fragment =
                parentFragmentManager.findFragmentByTag(HistoryDetailFragment::class.simpleName)
            fragment = if (fragment == null) {
                HistoryDetailFragment()
            } else {
                fragment as HistoryDetailFragment
            }
            replace(
                R.id.main_fragment_container_view,
                fragment,
                HistoryDetailFragment::class.simpleName,
            )
            setReorderingAllowed(true)
        }
    }

    private fun initLayout() {
        mainViewModel.setTitle(mainViewModel.curAppbarYear.value!!, mainViewModel.curAppbarMonth.value!!)
        with(binding.historyAppBarLayout) {
            appBarTitleTv.setOnClickListener {
                if (!mainViewModel.isDeleteMode.value!!){
                    AppBarBottomSheetFragment().show(
                        childFragmentManager,
                        AppBarBottomSheetFragment.TAG
                    )
                }
            }
            appBarBackIv.setOnClickListener {
                if (mainViewModel.isDeleteMode.value!!) {
                    mainViewModel.resetDeleteModeProperties()
                    historyAdapter.currentList.map {
                        if (mainViewModel.selectedDeleteItems.value!!.contains(it.id)){
                            it.selected = false
                        }
                    }
                }else{
                    mainViewModel.onClickPreMonthBtn()
                }
            }
            appBarRightIv.setOnClickListener {
                if (mainViewModel.isDeleteMode.value!!){
                    with(mainViewModel) {
                        isDeleteMode.value?.let {
                            deleteHistories()
                        }
                    }
                }else{
                    mainViewModel.onClickNextMonthBtn()
                }
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
                onBodyItemClickListener = object : HistoryAdapter.OnBodyItemClickListener {
                    override fun onBodyItemClick(
                        position: Int,
                        historyListItem: HistoriesListItem
                    ) {
                        with(mainViewModel){
                            if (!isDeleteMode.value!!){
                                fragmentTransaction()
                                Log.e(TAG, "onBodyItemClick: $historyListItem")
                                historyDetailViewModel.setMemberProperties(historyListItem)
                                historyDetailViewModel.isUpdateMode.value = true
                            }else{
                                historyListItem.selected = !historyListItem.selected
                                setDeleteModeTitle()
                                notifyItemChanged(position)
                                selectedDeleteItems.value?.let {
                                    if (it.contains(historyListItem.id)){
                                        it.remove(historyListItem.id)
                                    }else{
                                        it.add(historyListItem.id)
                                    }
                                    if (it.isEmpty()){
                                        resetDeleteModeProperties()
                                    }else{
                                        setDeleteModeTitle()
                                    }
                                }
                            }
                        }
                    }

                    override fun onBodyLongClick(
                        position: Int,
                        historyListItem: HistoriesListItem
                    ) {
                        mainViewModel.isDeleteMode.value?.let {
                            if (!it){
                                mainViewModel.setDeleteModeProperties(historyListItem.id)
                                historyListItem.selected = !it
                                historyAdapter.notifyItemChanged(position)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun initObserver() {
        with(mainViewModel) {
            isExpenseLiveData.observe(viewLifecycleOwner) { isExpense ->
                mainViewModel.getHistoriesTotalData(isExpense)
            }
            historiesTotalData.observe(viewLifecycleOwner) { it ->
                with(mainViewModel){
                    isDeleteMode.value?.let { deleteMode->
                        var size = selectedDeleteItems.value!!.size
                        if (!deleteMode) return@let
                        it!!.historyList.asSequence()
                            .map {
                                if (selectedDeleteItems.value!!.contains(it.id)){
                                    size--
                                    it.selected = true
                                }
                            }.takeWhile { size >= 0 }
                            .toList()
                    }
                }
                historyAdapter.submitList(it!!.historyList)
            }
            curAppbarTitle.observe(viewLifecycleOwner) {
                if (isDeleteMode.value!!) return@observe
                setTotalPrice()
                getHistoriesTotalData(isExpenseLiveData.value!!)
            }
        }
    }
}