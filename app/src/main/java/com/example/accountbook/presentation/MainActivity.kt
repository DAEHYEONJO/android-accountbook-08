package com.example.accountbook.presentation

import android.database.sqlite.SQLiteOpenHelper
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.commit
import com.example.accountbook.R
import com.example.accountbook.data.db.AccountBookDbHelper
import com.example.accountbook.data.model.Payments
import com.example.accountbook.databinding.ActivityMainBinding
import com.example.accountbook.presentation.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity"
        const val HISTORY_TAG = "HistoryFragment"
        const val CALENDAR_TAG = "CalendarFragment"
        const val STATISTICS_TAG = "StatisticsFragment"
        const val SETTING_TAG = "CalendarFragment"
    }

    private val mainViewModel: MainViewModel by viewModels()

    private val binding: ActivityMainBinding by lazy {
        DataBindingUtil.setContentView(
            this@MainActivity,
            R.layout.activity_main
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewModel = mainViewModel
        initBinding()
        initBottomNavigation()
    }

    private fun initBottomNavigation() {
        if (supportFragmentManager.fragments.isEmpty()) {
            setFragment(mainViewModel.curSelectedMenuItemId.value!!)
        }

        binding.mainBottomNavView.setOnItemSelectedListener {
            mainViewModel.curSelectedMenuItemId.value = it.itemId
            if (!it.isChecked) {
                setFragment(it.itemId)
            }
            true
        }
    }

    private fun setFragment(menuItemId: Int){
        when (menuItemId){
            R.id.bottom_nav_item_history -> supportFragmentManager.commit {
                replace(R.id.main_fragment_container_view, HistoryFragment())
            }
            R.id.bottom_nav_item_calendar -> supportFragmentManager.commit {
                replace(R.id.main_fragment_container_view, CalendarFragment())
            }
            R.id.bottom_nav_item_statistics -> supportFragmentManager.commit {
                replace(R.id.main_fragment_container_view, StatisticsFragment())
            }
            R.id.bottom_nav_item_setting -> supportFragmentManager.commit {
                replace(R.id.main_fragment_container_view, SettingFragment())
            }
        }
    }

    private fun initBinding() {
        with(binding){
            lifecycleOwner = this@MainActivity
            viewModel = mainViewModel
        }
    }
}