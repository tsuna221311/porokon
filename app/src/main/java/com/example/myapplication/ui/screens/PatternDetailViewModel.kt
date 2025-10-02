package com.example.myapplication.ui.screens

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.IncrementStitchRequest
import com.example.myapplication.model.Work
import com.example.myapplication.network.ApiClient
import com.example.myapplication.network.GCSApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.Instant

class PatternDetailViewModel (
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val id: Int = savedStateHandle.get<Int>("workId") ?: 100

    private val _work = MutableStateFlow<Work?>(null)
    val work: StateFlow<Work?> = _work

    init {
        fetchWork()
    }

    private fun fetchWork(){
        viewModelScope.launch {
            try {
                val body = ApiClient.service.getOneWork(id).body()
                    _work.value = body
                    fetchCsv(body?.work_url ?: "")

            } catch (e: Exception) {
                Log.e("PatternViewModel", "エラー", e)
                _work.value = null
            }
        }
    }

    public fun IncrementStitch(request: IncrementStitchRequest) {
        viewModelScope.launch {
            try {
                val result = ApiClient.service.incrementStitch(id, request).body()
                Log.d("DBResult", result.toString())
                if (_work.value != null) {
                    _work.value = _work.value!!.copy(
                        raw_index = result?.raw_index ?: 10000,
                        stitch_index = result?.stitch_index ?: 10000
                    )
                }
            } catch (e: Exception) {
                Log.e("PatternViewModel", "エラー", e)
            }
        }
    }

    private fun fetchCsv(signedUrl: String) {
        viewModelScope.launch {
            try {
                val response = GCSApiClient.service.downloadCsv(signedUrl)
                if (response.isSuccessful) {
                    val csvText = response.body()?.string() ?: ""
                    Log.d("csvText",csvText) // CSV の内容
                } else {
                    Log.d("csvText","Failed: ${response.message()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
