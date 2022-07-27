package com.example.accountbook.presentation

import android.database.sqlite.SQLiteOpenHelper
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
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
        initBinding()

    }

    private fun initBinding() {
        with(binding){
            lifecycleOwner = this@MainActivity
            viewModel = mainViewModel
        }
    }
}