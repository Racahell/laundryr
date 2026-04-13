package com.example.laundryr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.laundryr.data.local.SessionManager
import com.example.laundryr.data.remote.ApiClient
import com.example.laundryr.data.repository.LaundryRepository
import com.example.laundryr.ui.LaundryApp
import com.example.laundryr.ui.MainViewModel
import com.example.laundryr.ui.MainViewModelFactory
import com.example.laundryr.ui.theme.LaundryrTheme

class MainActivity : ComponentActivity() {
    private val sessionManager by lazy { SessionManager(this) }
    private val repository by lazy {
        LaundryRepository(ApiClient.create { sessionManager.getToken() })
    }

    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(sessionManager, repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LaundryrTheme {
                LaundryApp(viewModel)
            }
        }
    }
}
