

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatbotapp.adapter.HistoryAdapter
import com.example.chatbotapp.databinding.FragmentHistoryBinding
import com.example.chatbotapp.model.HistoryItem
import com.example.chatbotapp.viewmodel.ChatViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryFragment : Fragment() {
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var viewModel: ChatViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        setupRecyclerView()
        setupSearch()
        loadHistory()
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(requireActivity())[ChatViewModel::class.java]
    }

    private fun setupRecyclerView() {
        historyAdapter = HistoryAdapter()
        binding.recyclerHistory.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = historyAdapter
        }
    }

    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { historyAdapter.filter(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { historyAdapter.filter(it) }
                return true
            }
        })
    }

    private fun loadHistory() {
        viewModel.messages.observe(viewLifecycleOwner) { messages ->
            val historyItems = messages.chunked(2).mapIndexed { index, messagePair ->
                val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                val date = dateFormat.format(Date())

                HistoryItem(
                    date = "Conversa ${index + 1} - $date",
                    preview = messagePair.firstOrNull()?.content ?: "",
                    messages = messagePair
                )
            }
            historyAdapter.setData(historyItems.reversed()) // Mostra as conversas mais recentes primeiro
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}