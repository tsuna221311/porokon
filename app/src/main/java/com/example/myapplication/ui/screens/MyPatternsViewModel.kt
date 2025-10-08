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

// MyPatterns画面のUI状態
sealed interface MyPatternsUiState {
    data class Success(val works: List<Work>) : MyPatternsUiState
    object Error : MyPatternsUiState
    object Loading : MyPatternsUiState
}

class MyPatternsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<MyPatternsUiState>(MyPatternsUiState.Loading)
    val uiState: StateFlow<MyPatternsUiState> = _uiState.asStateFlow()

    init {
        // ViewModelが初期化されたら作品リストの読み込みを開始
        loadMyPatterns()
    }

    fun loadMyPatterns() {
        viewModelScope.launch {
            _uiState.value = MyPatternsUiState.Loading
            try {
                // APIを呼び出して作品リストを取得
                val response = ApiClient.service.getAllWorks()
                if (response.isSuccessful) {
                    _uiState.value = MyPatternsUiState.Success(response.body() ?: emptyList())
                } else {
                    throw Exception("Failed to load works: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("MyPatternsViewModel", "Error loading patterns", e)
                _uiState.value = MyPatternsUiState.Error
            }
        }
    }
}