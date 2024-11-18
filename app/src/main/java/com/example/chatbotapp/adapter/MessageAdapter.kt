package com.example.chatbotapp.adapter

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.chatbotapp.R
import com.example.chatbotapp.databinding.ItemMessageBinding
import com.example.chatbotapp.model.Message

// Adapter para exibir uma lista de mensagens em um RecyclerView
class MessageAdapter : ListAdapter<Message, MessageAdapter.MessageViewHolder>(MessageDiffCallback()) {

    // Cria um novo ViewHolder para exibir cada mensagem na lista
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        // Inflando o layout do item da mensagem e criando o ViewHolder correspondente
        val binding = ItemMessageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MessageViewHolder(binding)
    }

    // Associa os dados de uma mensagem ao ViewHolder na posição especificada
    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    // Classe ViewHolder que gerencia o conteúdo de cada mensagem
    class MessageViewHolder(private val binding: ItemMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // Associa os dados de um objeto Message ao layout do item da mensagem
        fun bind(message: Message) {
            binding.textMessage.text = message.content // Define o texto da mensagem

            // Configura os parâmetros de layout para definir a posição da mensagem (esquerda/direita)
            val params = binding.textMessage.layoutParams as LinearLayout.LayoutParams

            if (message.isFromBot) {
                // Se a mensagem for do bot, alinha-a à esquerda e aplica o estilo de fundo do bot
                params.gravity = Gravity.START
                binding.textMessage.setBackgroundResource(R.drawable.bg_message_bot)
            } else {
                // Se a mensagem for do usuário, alinha-a à direita e aplica o estilo de fundo do usuário
                params.gravity = Gravity.END
                binding.textMessage.setBackgroundResource(R.drawable.bg_message_user)
            }

            // Atualiza os parâmetros de layout da mensagem
            binding.textMessage.layoutParams = params
        }
    }

    // Callback do DiffUtil para calcular diferenças na lista e otimizar a atualização do RecyclerView
    private class MessageDiffCallback : DiffUtil.ItemCallback<Message>() {

        // Verifica se dois itens representam a mesma mensagem (compara pelo timestamp)
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.timestamp == newItem.timestamp
        }

        // Verifica se o conteúdo de dois itens é o mesmo
        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem == newItem
        }
    }
}
