package com.example.chatbotapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.chatbotapp.databinding.ActivityMainBinding
import ChatFragment
import HistoryFragment
import ProfileFragment

// Classe principal do aplicativo, responsável pela navegação entre os fragmentos principais
class MainActivity : AppCompatActivity() {
    // Binding para acessar as views do layout de forma segura
    private lateinit var binding: ActivityMainBinding

    // Função chamada quando a atividade é criada
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Infla o layout usando View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configura a navegação inferior para trocar de fragmentos
        setupBottomNavigation()

        // Se não houver um estado salvo (primeira vez que a atividade está sendo criada),
        // carrega o fragmento de chat como o fragmento inicial
        if (savedInstanceState == null) {
            loadFragment(ChatFragment())
        }
    }

    // Configura a navegação do BottomNavigationView para trocar entre fragmentos
    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_chat -> loadFragment(ChatFragment()) // Carrega o fragmento de chat
                R.id.navigation_history -> loadFragment(HistoryFragment()) // Carrega o fragmento de histórico
                R.id.navigation_profile -> loadFragment(ProfileFragment()) // Carrega o fragmento de perfil
            }
            true // Retorna true para indicar que a seleção foi tratada
        }
    }

    // Carrega um fragmento específico no container da atividade principal
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment) // Substitui o fragmento atual no container
            .commit() // Confirma a transação e aplica a mudança
    }
}
