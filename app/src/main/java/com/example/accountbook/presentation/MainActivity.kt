package com.example.accountbook.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.core.view.doOnLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.commit
import com.example.accountbook.R
import com.example.accountbook.data.db.AccountBookDbHelper
import com.example.accountbook.data.model.Categories
import com.example.accountbook.data.model.Payments
import com.example.accountbook.databinding.ActivityMainBinding
import com.example.accountbook.presentation.calendar.CalendarFragment
import com.example.accountbook.presentation.history.HistoryDetailFragment
import com.example.accountbook.presentation.history.HistoryFragment
import com.example.accountbook.presentation.setting.SettingDetailFragment
import com.example.accountbook.presentation.setting.SettingFragment
import com.example.accountbook.presentation.statistics.StatisticsFragment
import com.example.accountbook.presentation.viewmodel.HistoryDetailViewModel
import com.example.accountbook.presentation.viewmodel.MainViewModel
import com.example.accountbook.presentation.viewmodel.SettingViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MainActivity"
    }

    private val mainViewModel: MainViewModel by viewModels()
    private val historyDetailViewModel: HistoryDetailViewModel by viewModels()
    private val settingViewModel: SettingViewModel by viewModels()
    private val binding: ActivityMainBinding by lazy {
        DataBindingUtil.setContentView(
            this@MainActivity,
            R.layout.activity_main
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewModel = mainViewModel
        historyDetailViewModel.fetchData()
        initBinding()
        initBottomNavigation()
    }

    private fun initBottomNavigation() {
        binding.mainBottomNavView.doOnLayout { 
            settingViewModel.bottomNavigationHeight = it.height
            Log.e(TAG, "initBottomNavigation: ${it.height}", )
        }
        if (supportFragmentManager.fragments.isEmpty()) {
            setFragment(mainViewModel.curSelectedMenuItemId.value!!)
        }

        binding.mainBottomNavView.setOnItemSelectedListener {

            with(mainViewModel){
                isDeleteMode.value?.let { deleteMode ->
                    if (deleteMode){
                        resetDeleteModeProperties()
                    }
                }
                curSelectedMenuItemId.value = it.itemId
            }

            if (!it.isChecked) {
                setFragment(it.itemId)
            }

            true
        }
    }

    private fun setFragment(menuItemId: Int) {
        var fragment = supportFragmentManager.findFragmentByTag(menuItemId.toString())
        Log.e(TAG, "setFragment: backstackCount: ${supportFragmentManager.backStackEntryCount}", )
        supportFragmentManager.popBackStack()
        if (fragment == null){
            when(menuItemId){
                R.id.bottom_nav_item_history -> fragment = HistoryFragment()
                R.id.bottom_nav_item_calendar -> fragment = CalendarFragment()
                R.id.bottom_nav_item_statistics -> fragment = StatisticsFragment()
                R.id.bottom_nav_item_setting -> fragment = SettingFragment()
            }
        }else{
            when(menuItemId){
                R.id.bottom_nav_item_history -> {
                    fragment as HistoryFragment
                }
                R.id.bottom_nav_item_calendar -> fragment as CalendarFragment
                R.id.bottom_nav_item_statistics -> fragment as StatisticsFragment
                R.id.bottom_nav_item_setting -> fragment as SettingFragment
            }
        }
        supportFragmentManager.commit {
            replace(R.id.main_fragment_container_view, fragment!!, menuItemId.toString())
        }
    }

    private fun initBinding() {
        with(binding) {
            lifecycleOwner = this@MainActivity
            viewModel = mainViewModel
        }
    }

    override fun onBackPressed() {
        with(supportFragmentManager){
            if (backStackEntryCount!=2){
                historyDetailViewModel.resetMemberProperties()
            }else{
                //popBackStack()
            }
            Log.e(TAG, "onBackPressed: $backStackEntryCount", )
            Log.e(TAG, "onBackPressed: ${this.fragments}", )
            if(this.fragments.first() !is HistoryFragment){
                if (fragments.size==1 && fragments.last() !is HistoryDetailFragment && fragments.last() !is SettingDetailFragment){
                    binding.mainBottomNavView.selectedItemId = R.id.bottom_nav_item_history
                    setFragment(R.id.bottom_nav_item_history)
                    return
                }
            }
        }
        super.onBackPressed()
    }
}