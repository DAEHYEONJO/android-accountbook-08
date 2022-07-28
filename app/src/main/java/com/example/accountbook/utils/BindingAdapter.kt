package com.example.accountbook.utils

import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import com.example.accountbook.R

@BindingAdapter("app:setBackgroundSyncWithToggleBtn")
fun ConstraintLayout.setBackgroundSyncWithToggleBtn(checked: Boolean){
    if (this.id == R.id.checkbox_history_expense_layout){ // 지출의 경우
        if (checked){
            setBackgroundResource(R.drawable.checkbox_checked_true_right_background_radius_10)
        }else{
            setBackgroundResource(R.drawable.checkbox_checked_false_right_background_radius_10)
        }
    }else{ // 수입의 경우
        if (checked){
            setBackgroundResource(R.drawable.checkbox_checked_true_left_background_radius_10)
        }else{
            setBackgroundResource(R.drawable.checkbox_checked_false_left_background_radius_10)
        }
    }
}

@BindingAdapter("app:setPriceText")
fun TextView.setPriceText(price: String){

}