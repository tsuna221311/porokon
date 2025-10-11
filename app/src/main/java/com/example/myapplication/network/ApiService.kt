package com.example.myapplication.network

import com.example.myapplication.model.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

/**
 * アプリケーションが利用するすべてのAPIエンドポイントを定義するインターフェース。
 * Retrofitライブラリによって、このインターフェースが実際のAPI通信コードに変換されます。
 */
interface ApiService {

    // --- ユーザー関連 ---

    @POST("v1/users")
    suspend fun registerUser(): Response<User>

    @PATCH("v1/users")
    suspend fun changeDisplayName(@Body request: ChangeDisplayNameRequest): Response<User>

    // --- 作品 (Work) 関連 ---
    @POST("/v1/works")
    suspend fun createWork(@Body request: CreateWorkRequest): Response<Work>

    @GET("v1/works")
    suspend fun getAllWorks(@Query("completed") completed: Boolean? = null): Response<List<Work>>

    @POST("v1/works")
    suspend fun registerWork(@Body work: RegisterWork): Response<Work>

    @GET("v1/works/{id}")
    suspend fun getOneWork(@Path("id") id: Int): Response<Work>

    @PUT("v1/works/{id}")
    suspend fun updateWork(@Path("id") id: Int, @Body request: UpdateWorkRequest): Response<Work>

    @PATCH("v1/works/{id}")
    suspend fun incrementStitch(@Path("id") id: Int, @Body request: IncrementStitchRequest): Response<Work>

    @DELETE("v1/works/{id}")
    suspend fun deleteWork(@Path("id") id: Int): Response<Unit>

    // --- OCR / ファイルアップロード関連 ---

    @Multipart
    @POST("v1/csv-conversions")
    suspend fun uploadChartImage(@Part file: MultipartBody.Part): Response<CsvConversions>

    @Multipart
    @POST("v1/ocr")
    suspend fun uploadOcrImage(@Part file: MultipartBody.Part): Response<OcrResponse>

    @Multipart
    @POST("v1/fix-csv")
    suspend fun uploadFixedCsv(@Part file: MultipartBody.Part): Response<CsvConversions>
}


