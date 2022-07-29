package com.example.accountbook.utils

import android.graphics.Color
import android.widget.FrameLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.example.accountbook.R
import org.w3c.dom.Text
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
fun TextView.setPriceText(price: Int){
    text = getCommaPriceString(price)
}

@BindingAdapter("app:setDateMdEEType")
fun TextView.setDateMdEEType(date: Date){
    text = dateToStringMdEEType(date)
}

@BindingAdapter("app:setCategoryBackground")
fun FrameLayout.setCategoryBackground(colorString: String){
    val originDrawable = ContextCompat.getDrawable(this.context, R.drawable.history_body_category_999)
    originDrawable!!.setTint(Color.parseColor(colorString))
    background = originDrawable
}

@BindingAdapter("isExpense", "price")
fun TextView.setPriceStyleText(isExpense: Int, price: Int){
    text = if (isExpense==1){
        setTextAppearance(R.style.Widget_TextView_KopubWorldDotumPro14_Red100_Bold_TextAppearance)
        "-${getCommaPriceString(price)}원"
    }else{
        setTextAppearance(R.style.Widget_TextView_KopubWorldDotumPro14_Income100_Bold_TextAppearance)
        "${getCommaPriceString(price)}원"
    }
}