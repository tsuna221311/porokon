package com.example.myapplication.ui.screens

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.R
import com.example.myapplication.model.CsvConversions
import com.example.myapplication.model.RegisterWork
import com.example.myapplication.model.Work
import com.example.myapplication.network.ApiClient
import com.google.protobuf.Api
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.File

sealed interface UploadResult {
    object Loading : UploadResult
    data class Success(val response: CsvConversions?) : UploadResult
    object Error : UploadResult
}

class RegisterWorkViewModel : ViewModel() {

    private val _uploadResult = MutableStateFlow<UploadResult>(UploadResult.Loading)
    val uploadResult: StateFlow<UploadResult> = _uploadResult.asStateFlow()

    suspend fun uploadImage(context: Context): CsvConversions? {
        return try {
            val inputStream = context.resources.openRawResource(R.raw.input)
            val tempFile = File(context.cacheDir, "sample.jpg")
            tempFile.outputStream().use { output ->
                inputStream.copyTo(output)
            }

            val requestFile = tempFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", tempFile.name, requestFile)

            val result = ApiClient.service.uploadImage(body)
            _uploadResult.value = UploadResult.Success(result.body())
            result.body()

        } catch (e: Exception) {
            Log.e("Exception", "アップロード失敗: ", e)
            _uploadResult.value = UploadResult.Error
            null
        }
    }

    fun registerWork(context: Context) {
        viewModelScope.launch {
            val res = uploadImage(context)
            if (res != null) {
                try {
                    val res = ApiClient.service.registerWork(
                        RegisterWork(
                            title = "strung",
                            work_url = res.csv_url,
                            description = "haihai"
                        )
                    )

                    Log.d("RegisterWork", res.toString())
                } catch (e: Exception) {
                    Log.e("RegisterWork", "Exception : ", e)
                }
            }
        }
    }
}
