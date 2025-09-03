package com.example.myapplication.ui.screens

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.Work
import com.example.myapplication.network.ApiClient
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed interface DashboardUiState {
    data class Success(val works: List<Work>?) : DashboardUiState
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
                val result = ApiClient.service.getAllWorks()
                dashboardUiState = DashboardUiState.Success(result.body())
            } catch (e: IOException) {
                dashboardUiState = DashboardUiState.Error
                Log.e("DashboardViewModel", "Network error", e)
            } catch (e: HttpException) {
                if (e.code() == 401) {
                    dashboardUiState = DashboardUiState.Error
                    Log.e("DashboardViewModel", "Unauthorized: Authentication required", e)
                    dashboardUiState = DashboardUiState.Error
                } else {
                    dashboardUiState = DashboardUiState.Error
                    Log.e("DashboardViewModel", "HTTP error ${e.code()}", e)
                }
            }
        }
    }
}
