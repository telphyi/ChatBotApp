package com.example.chatbotapp.api

import com.example.chatbotapp.model.ChatRequest
import com.example.chatbotapp.model.ChatResponse
import com.example.chatbotapp.model.HistoryResponse
import retrofit2.http.*

// Interface que define os endpoints da API usando Retrofit
interface ApiService {

    // Método para enviar uma mensagem ao chatbot
    @Headers(
        "X-API-Key: tpsi_mobile_2024_key", // Header de autenticação da API
        "Content-Type: application/json" // Define o tipo de conteúdo como JSON
    )
    @POST("chat") // Endpoint da API para envio de mensagens (requisição POST)
    suspend fun sendMessage(@Body request: ChatRequest): ChatResponse
    // Envia uma requisição POST para o endpoint "chat"
    // @Body request: Corpo da requisição que contém a mensagem (do tipo ChatRequest)
    // Retorna uma resposta do tipo ChatResponse

    // Método para obter o histórico de conversas
    @Headers(
        "X-API-Key: tpsi_mobile_2024_key", // Header de autenticação da API
        "Content-Type: application/json" // Define o tipo de conteúdo como JSON
    )
    @GET("get-history") // Endpoint da API para obtenção do histórico de conversas (requisição GET)
    suspend fun getHistory(@Query("client_id") clientId: String): HistoryResponse
    // Envia uma requisição GET para o endpoint "get-history"
    // @Query("client_id"): Adiciona um parâmetro de consulta "client_id" para filtrar o histórico pelo ID do cliente
    // Retorna uma resposta do tipo HistoryResponse
}
