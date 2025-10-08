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
                // APIを呼び出して作品一覧を取得
                val result = ApiClient.service.getAllWorks()

                if (result.isSuccessful) {
                    _dashboardUiState.value = DashboardUiState.Success(result.body() ?: emptyList())
                } else {
                    // isSuccessfulでなかった場合もHttpExceptionとして処理
                    throw HttpException(result)
                }

            } catch (e: IOException) {
                // ネットワーク接続エラーなど
                _dashboardUiState.value = DashboardUiState.Error
                Log.e("DashboardViewModel", "Network error", e)
            } catch (e: HttpException) {
                // サーバーからのHTTPエラー (404, 500など)
                _dashboardUiState.value = DashboardUiState.Error
                Log.e("DashboardViewModel", "HTTP error ${e.code()}", e)
            } catch (e: Exception) {
                // その他の予期せぬエラー
                _dashboardUiState.value = DashboardUiState.Error
                Log.e("DashboardViewModel", "Unexpected error", e)
            }
        }
    }
}

