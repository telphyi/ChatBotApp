package com.example.chatbotapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatbotapp.api.ApiClient
import com.example.chatbotapp.model.HistoryItem
import com.example.chatbotapp.model.Message
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// ViewModel responsável por carregar e gerenciar o histórico de conversas
class HistoryViewModel : ViewModel() {

    // LiveData para armazenar e observar a lista de itens do histórico
    private val _history = MutableLiveData<List<HistoryItem>>()
    val history: LiveData<List<HistoryItem>> = _history

    // LiveData para indicar quando o histórico está sendo carregado (para exibir um indicador de carregamento na UI)
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // LiveData para armazenar mensagens de erro e notificar a UI
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    // Função para carregar o histórico de conversas do cliente
    fun loadHistory(clientId: String) {
        viewModelScope.launch { // Inicia uma corrotina no escopo do ViewModel para não bloquear a thread principal
            try {
                _isLoading.value = true // Atualiza o estado de carregamento para true

                // Faz a chamada para o endpoint de histórico da API
                val response = ApiClient.apiService.getHistory(clientId)

                // Converte os dados recebidos da API para o formato da UI
                val historyItems = response.history.groupBy { historyMessage ->
                    try {
                        // Formato da data na API: "Sun, 17 Nov 2024"
                        val inputFormat = SimpleDateFormat("EEE, dd MMM yyyy", Locale.ENGLISH)
                        val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        val date = inputFormat.parse(historyMessage.timestamp)
                        outputFormat.format(date) // Converte para um formato de data amigável para exibição
                    } catch (e: Exception) {
                        // Se houver erro ao fazer o parse da data, utiliza a data atual como fallback
                        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                    }
                }.map { (date, messages) -> // Agrupa as mensagens pelo campo "data"
                    HistoryItem(
                        date = date, // Data da conversa
                        preview = messages.firstOrNull()?.userMessage ?: "", // Primeira mensagem do usuário como prévia da conversa
                        messages = messages.flatMap { historyMessage ->
                            // Transforma cada mensagem de usuário e bot em um par de mensagens na lista final
                            listOf(
                                Message(
                                    content = historyMessage.userMessage,
                                    isFromBot = false
                                ),
                                Message(
                                    content = historyMessage.botMessage,
                                    isFromBot = true
                                )
                            )
                        }
                    )
                }.sortedByDescending { it.date } // Ordena o histórico pela data, do mais recente para o mais antigo

                // Atualiza o LiveData com a lista de itens de histórico formatada
                _history.value = historyItems

            } catch (e: Exception) {
                // Caso ocorra um erro, loga a mensagem de erro e atualiza o LiveData de erro para a UI
                Log.e("HistoryViewModel", "Error loading history", e)
                _error.value = "Erro ao carregar histórico: ${e.message}"
            } finally {
                // Atualiza o estado de carregamento para false após o término da operação (seja sucesso ou erro)
                _isLoading.value = false
            }
        }
    }
}
