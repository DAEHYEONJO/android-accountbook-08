package com.example.accountbook.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.accountbook.databinding.StatisticsItemBinding
import com.example.accountbook.domain.model.StatisticsItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StatisticsAdapter @Inject constructor(): RecyclerView.Adapter<StatisticsAdapter.ViewHolder>(){

    var statisticsItemList = emptyList<StatisticsItem>()

    class ViewHolder(val binding: StatisticsItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(statisticsItem: StatisticsItem){
            binding.statisticsItem = statisticsItem
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = StatisticsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(statisticsItemList[position])
    }

    override fun getItemCount(): Int = statisticsItemList.size
}