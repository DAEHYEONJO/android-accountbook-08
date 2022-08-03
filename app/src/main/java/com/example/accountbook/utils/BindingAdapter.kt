package com.example.accountbook.utils

import android.graphics.Color
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.example.accountbook.R
import org.w3c.dom.Text
import java.time.Year
import java.util.*

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
fun TextView.setPriceText(price: Long){
    text = getCommaPriceString(price)
}

@BindingAdapter("app:setDateMdEEType")
fun TextView.setDateMdEEType(date: Date){
    text = dateToStringMdEEType(date)
}

@BindingAdapter("year", "month", "day")
fun TextView.stringToYyyyMdEEString(year: Int, month: Int, day: Int){
    text = com.example.accountbook.utils.stringToYyyyMdEEString(year, month, day)
}

@BindingAdapter("app:setCategoryBackground")
fun FrameLayout.setCategoryBackground(colorString: String){
    val originDrawable = ContextCompat.getDrawable(this.context, R.drawable.category_background_999)
    originDrawable!!.setTint(Color.parseColor(colorString))
    background = originDrawable
}

@BindingAdapter("isExpense", "price", "suffix")
fun TextView.setPriceStyleText(isExpense: Int, price: Long, suffix: String = ""){
    text = if (isExpense==1){
        setTextAppearance(R.style.Widget_TextView_KopubWorldDotumPro14_Red100_Bold_TextAppearance)
        "-${getCommaPriceString(price)}$suffix"
    }else{
        setTextAppearance(R.style.Widget_TextView_KopubWorldDotumPro14_Income100_Bold_TextAppearance)
        "${getCommaPriceString(price)}$suffix"
    }
}


@BindingAdapter("app:changeButtonBackground")
fun AppCompatButton.changeButtonBackground(isButtonEnabled: Boolean){
    if (isButtonEnabled){
        setBackgroundResource(R.drawable.button_background_radius_14)
    }else{
        setBackgroundResource(R.drawable.button_background_opacity50_radius_14)
    }
}