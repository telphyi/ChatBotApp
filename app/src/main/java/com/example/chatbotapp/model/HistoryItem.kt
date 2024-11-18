package com.example.chatbotapp.model

// Classe de dados para representar um item do histórico de conversas
data class HistoryItem(
    val date: String, // Data da conversa (em formato de string), usada para mostrar quando a conversa ocorreu
    val preview: String, // Prévia do conteúdo da conversa, geralmente as primeiras palavras para identificação rápida
    val messages: List<Message> // Lista de mensagens que compõem a conversa completa
)
