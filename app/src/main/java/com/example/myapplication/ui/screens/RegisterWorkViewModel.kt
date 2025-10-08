package com.example.myapplication.ui.screens

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.RegisterWork
import com.example.myapplication.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 作品保存画面のUIが取りうる状態を定義します。
 */
sealed interface RegisterWorkUiState {
    object Standby : RegisterWorkUiState // 初期状態
    object Loading : RegisterWorkUiState // 保存中
    object Success : RegisterWorkUiState // 保存成功
    data class Error(val message: String) : RegisterWorkUiState // 保存失敗
}

/**
 * 新しい作品をサーバーに登録するためのViewModel。
 */
class RegisterWorkViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow<RegisterWorkUiState>(RegisterWorkUiState.Standby)
    val uiState: StateFlow<RegisterWorkUiState> = _uiState.asStateFlow()

    // ConfirmPhotoScreenからナビゲーション経由で渡された、アップロード済み編み図データ(CSV)のURL
    private val workUrl: String = checkNotNull(savedStateHandle["fileUrl"])

    /**
     * UI（SavePatternScreen）から呼び出され、新しい作品をサーバーに登録します。
     * @param title ユーザーが入力した作品のタイトル。
     * @param description ユーザーが入力したメモ。
     */
    fun registerNewWork(title: String, description: String) {
        viewModelScope.launch {
            _uiState.value = RegisterWorkUiState.Loading
            try {
                // APIに送信するためのリクエストデータを作成
                val request = RegisterWork(
                    title = title,
                    description = description,
                    file_name = workUrl
                )

                // APIクライアントを呼び出して、実際にサーバーに作品を登録する
                val response = ApiClient.service.registerWork(request)

                if (response.isSuccessful) {
                    _uiState.value = RegisterWorkUiState.Success
                } else {
                    // APIがエラーを返した場合
                    throw Exception("Failed to register work: ${response.message()}")
                }
            } catch (e: Exception) {
                // 通信失敗など、何か問題が起きたらUIを「エラー」状態にする
                Log.e("RegisterWorkViewModel", "Error registering work", e)
                _uiState.value = RegisterWorkUiState.Error("作品の登録に失敗しました。")
            }
        }
    }
}
