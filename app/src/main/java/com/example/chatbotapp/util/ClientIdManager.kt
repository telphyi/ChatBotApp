package com.example.chatbotapp.util // ou .manager

import android.content.Context
import java.util.UUID

// Classe responsável por gerenciar o ID do cliente, garantindo que cada dispositivo tenha um identificador único
class ClientIdManager(context: Context) {

    // Objeto SharedPreferences para armazenar o ID do cliente de forma persistente no dispositivo
    private val sharedPreferences = context.getSharedPreferences("ChatBotPrefs", Context.MODE_PRIVATE)
    private val CLIENT_ID_KEY = "client_id" // Chave utilizada para armazenar e recuperar o ID do cliente

    // Função para obter o ID do cliente
    fun getClientId(): String {
        // Tenta obter o ID do cliente do SharedPreferences
        var clientId = sharedPreferences.getString(CLIENT_ID_KEY, null)

        // Se o ID do cliente ainda não existir, gera um novo ID único
        if (clientId == null) {
            clientId = UUID.randomUUID().toString() // Gera um novo UUID para ser o ID do cliente
            sharedPreferences.edit().putString(CLIENT_ID_KEY, clientId).apply() // Armazena o novo ID no SharedPreferences de forma persistente
        }

        // Retorna o ID do cliente, seja o recuperado ou o recém-criado
        return clientId
    }
}
