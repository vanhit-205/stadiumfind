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

    /**
     * Cập nhật lại giao diện và luồng điều hướng khi người dùng đăng nhập hoặc đăng xuất thành công.
     */
    fun refreshNavigation() {
        setupNavigation()
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Auto-login: có token thì kiểm tra role
        val isLoggedIn = runBlocking { userPreferences.isLoggedIn.first() }
        val userRole = runBlocking { userPreferences.role.first() }

        val isOwner = "STADIUM_OWNER".equals(userRole, ignoreCase = true)
        if (isOwner) {
            binding.bottomNav.menu.clear()
            binding.bottomNav.inflateMenu(R.menu.bottom_nav_owner_menu)
        } else {
            binding.bottomNav.menu.clear()
            binding.bottomNav.inflateMenu(R.menu.bottom_nav_menu)
        }

        val graph = navController.navInflater.inflate(R.navigation.nav_graph)
        val startDest = when {
            !isLoggedIn -> R.id.loginFragment
            isOwner -> R.id.ownerBookingsFragment
            else -> R.id.homeFragment
        }
        graph.setStartDestination(startDest)
        navController.setGraph(graph, null)

        // Gắn Bottom Navigation với NavController
        binding.bottomNav.setupWithNavController(navController)

        // Ẩn Bottom Nav khi ở màn Auth, Chi tiết sân hoặc Đặt sân (chọn lịch)
        val hideBottomNavDestinations = setOf(
            R.id.loginFragment,
            R.id.registerFragment,
            R.id.stadiumDetailFragment,
            R.id.bookingFragment
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