package com.example.accountbook.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.accountbook.R

class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}