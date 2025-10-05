package com.example.myapplication.network

import com.example.myapplication.model.ChangeDisplayNameRequest
import com.example.myapplication.model.CsvConversions
import com.example.myapplication.model.IncrementStitchRequest
import com.example.myapplication.model.RegisterWork
import com.example.myapplication.model.User
import com.example.myapplication.model.Work
import okhttp3.Call
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Url

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
        @Body work : RegisterWork
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

    @DELETE("v1/works/{id}")
    suspend fun deleteWork(
        @Path("id") id: Int
    )

    @Multipart
    @POST("v1/csv-conversions")
    suspend fun uploadImage(
        @Part file: MultipartBody.Part
    ): Response<CsvConversions>
}
