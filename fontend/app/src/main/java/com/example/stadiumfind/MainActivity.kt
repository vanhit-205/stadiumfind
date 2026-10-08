package com.example.stadiumfind

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.stadiumfind.core.data.UserPreferencesDataStore
import com.example.stadiumfind.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    @Inject
    lateinit var userPreferences: UserPreferencesDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Edge-to-edge: cho phép content vẽ dưới status bar
        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Auto-login: có token thì vào thẳng Home, ngược lại bắt đầu ở Login
        val isLoggedIn = runBlocking { userPreferences.isLoggedIn.first() }
        val graph = navController.navInflater.inflate(R.navigation.nav_graph)
        graph.setStartDestination(if (isLoggedIn) R.id.homeFragment else R.id.loginFragment)
        navController.setGraph(graph, null)

        // Gắn Bottom Navigation với NavController
        binding.bottomNav.setupWithNavController(navController)

        // Ẩn Bottom Nav khi ở màn Auth (Login / Register)
        val hideBottomNavDestinations = setOf(
            R.id.loginFragment,
            R.id.registerFragment
        )

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id in hideBottomNavDestinations) {
                binding.bottomNav.visibility = View.GONE
            } else {
                binding.bottomNav.visibility = View.VISIBLE
            }
        }
    }
}