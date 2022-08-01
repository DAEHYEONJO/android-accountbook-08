package com.example.accountbook.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.accountbook.R
import com.example.accountbook.data.model.Categories
import com.example.accountbook.data.model.Payments
import com.example.accountbook.databinding.CategorySpinnerDropDownItemBinding
import com.example.accountbook.databinding.PaymentSpinnerDropDownItemBinding
import com.example.accountbook.databinding.SpinnerViewItemBinding
import com.example.accountbook.utils.dpToPx
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject
import javax.inject.Singleton

@FragmentScoped
class CategorySpinnerAdapter @Inject constructor(
    @ApplicationContext val context: Context): BaseAdapter() {

    var dp16ToPx = 0
    var categoryList: ArrayList<Categories> = ArrayList()

    companion object{
        const val TAG = "CategorySpinnerAdapter"
    }

    init {
        dp16ToPx = dpToPx(context, 16).toInt()
    }

    override fun getCount(): Int = categoryList.size

    override fun getItem(position: Int): Any = categoryList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding = SpinnerViewItemBinding.inflate(LayoutInflater.from(context), parent, false)
        Log.e(TAG, "getView: ${categoryList}", )
        Log.e(TAG, "getView: ${position}", )
        Log.e(TAG, "getView: ${categoryList[position].category}", )
        val textAppearanceResId = if (position != 0) R.style.Widget_TextView_KopubWorldDotumPro14_Purple100_Bold_TextAppearance
        else R.style.Widget_TextView_KopubWorldDotumPro14_Light_Purple100_Bold_TextAppearance

        with(binding.paymentSpinnerViewTv){
            setTextAppearance(textAppearanceResId)
            text = categoryList[position].category
        }
        return binding.root
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding = CategorySpinnerDropDownItemBinding.inflate(LayoutInflater.from(context), parent, false)
        with(binding){
            categories = categoryList[position]
            Log.e(TAG, "getDropDownView: ${categoryList[position].labelColor}", )
            if (position == 0){
                root.layoutParams.height = 1
            }else if (position!=categoryList.size - 1){
                categorySpinnerIv.visibility = View.INVISIBLE
            }else{
                categorySpinnerTv.setTextAppearance(R.style.Widget_TextView_KopubWorldDotumPro12_Light_Purple100_Bold_TextAppearance)
                val layoutParams = (categorySpinnerLayout.layoutParams as ConstraintLayout.LayoutParams)
                layoutParams.setMargins(0,dp16ToPx,0,dp16ToPx)
                categorySpinnerLayout.layoutParams =layoutParams
            }
        }

        return binding.root
    }
}