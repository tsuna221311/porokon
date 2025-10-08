package com.example.myapplication.network

import com.example.myapplication.model.ChangeDisplayNameRequest
import com.example.myapplication.model.CsvConversions
import com.example.myapplication.model.IncrementStitchRequest
import com.example.myapplication.model.RegisterWork
import com.example.myapplication.model.User
import com.example.myapplication.model.Work
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

// 作品情報（主に work_url）を更新するためのリクエストボディ
data class UpdateWorkRequest(
    val work_url: String
)

interface ApiService {
    @POST("v1/users")
    suspend fun registerUser(): Response<Unit>

    @PATCH("v1/users")
    suspend fun changeDisplayName(
        @Body request: ChangeDisplayNameRequest
    ): Response<User>

    @GET("v1/works")
    suspend fun getAllWorks(): Response<List<Work>>

    @POST("v1/works")
    suspend fun registerWork(
        @Body work: RegisterWork
    ): Response<Work>

    @GET("v1/works/{id}")
    suspend fun getOneWork(
        @Path("id") id: Int
    ): Response<Work>

    @PATCH("v1/works/{id}")
    suspend fun incrementStitch(
        @Path("id") id: Int,
        @Body request: IncrementStitchRequest
    ): Response<Work>

    // ★★★ PatternEditViewModelで必要な定義を追加 ★★★
    @PATCH("v1/works/{id}")
    suspend fun updateWork(@Path("id") id: Int, @Body request: UpdateWorkRequest): Response<Work>

    @DELETE("v1/works/{id}")
    suspend fun deleteWork(
        @Path("id") id: Int
    )

    @Multipart
    @POST("v1/csv-conversions")
    suspend fun uploadImage(
        @Part file: MultipartBody.Part
    ): Response<CsvConversions>

    // ★★★ PatternEditViewModelで必要な定義を追加 ★★★
    @Multipart
    @POST("v1/csv-uploads") // 仮のエンドポイント名です。実際のAPI仕様に合わせてください。
    suspend fun uploadCsv(@Part file: MultipartBody.Part): Response<CsvConversions>
}
