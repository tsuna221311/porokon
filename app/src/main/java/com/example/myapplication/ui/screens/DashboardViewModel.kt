package com.example.myapplication.ui.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.Work
import com.example.myapplication.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import retrofit2.HttpException
import java.io.IOException
import java.time.Instant

// --- UIの状態をここで一元管理 ---
sealed interface DashboardUiState {
    object Loading : DashboardUiState
    data class Success(val works: List<Work>) : DashboardUiState
    object Error : DashboardUiState
}

class DashboardViewModel : ViewModel() {

    private val _dashboardUiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val dashboardUiState: StateFlow<DashboardUiState> = _dashboardUiState.asStateFlow()

    init {
        // ViewModelが作成されたときに、データを読み込む
        fetchWorks()
    }

    private fun fetchWorks() {
        viewModelScope.launch {
            _dashboardUiState.value = DashboardUiState.Loading
            try {
                val result = ApiClient.service.getAllWorks()
                _dashboardUiState.value = DashboardUiState.Success(result.body() ?: emptyList())
            } catch (e: IOException) {
                _dashboardUiState.value = DashboardUiState.Error
                Log.e("DashboardViewModel", "Network error", e)
            } catch (e: HttpException) {
                if (e.code() == 401) {
                    _dashboardUiState.value = DashboardUiState.Error
                    Log.e("DashboardViewModel", "Unauthorized: Authentication required", e)
                } else {
                    _dashboardUiState.value = DashboardUiState.Error
                    Log.e("DashboardViewModel", "HTTP error ${e.code()}", e)
                }
            } catch (e: Exception) {
                _dashboardUiState.value = DashboardUiState.Error
            }
        }
    }
}

