package com.example.chatbotapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.chatbotapp.databinding.ItemHistoryBinding
import com.example.chatbotapp.model.HistoryItem
import com.example.chatbotapp.model.Message

// Adapter para exibir uma lista de itens de histórico em um RecyclerView
class HistoryAdapter : ListAdapter<HistoryItem, HistoryAdapter.ViewHolder>(HistoryDiffCallback()) {

    // Callback que será acionado quando um item for clicado, passando a lista de mensagens
    var onItemClick: ((List<Message>) -> Unit)? = null

    // Lista original dos itens do histórico, usada para realizar filtragem
    private var originalList = listOf<HistoryItem>()

    // ViewHolder que gerencia o conteúdo de cada item do RecyclerView
    inner class ViewHolder(private val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // Função para associar os dados do HistoryItem ao layout do item
        fun bind(item: HistoryItem) {
            binding.textDate.text = item.date // Definindo a data do item
            binding.textPreview.text = item.preview // Definindo a prévia do conteúdo do item

            // Define um listener de clique para o itemView que chama a função onItemClick com as mensagens do item
            itemView.setOnClickListener {
                onItemClick?.invoke(item.messages)
            }
        }
    }

    // Cria um novo ViewHolder quando necessário
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    // Associa os dados ao ViewHolder na posição especificada
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    // Define os dados da lista de histórico e envia para o ListAdapter
    fun setData(items: List<HistoryItem>) {
        originalList = items // Armazena a lista original para uso em filtragens
        submitList(items) // Envia a nova lista de dados ao ListAdapter
    }

    // Filtra os itens da lista de acordo com a consulta fornecida
    fun filter(query: String) {
        val filteredList = if (query.isEmpty()) {
            // Se a consulta estiver vazia, retorna a lista original
            originalList
        } else {
            // Caso contrário, filtra os itens que contenham a consulta no preview ou na data
            originalList.filter { item ->
                item.preview.contains(query, ignoreCase = true) ||
                        item.date.contains(query, ignoreCase = true)
            }
        }
        submitList(filteredList) // Envia a lista filtrada ao ListAdapter
    }

    // Classe que define como o DiffUtil deve comparar os itens da lista
    private class HistoryDiffCallback : DiffUtil.ItemCallback<HistoryItem>() {
        // Verifica se dois itens são os mesmos comparando suas datas
        override fun areItemsTheSame(oldItem: HistoryItem, newItem: HistoryItem): Boolean {
            return oldItem.date == newItem.date
        }

        // Verifica se o conteúdo de dois itens é o mesmo
        override fun areContentsTheSame(oldItem: HistoryItem, newItem: HistoryItem): Boolean {
            return oldItem == newItem
        }
    }
}
