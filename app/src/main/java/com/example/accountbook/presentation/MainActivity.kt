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
import com.example.accountbook.data.model.Categories
import com.example.accountbook.data.model.Histories
import com.example.accountbook.data.model.Payments
import com.example.accountbook.databinding.ActivityMainBinding
import com.example.accountbook.presentation.adapter.HistoryAdapter
import com.example.accountbook.presentation.viewmodel.MainViewModel
import com.example.accountbook.utils.getStartEndOfCurMonth
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.time.LocalDate

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity"
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
        //testQueries()
    }

//    private fun testQueries() {
//        val dbHelper = AccountBookDbHelper(applicationContext)
//        with(dbHelper) {
////            val formatter = SimpleDateFormat("yyyy M d")
////            val str1 = "2022 7 29"
////            val date1 = formatter.parse(str1).time
////            val str2 = "2022 7 16"
////            val date2 = formatter.parse(str2).time
////            val str3 = "2022 7 20"
////            val date3 = formatter.parse(str3).time
////            val str4 = "2022 7 14"
////            val date4 = formatter.parse(str4).time
////            val str5 = "2022 7 20"
////            val date5 = formatter.parse(str5).time
////            val str6 = "2022 7 20"
////            val date6 = formatter.parse(str6).time
////            val str7 = "2022 7 14"
////            val date7 = formatter.parse(str7).time
////            val str8 = "2022 6 29"
////            val date8 = formatter.parse(str8).time
////            val str9 = "2022 6 30"
////            val date9 = formatter.parse(str9).time
////            val str10 = "2022 6 3"
////            val date10 = formatter.parse(str10).time
////
////
////            insertHistory(
////                Histories(
////                    date = date1,
////                    price = 10000,
////                    description = "부자되게 해주세요1",
////                    payments = getPaymentsById(1)!!,
////                    categories = getCategoriesById(2)!!,
////                )
////            )
////            insertHistory(
////                Histories(
////                    date = date2,
////                    price = 12000,
////                    description = "부자되게 해주세요2",
////                    payments = getPaymentsById(2)!!,
////                    categories = getCategoriesById(2)!!,
////                )
////            )
////            insertHistory(
////                Histories(
////                    date = date3,
////                    price = 20000,
////                    description = "부자되게 해주세요3",
////                    payments = getPaymentsById(2)!!,
////                    categories = getCategoriesById(2)!!,
////                )
////            )
////            insertHistory(
////                Histories(
////                    date = date4,
////                    price = 6000,
////                    description = "부자되게 해주세요4",
////                    payments = getPaymentsById(3)!!,
////                    categories = getCategoriesById(2)!!,
////                )
////            )
////            insertHistory(
////                Histories(
////                    date = date5,
////                    price = 200000000,
////                    description = "부자되게 해주세요5",
////                    payments = getPaymentsById(2)!!,
////                    categories = getCategoriesById(4)!!,
////                )
////            )
////            insertHistory(
////                Histories(
////                    date = date6,
////                    price = 6000,
////                     description = "부자되게 해주세요6",
////                    payments = getPaymentsById(3)!!,
////                    categories = getCategoriesById(5)!!,
////                )
////            )
////            insertHistory(
////                Histories(
////                    date = date7,
////                    price = 3000,
////                    description = "용돈 좋아",
////                    payments = getPaymentsById(3)!!,
////                    categories = getCategoriesById(5)!!,
////                )
////            )
////            insertHistory(
////                Histories(
////                    date = date8,
////                    price = 2000,
////                    description = "용돈 좋아2",
////                    payments = getPaymentsById(3)!!,
////                    categories = getCategoriesById(5)!!,
////                )
////            )
////            insertHistory(
////                Histories(
////                    date = date9,
////                    price = 1000,
////                    description = "용돈 좋아3",
////                    payments = getPaymentsById(3)!!,
////                    categories = getCategoriesById(5)!!,
////                )
////            )
////            insertHistory(
////                Histories(
////                    date = date10,
////                    price = 9000,
////                    description = "용돈 좋아4",
////                    payments = getPaymentsById(3)!!,
////                    categories = getCategoriesById(5)!!,
////                )
////            )
////            getAllPayments().forEach {
////                Log.e(TAG, "testQueries: $it", )
////            }
////            getAllCategories().forEach {
////                Log.e(TAG, "testQueries: $it", )
////            }
////            val(start, end) = getStartEndOfCurMonth(
////                startMonth = 6,
////                startYear = 2022
////            )
////            val ret = getHistoriesTotalData(0, start, end)
////            Log.e(TAG, "수입: ${ret.totalIncome}", )
////            Log.e(TAG, "지출: ${ret.totalExpense}", )
////            ret.historyList.forEach {
////                Log.e(TAG, "testQueries: $it", )
////            }
//        }
//    }

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

    private fun setFragment(menuItemId: Int) {
        when (menuItemId) {
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
        with(binding) {
            lifecycleOwner = this@MainActivity
            viewModel = mainViewModel
        }
    }
}