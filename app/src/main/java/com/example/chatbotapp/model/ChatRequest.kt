package com.example.chatbotapp.model

import com.google.gson.annotations.SerializedName

// Classe de dados para representar uma requisição de chat
data class ChatRequest(
    @SerializedName("client_id") val clientId: String, // O ID do cliente, usado para identificar o autor da mensagem
    @SerializedName("message") val message: String // O conteúdo da mensagem enviada ao chatbot
)
