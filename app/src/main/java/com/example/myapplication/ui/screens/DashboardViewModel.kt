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
import retrofit2.HttpException
import java.io.IOException

sealed interface DashboardUiState {
    object Loading : DashboardUiState
    data class Success(val works: List<Work>) : DashboardUiState
    object Error : DashboardUiState
}

class DashboardViewModel : ViewModel() {
    private val _dashboardUiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val dashboardUiState: StateFlow<DashboardUiState> = _dashboardUiState.asStateFlow()

    init {
        fetchWorks()
    }

    private fun fetchWorks() {
        viewModelScope.launch {
            _dashboardUiState.value = DashboardUiState.Loading
            try {
                // ★★★ 修正: completed=false を付けて未完了の作品のみ取得 ★★★
                val result = ApiClient.service.getAllWorks(completed = false)

                if (result.isSuccessful) {
                    _dashboardUiState.value = DashboardUiState.Success(result.body() ?: emptyList())
                } else {
                    throw HttpException(result)
                }
            } catch (e: Exception) {
                Log.e("DashboardViewModel", "Error fetching works", e)
                _dashboardUiState.value = DashboardUiState.Error
            }
        }
    }
}

