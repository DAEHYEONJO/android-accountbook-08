package com.example.accountbook.presentation.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.accountbook.R
import com.example.accountbook.databinding.CalendarItemBinding
import com.example.accountbook.domain.model.CalendarItem
import com.example.accountbook.utils.getCommaPriceString
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CalendarAdapter @Inject constructor() :
    RecyclerView.Adapter<CalendarAdapter.ItemViewHolder>() {

    companion object {
        const val TAG = "CalendarAdapter"
    }

    var dayList: List<CalendarItem> = emptyList()
    var deviceWidth: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding =
            CalendarItemBinding.inflate(LayoutInflater.from(parent.context), parent, false).apply {
                this.root.layoutParams.height =
                    (((deviceWidth - 6).toDouble() / 7) * 1.16).toInt() // (device 가로길이 - 1dp*6개 ) / 7, 1.16은 가로:세로 디자인 비율
            }
        return ItemViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(dayList[position])

    }

    override fun getItemCount(): Int = dayList.size

    class ItemViewHolder(val binding: CalendarItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(calendarItem: CalendarItem) {
            with(binding) {
                dayText.text = calendarItem.day.toString()
                rightView.visibility = if (calendarItem.isSaturday) View.GONE else View.VISIBLE
                dayText.setTextAppearance(
                    if (calendarItem.isThisMonth) R.style.Widget_TextView_KopubWorldDotumPro8_Purple100_Bold_TextAppearance
                    else R.style.Widget_TextView_KopubWorldDotumPro8_Light_Purple100_Bold_TextAppearance
                )
                root.setBackgroundResource(
                    if (calendarItem.isToday) R.color.primary_white_100
                    else R.color.primary_off_white_100
                )
                with(calendarItemIncomeTv){
                    visibility = calendarItem.incomePrice.let {
                        text = getCommaPriceString(it)
                        if (it!=0L) View.VISIBLE
                        else View.GONE
                    }
                }
                with(calendarItemExpenseTv){
                    visibility = calendarItem.expensePrice.let {
                        text = "-${getCommaPriceString(it)}"
                        if (it!=0L) View.VISIBLE
                        else View.GONE
                    }
                }
                calendarItemTotalTv.text = calendarItem.totalPrice.let {
                    if (it!=0L) getCommaPriceString(it)
                    else ""
                }
            }
        }
    }
}