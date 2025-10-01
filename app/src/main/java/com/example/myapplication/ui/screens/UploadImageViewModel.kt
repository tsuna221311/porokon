package com.example.myapplication.ui.screens

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.R
import com.example.myapplication.network.ApiClient
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.File

class UploadImageViewModel : ViewModel() {


    // 呼び出し例 (Navigation.kt を想定)
    /**
     *     val viewModal: UploadImageViewModel = viewModel()
     *     viewModal.uploadImage(context = LocalContext.current)
     *
     */
    fun uploadImage(context: Context) {
        viewModelScope.launch {
            try {
                val inputStream = context.resources.openRawResource(R.raw.input)
                val tempFile = File(context.cacheDir, "sample.jpg")
                tempFile.outputStream().use { output ->
                    inputStream.copyTo(output)
                }

                val requestFile = tempFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("file", tempFile.name, requestFile)

                val result = ApiClient.service.uploadImage(body)
                Log.d("CevResult", result.body()?.csv ?: "csv結果来なかった")

            } catch (e: Exception) {
                Log.e("Exception", "アップロード失敗: ", e)
            }
        }
    }
}
