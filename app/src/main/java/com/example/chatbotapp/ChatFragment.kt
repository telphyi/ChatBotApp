import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatbotapp.R
import com.example.chatbotapp.adapter.MessageAdapter
import com.example.chatbotapp.databinding.FragmentChatBinding
import com.example.chatbotapp.util.ClientIdManager
import com.example.chatbotapp.viewmodel.ChatViewModel

// Fragment responsável pela tela de chat do chatbot
class ChatFragment : Fragment() {

    // Binding para acessar as views no layout de forma segura
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    // Adaptador para exibir a lista de mensagens
    private lateinit var messageAdapter: MessageAdapter

    // ViewModel que gerencia os dados e lógica da interface do chat
    private lateinit var viewModel: ChatViewModel

    // Gerenciador para garantir que cada cliente tenha um ID único
    private lateinit var clientIdManager: ClientIdManager

    // Define que o fragmento possui um menu de opções
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true) // Indica que este fragmento possui um menu de opções
    }

    // Infla o layout do fragmento e inicializa o binding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Configurações iniciais do fragmento após a criação da view
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel() // Configura o ViewModel
        setupRecyclerView() // Configura o RecyclerView para exibir as mensagens
        setupClickListeners() // Configura os listeners dos botões
        observeViewModel() // Observa os dados do ViewModel para atualizar a UI
    }

    // Infla o menu de opções no fragmento
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.chat_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    // Lida com cliques nos itens do menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_clear -> { // Se a opção de limpar conversa for selecionada
                showClearConfirmationDialog() // Mostra o diálogo de confirmação para limpar a conversa
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Configura o ViewModel para interagir com o fragmento
    private fun setupViewModel() {
        viewModel = ViewModelProvider(requireActivity())[ChatViewModel::class.java]
        clientIdManager = ClientIdManager(requireContext())
        viewModel.initialize(clientIdManager.getClientId()) // Inicializa o ViewModel com o clientId gerenciado
    }

    // Configura o RecyclerView para exibir a lista de mensagens do chat
    private fun setupRecyclerView() {
        messageAdapter = MessageAdapter()
        binding.recyclerChat.apply {
            layoutManager = LinearLayoutManager(context) // Define o layout manager para dispor os itens verticalmente
            adapter = messageAdapter // Atribui o adaptador ao RecyclerView
        }
    }

    // Configura os cliques nos botões do fragmento
    private fun setupClickListeners() {
        binding.buttonSend.setOnClickListener {
            val messageText = binding.editMessage.text.toString()
            if (messageText.isNotEmpty()) {
                viewModel.sendMessage(messageText) // Envia a mensagem através do ViewModel
                binding.editMessage.text.clear() // Limpa o campo de entrada de texto
            }
        }
    }

    // Observa os dados do ViewModel para atualizar a interface do usuário
    private fun observeViewModel() {
        viewModel.messages.observe(viewLifecycleOwner) { messages ->
            messageAdapter.submitList(messages) // Atualiza a lista de mensagens no adaptador
            binding.recyclerChat.scrollToPosition(messages.size - 1) // Rola até a última mensagem
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.buttonSend.isEnabled = !isLoading // Desabilita o botão de enviar quando está carregando
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE // Mostra ou esconde a barra de progresso
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show() // Mostra uma mensagem de erro
            }
        }
    }

    // Mostra um diálogo de confirmação para limpar a conversa
    private fun showClearConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Limpar Conversa")
            .setMessage("Tem certeza que deseja limpar toda a conversa?")
            .setPositiveButton("Sim") { _, _ ->
                viewModel.clearMessages() // Limpa as mensagens se o usuário confirmar
            }
            .setNegativeButton("Não", null) // Não faz nada se o usuário cancelar
            .show()
    }

    // Limpa o binding quando a view do fragmento é destruída
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
