package com.example.myapplication.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.network.ApiClient
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.io.IOException

sealed interface DashboardUiState {
    data class Success(val works: String) : DashboardUiState
    object Error : DashboardUiState
    object Loading : DashboardUiState
}

class DashboardViewModel : ViewModel() {
    var dashboardUiState: DashboardUiState by mutableStateOf(DashboardUiState.Loading)
        private set

    init {
        getWorks()
    }

    fun getWorks() {
        viewModelScope.launch {
            try {
                val listResult = ApiClient.service.getWorks()
                dashboardUiState = DashboardUiState.Success(
                    "Success: ${listResult.size} works retrieved"
                )
            } catch (e: IOException) {
                dashboardUiState = DashboardUiState.Error
            }
        }
    }
}
