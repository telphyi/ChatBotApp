package com.example.chatbotapp.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// Objeto singleton responsável por configurar o cliente API usando Retrofit
object ApiClient {

    // URL base da API para todas as requisições (substitua pela sua própria URL)
    private const val BASE_URL = "http://pmonteiro.ovh:5000/"  // Esta é a URL base correta

    // Configuração do interceptor de logging para monitorar as requisições e respostas HTTP
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // Define o nível do log para exibir o corpo das requisições e respostas
    }

    // Criação do OkHttpClient com configurações adicionais como interceptores e tempos de timeout
    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            // Interceptor para adicionar headers personalizados em cada requisição
            val original = chain.request()
            val request = original.newBuilder()
                .header("X-API-Key", "tpsi_mobile_2024_key") // Header personalizado para autenticação da API
                .header("Content-Type", "application/json") // Especifica o tipo de conteúdo sendo enviado
                .method(original.method, original.body) // Mantém o método e o corpo da requisição original
                .build()
            chain.proceed(request) // Prossegue com a requisição modificada
        }
        .addInterceptor(logging) // Adiciona o interceptor de logging para depuração
        .connectTimeout(30, TimeUnit.SECONDS) // Tempo máximo de conexão de 30 segundos
        .readTimeout(30, TimeUnit.SECONDS) // Tempo máximo para leitura de 30 segundos
        .writeTimeout(30, TimeUnit.SECONDS) // Tempo máximo para escrita de 30 segundos
        .build()

    // Criação do objeto Retrofit usando o cliente HTTP configurado e o conversor Gson
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)  // Define a URL base para todas as requisições
        .client(client) // Define o cliente OkHttp customizado com interceptores e timeouts
        .addConverterFactory(GsonConverterFactory.create()) // Adiciona o conversor Gson para transformar JSON em objetos Kotlin e vice-versa
        .build()

    // Criação do serviço API que será usado para realizar as chamadas à API
    val apiService: ApiService = retrofit.create(ApiService::class.java)
}
