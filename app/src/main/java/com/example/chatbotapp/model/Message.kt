package com.example.chatbotapp.model

// Classe de dados para representar uma mensagem no chat
data class Message(
    val content: String, // Conteúdo da mensagem, que pode ser do usuário ou do bot
    val isFromBot: Boolean = false, // Indica se a mensagem foi enviada pelo bot (true) ou pelo usuário (false)
    val timestamp: Long = System.currentTimeMillis() // Momento em que a mensagem foi criada, armazenado em milissegundos desde 1970 (usado para ordenar as mensagens cronologicamente)
)
