package com.example.chatbotapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatbotapp.api.ApiClient
import com.example.chatbotapp.model.ChatRequest
import com.example.chatbotapp.model.Message
import com.google.gson.Gson
import kotlinx.coroutines.launch

// ViewModel responsável por gerenciar a lógica do chat e manter o estado da UI
class ChatViewModel : ViewModel() {

    // LiveData para armazenar e observar a lista de mensagens
    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> = _messages

    // LiveData para indicar se uma operação está em andamento (ex.: carregando resposta do bot)
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // LiveData para armazenar mensagens de erro e notificar a UI
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    // Lista para manter todas as mensagens (do usuário e do bot)
    private val messagesList = mutableListOf<Message>()

    // Identificador do cliente, usado para associar mensagens a um usuário específico
    private var clientId: String? = null

    // Instância do Gson para serialização e deserialização de JSON
    private val gson = Gson()

    companion object {
        private const val TAG = "ChatViewModel" // Constante para tag de log
    }

    // Função para inicializar o ViewModel com o clientId, se ainda não estiver definido
    fun initialize(clientId: String) {
        if (this.clientId == null) {
            this.clientId = clientId
            Log.d(TAG, "Initialized with clientId: $clientId") // Log da inicialização
        }
    }

    // Função para enviar uma mensagem para o chatbot
    fun sendMessage(content: String) {
        viewModelScope.launch { // Inicia uma corrotina no escopo do ViewModel
            try {
                _isLoading.value = true // Indica que uma operação está em andamento

                // Verifica se o clientId está definido de forma segura
                clientId?.let { safeClientId ->

                    // Adiciona mensagem do usuário à lista de mensagens
                    val userMessage = Message(content = content, isFromBot = false)
                    messagesList.add(userMessage)
                    _messages.value = messagesList.toList() // Atualiza o LiveData com a nova lista de mensagens

                    // Cria o objeto da requisição para enviar ao bot
                    val request = ChatRequest(
                        clientId = safeClientId,
                        message = content
                    )

                    // Log do JSON da requisição que será enviado
                    val jsonRequest = gson.toJson(request)
                    Log.d(TAG, "Sending JSON: $jsonRequest")

                    // Envia a mensagem para a API e recebe a resposta
                    val response = ApiClient.apiService.sendMessage(request)
                    Log.d(TAG, "Received response: ${gson.toJson(response)}")

                    // Adiciona a resposta do bot à lista de mensagens
                    if (response.response.isNotEmpty()) {
                        val botMessage = Message(
                            content = response.response,
                            isFromBot = true
                        )
                        messagesList.add(botMessage)
                        _messages.value = messagesList.toList() // Atualiza o LiveData com a nova lista de mensagens incluindo a resposta do bot
                    }
                }
            } catch (e: Exception) {
                // Loga o erro e atualiza o LiveData de erro
                Log.e(TAG, "Error sending message", e)
                _error.value = "Erro ao enviar mensagem: ${e.message}"
            } finally {
                // Indica que a operação foi concluída
                _isLoading.value = false
            }
        }
    }

    // Função para limpar todas as mensagens do histórico
    fun clearMessages() {
        messagesList.clear() // Limpa a lista de mensagens
        _messages.value = emptyList() // Atualiza o LiveData com uma lista vazia
    }
}
