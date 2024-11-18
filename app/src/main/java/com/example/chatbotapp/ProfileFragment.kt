import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.chatbotapp.databinding.FragmentProfileBinding
import com.example.chatbotapp.util.ClientIdManager
import com.example.chatbotapp.viewmodel.ChatViewModel

// Fragmento responsável pelo perfil do usuário, onde é possível visualizar informações e configurar preferências
class ProfileFragment : Fragment() {

    // Binding para acessar as views do layout de forma segura
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    // Gerenciador de ID do cliente para identificar cada usuário
    private lateinit var clientIdManager: ClientIdManager

    // Objeto SharedPreferences para salvar e recuperar preferências do usuário
    private lateinit var sharedPreferences: SharedPreferences

    // ViewModel para acessar as estatísticas das mensagens do chat
    private lateinit var chatViewModel: ChatViewModel

    // Infla o layout do fragmento e inicializa o binding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Configurações iniciais do fragmento após a criação da view
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews() // Configura as views iniciais, como switches e ID do cliente
        loadStatistics() // Carrega estatísticas de mensagens do chat
        setupListeners() // Configura os listeners para switches e botões
    }

    // Configura as views e inicializa objetos como ClientIdManager e SharedPreferences
    private fun setupViews() {
        clientIdManager = ClientIdManager(requireContext())
        chatViewModel = ViewModelProvider(requireActivity())[ChatViewModel::class.java]

        sharedPreferences = requireContext().getSharedPreferences(
            "app_settings",
            Context.MODE_PRIVATE
        )

        // Exibe o ID único do cliente na tela de perfil
        binding.textClientId.text = clientIdManager.getClientId()

        // Inicializa os switches com os valores salvos nas preferências do usuário
        binding.switchNotifications.isChecked =
            sharedPreferences.getBoolean("notifications_enabled", true)
        binding.switchDarkMode.isChecked =
            sharedPreferences.getBoolean("light_mode_enabled", false)
    }

    // Carrega estatísticas das mensagens do chat e as exibe no perfil
    private fun loadStatistics() {
        chatViewModel.messages.observe(viewLifecycleOwner) { messages ->
            val totalMessages = messages.size
            val userMessages = messages.count { !it.isFromBot }
            val botMessages = messages.count { it.isFromBot }

            // Atualiza as views para exibir o total de mensagens e detalhes
            binding.textTotalMessages.text = "Total de mensagens: $totalMessages"
            binding.textTotalConversations.text = "Mensagens do usuário: $userMessages"
            binding.textAverageMessages.text = "Respostas do bot: $botMessages"
        }
    }

    // Configura os listeners para switches e o botão de limpar dados
    private fun setupListeners() {
        // Listener para o switch de notificações, salva a preferência no SharedPreferences
        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit()
                .putBoolean("notifications_enabled", isChecked)
                .apply()

            val message = if (isChecked) "Notificações ativadas" else "Notificações desativadas"
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }

        // Listener para o switch de modo claro/escuro, salva a preferência no SharedPreferences
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit()
                .putBoolean("light_mode_enabled", isChecked)
                .apply()

            val message = if (isChecked) "Light mode ativado" else "Light mode desativado"
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }

        // Listener para o botão de limpar todos os dados, mostra um diálogo de confirmação
        binding.buttonClearAll.setOnClickListener {
            showClearConfirmationDialog()
        }
    }

    // Mostra um diálogo de confirmação para limpar todos os dados do aplicativo
    private fun showClearConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Limpar Dados")
            .setMessage("Tem certeza que deseja limpar todos os dados? Esta ação não pode ser desfeita.")
            .setPositiveButton("Sim") { _, _ ->
                clearAllData() // Chama a função para limpar todos os dados se confirmado
            }
            .setNegativeButton("Não", null)
            .show()
    }

    // Limpa todas as mensagens do chat e reseta as preferências do usuário
    private fun clearAllData() {
        chatViewModel.clearMessages() // Limpa as mensagens do chat

        // Limpa todas as preferências salvas do usuário
        sharedPreferences.edit().clear().apply()

        // Atualiza os switches para os valores padrão
        binding.switchNotifications.isChecked = true
        binding.switchDarkMode.isChecked = false

        Toast.makeText(context, "Todos os dados foram limpos", Toast.LENGTH_SHORT).show()
    }

    // Limpa o binding quando a view do fragmento é destruída para evitar vazamentos de memória
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
