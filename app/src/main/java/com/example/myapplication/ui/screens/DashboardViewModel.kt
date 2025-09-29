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
    data class Success(val works: List<Work>?) : DashboardUiState
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
                if (result.body() == null) {
                    val dummyWorks = listOf(
                        Work(
                            id = 1,
                            title = "シンプルなマフラー",
                            description = "最近の作業: 5段目を編み終えました",
                            work_url = "",
                            raw_index = 5,
                            stitch_index = 0,
                            is_completed = false,
                            completed_at = Instant.now().toString(),
                            created_at = Instant.now().toString(),
                            updated_at = Instant.now().toString()
                        ),
                        Work(
                            id = 2,
                            title = "ハートのコースター",
                            description = "完了！ - 2024/02/01",
                            work_url = "",
                            raw_index = 0,
                            stitch_index = 0,
                            is_completed = true,
                            completed_at = Instant.now().toString(),
                            created_at = Instant.now().toString(),
                            updated_at = Instant.now().toString()
                        )
                    )
                    _dashboardUiState.value = DashboardUiState.Success(dummyWorks)
                } else {
                    _dashboardUiState.value = DashboardUiState.Success(result.body())
                }
            } catch (e: IOException) {
                _dashboardUiState.value = DashboardUiState.Error
                Log.e("DashboardViewModel", "Network error", e)
            } catch (e: HttpException) {
                if (e.code() == 401) {
                    _dashboardUiState.value = DashboardUiState.Error
                    Log.e("DashboardViewModel", "Unauthorized: Authentication required", e)
                    _dashboardUiState.value = DashboardUiState.Error
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

