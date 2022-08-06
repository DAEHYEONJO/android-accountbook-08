package com.example.accountbook.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.accountbook.databinding.SettingColorItemBinding
import com.example.accountbook.presentation.model.ColorItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingColorAdapter @Inject constructor(): RecyclerView.Adapter<SettingColorAdapter.ViewHolder>() {

    var colorList:List<ColorItem> = emptyList()
    interface OnItemClickListener{
        fun onClick(position: Int, item: ColorItem)
    }
    var listener: OnItemClickListener? = null
    var preSelectedPos = 0

    inner class ViewHolder(val binding: SettingColorItemBinding): RecyclerView.ViewHolder(binding.root){
        init {
            binding.root.setOnClickListener {
                colorList[preSelectedPos].isTouched = false
                notifyItemChanged(preSelectedPos)
                preSelectedPos = adapterPosition
                listener?.onClick(adapterPosition, colorList[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = SettingColorItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = colorList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (colorList[position].isTouched){
            holder.binding.setting.animate().scaleX(1.7f).scaleY(1.7f).start()
        }else{
            holder.binding.setting.animate().scaleX(1f).scaleY(1f).start()
        }
        holder.binding.setting.setBackgroundColor(colorList[position].color)
    }
}