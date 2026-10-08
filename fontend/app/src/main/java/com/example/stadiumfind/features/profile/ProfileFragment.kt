package com.example.stadiumfind.features.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.stadiumfind.R
import com.example.stadiumfind.core.data.UserPreferencesDataStore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    @Inject
    lateinit var userPreferences: UserPreferencesDataStore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_profile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Logout tạm: xóa token rồi quay về Login (xóa back stack)
        view.findViewById<Button>(R.id.btnLogout).setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                userPreferences.clearAll()
                findNavController().navigate(R.id.action_global_loginFragment)
            }
        }
    }
}
