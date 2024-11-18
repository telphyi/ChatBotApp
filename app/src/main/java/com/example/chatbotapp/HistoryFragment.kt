import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatbotapp.adapter.HistoryAdapter
import com.example.chatbotapp.databinding.FragmentHistoryBinding
import com.example.chatbotapp.util.ClientIdManager
import com.example.chatbotapp.viewmodel.HistoryViewModel

// Fragmento responsável por exibir o histórico de conversas do chatbot
class HistoryFragment : Fragment() {

    // Binding para acessar as views do layout de forma segura
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    // Adaptador para exibir a lista de itens do histórico
    private lateinit var historyAdapter: HistoryAdapter

    // ViewModel que gerencia a lógica e os dados do histórico
    private lateinit var viewModel: HistoryViewModel

    // Gerenciador para obter o ID único do cliente
    private lateinit var clientIdManager: ClientIdManager

    // Infla o layout do fragmento e inicializa o binding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Configurações iniciais do fragmento após a criação da view
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel() // Configura o ViewModel
        setupRecyclerView() // Configura o RecyclerView para exibir os itens do histórico
        setupSearch() // Configura o campo de busca para filtrar os itens do histórico
        loadHistory() // Carrega o histórico de conversas
    }

    // Configura o ViewModel e os observadores para atualizar a UI
    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[HistoryViewModel::class.java]
        clientIdManager = ClientIdManager(requireContext())

        // Observa as mudanças no histórico e atualiza o adaptador
        viewModel.history.observe(viewLifecycleOwner) { historyItems ->
            historyAdapter.setData(historyItems)
        }

        // Observa o estado de carregamento e atualiza a visibilidade da barra de progresso
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observa as mensagens de erro e mostra um Toast se houver um erro
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            }
        }
    }

    // Configura o RecyclerView para exibir a lista de itens do histórico de conversas
    private fun setupRecyclerView() {
        historyAdapter = HistoryAdapter()
        binding.recyclerHistory.apply {
            layoutManager = LinearLayoutManager(context) // Define o layout manager para listar os itens verticalmente
            adapter = historyAdapter // Atribui o adaptador ao RecyclerView
        }

        // Define a ação quando um item do histórico é clicado (TODO para implementar navegação)
        historyAdapter.onItemClick = { messages ->
            // TODO: Implementar navegação para o chat com essas mensagens
        }
    }

    // Configura o campo de busca para filtrar os itens do histórico
    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { historyAdapter.filter(it) } // Filtra os itens quando o usuário envia a consulta
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { historyAdapter.filter(it) } // Filtra os itens enquanto o usuário digita a consulta
                return true
            }
        })
    }

    // Carrega o histórico de conversas do cliente
    private fun loadHistory() {
        val clientId = clientIdManager.getClientId() // Obtém o clientId
        viewModel.loadHistory(clientId) // Carrega o histórico com o ViewModel
    }

    // Recarrega o histórico de conversas quando o fragmento volta a ficar visível
    override fun onResume() {
        super.onResume()
        loadHistory() // Carrega novamente o histórico para garantir que esteja atualizado
    }

    // Limpa o binding quando a view do fragmento é destruída para evitar vazamentos de memória
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
