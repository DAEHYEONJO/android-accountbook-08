package com.example.accountbook.presentation.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.accountbook.R
import com.example.accountbook.data.model.Payments
import com.example.accountbook.databinding.PaymentSpinnerDropDownItemBinding
import com.example.accountbook.databinding.SpinnerViewItemBinding
import com.example.accountbook.utils.dpToPx
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject
import javax.inject.Singleton

@FragmentScoped
class PaymentSpinnerAdapter @Inject constructor(
    @ApplicationContext val context: Context): BaseAdapter() {

    var dp16ToPx = 0
    var paymentList: ArrayList<Payments> = ArrayList()

    companion object{
        const val TAG = "PaymentSpinnerAdapter"
    }

    init {
        dp16ToPx = dpToPx(context, 16).toInt()
    }

    override fun getCount(): Int = paymentList.size

    override fun getItem(position: Int): Any = paymentList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding = SpinnerViewItemBinding.inflate(LayoutInflater.from(context), parent, false)
        Log.e(TAG, "getView: ${paymentList[position].payment}", )
        val textAppearanceResId = if (position != 0&& position!=paymentList.size-1) R.style.Widget_TextView_KopubWorldDotumPro14_Purple100_Bold_TextAppearance
        else R.style.Widget_TextView_KopubWorldDotumPro14_Light_Purple100_Bold_TextAppearance

        with(binding.paymentSpinnerViewTv){
            setTextAppearance(textAppearanceResId)
            if (position!=paymentList.size-1) text = paymentList[position].payment
        }
        return binding.root
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding = PaymentSpinnerDropDownItemBinding.inflate(LayoutInflater.from(context), parent, false)

        with(binding){
            spinnerItemPaymentTv.text = paymentList[position].payment
            if (position == 0){
                root.layoutParams.height = 1
            }else if (position!=paymentList.size -1){
                spinnerItemPaymentIv.visibility = View.INVISIBLE
            }else{
                val layoutParams = (spinnerItemPaymentTv.layoutParams as ConstraintLayout.LayoutParams)
                layoutParams.setMargins(0,dp16ToPx,0,dp16ToPx)
                spinnerItemPaymentTv.layoutParams = layoutParams
            }
        }

        return binding.root
    }
}