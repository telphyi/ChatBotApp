package com.example.chatbotapp.model

import com.google.gson.annotations.SerializedName

// Classe de dados para representar a resposta do histórico de conversas da API
data class HistoryResponse(
    @SerializedName("client_id") val clientId: String, // ID do cliente associado ao histórico, usado para identificar o usuário
    @SerializedName("history") val history: List<HistoryMessage> // Lista de mensagens do histórico, contendo todas as interações (usuário e bot)
)

// Classe de dados para representar cada mensagem do histórico
data class HistoryMessage(
    @SerializedName("user_message") val userMessage: String, // Mensagem enviada pelo usuário
    @SerializedName("bot_message") val botMessage: String, // Resposta do chatbot à mensagem do usuário
    @SerializedName("timestamp") val timestamp: String // Data e hora em que a interação ocorreu
)
