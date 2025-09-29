package com.example.myapplication.ui.screens

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.Work
import com.example.myapplication.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.Instant

class PatternViewModel (
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val id: Int = savedStateHandle.get<Int>("workId") ?: 0

    private val _work = MutableStateFlow<Work?>(null)
    val work: StateFlow<Work?> = _work

    init {
        fetchWork()
    }

    private fun fetchWork(){
        viewModelScope.launch {
            try {
                val result = ApiClient.service.getOneWork(id)

                Log.d("nakami", result.body().toString())

                    _work.value = result.body() ?: Work(
                        id = 0,
                        title = "鯖には何もなかった",
                        description = "完了！ - 2024/02/01",
                        work_url = "",
                        raw_index = 5,
                        stitch_index = 0,
                        is_completed = true,
                        completed_at = Instant.now().toString(),
                        created_at = Instant.now().toString(),
                        updated_at = Instant.now().toString()
                    )
            } catch (e: Exception) {
                Log.e("PatternViewModel", "エラー", e)
                _work.value = null
            }
        }
    }
}
