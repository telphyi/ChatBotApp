package com.example.chatbotapp.model

import com.google.gson.annotations.SerializedName

// Classe de dados para representar a resposta do chatbot
data class ChatResponse(
    @SerializedName("response") val response: String, // O conteúdo da resposta do chatbot
    @SerializedName("client_id") val clientId: String // O ID do cliente que originou a mensagem (para garantir a correspondência)
)
