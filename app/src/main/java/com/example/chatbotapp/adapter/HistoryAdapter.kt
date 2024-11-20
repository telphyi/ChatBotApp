package com.example.chatbotapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.chatbotapp.databinding.ItemHistoryBinding
import com.example.chatbotapp.model.HistoryItem
import com.example.chatbotapp.model.Message

class HistoryAdapter : ListAdapter<HistoryItem, HistoryAdapter.ViewHolder>(HistoryDiffCallback()) {

    var onItemClick: ((List<Message>) -> Unit)? = null
    private var originalList = listOf<HistoryItem>()

    inner class ViewHolder(private val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: HistoryItem) {
            binding.textDate.text = item.date

            val userMessage = item.messages.find { !it.isFromBot }
            val botMessage = item.messages.find { it.isFromBot }

            binding.textUserMessage.text = "Você: ${userMessage?.content ?: ""}"
            binding.textBotMessage.text = "Bot: ${botMessage?.content ?: ""}"

            itemView.setOnClickListener {
                onItemClick?.invoke(item.messages)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun setData(items: List<HistoryItem>) {
        originalList = items
        submitList(items)
    }

    fun filter(query: String) {
        val filteredList = if (query.isEmpty()) {
            originalList
        } else {
            originalList.filter { item ->
                item.messages.any { message ->
                    message.content.contains(query, ignoreCase = true)
                }
            }
        }
        submitList(filteredList)
    }

    private class HistoryDiffCallback : DiffUtil.ItemCallback<HistoryItem>() {
        override fun areItemsTheSame(oldItem: HistoryItem, newItem: HistoryItem): Boolean {
            return oldItem.date == newItem.date
        }

        override fun areContentsTheSame(oldItem: HistoryItem, newItem: HistoryItem): Boolean {
            return oldItem == newItem
        }
    }
}