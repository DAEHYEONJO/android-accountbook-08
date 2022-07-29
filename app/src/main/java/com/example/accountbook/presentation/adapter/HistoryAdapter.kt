package com.example.accountbook.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.accountbook.R
import com.example.accountbook.databinding.HistoryBodyItemBinding
import com.example.accountbook.databinding.HistoryHeaderItemBinding
import com.example.accountbook.domain.model.HEADER
import com.example.accountbook.domain.model.HistoriesListItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryAdapter @Inject constructor()
    : ListAdapter<HistoriesListItem, RecyclerView.ViewHolder>(diffUtil) {

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<HistoriesListItem>() {
            override fun areItemsTheSame(
                oldItem: HistoriesListItem,
                newItem: HistoriesListItem
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: HistoriesListItem,
                newItem: HistoriesListItem
            ): Boolean {
                return oldItem.id == newItem.id &&
                        oldItem.date == newItem.date
            }
        }
    }

    class HeaderViewHolder(private val binding: HistoryHeaderItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(historyListItem: HistoriesListItem) {
            binding.historyListItem = historyListItem
            binding.executePendingBindings()
        }
    }

    class BodyViewHolder(private val binding: HistoryBodyItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(historyListItem: HistoriesListItem) {
            binding.historyListItem = historyListItem
            binding.executePendingBindings()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == HEADER) {
            HeaderViewHolder(
                HistoryHeaderItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            BodyViewHolder(
                HistoryBodyItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == HEADER) {
            (holder as HeaderViewHolder).bind(currentList[position])
        } else {
            (holder as BodyViewHolder).bind(currentList[position])
        }
    }

    override fun getItemViewType(position: Int): Int {
        return currentList[position].viewType
    }

}


