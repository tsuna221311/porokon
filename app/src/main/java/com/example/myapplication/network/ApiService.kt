package com.example.myapplication.network

import com.example.myapplication.model.IncrementStitchRequest
import com.example.myapplication.model.Work
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @GET("v1/works")
    suspend fun getAllWorks(): Response<List<Work>>
    @GET("v1/works")
    suspend fun getOneWork(
        @Path("id") id: Int
    ): Response<Work>
    @POST("v1/users")
    suspend fun registerUser(): Response<Unit>

    @PATCH("v1/works/{id}")
    suspend fun incrementStitch(
        @Path("id") id: Int,
        @Body request: IncrementStitchRequest
    ): Response<Work>

    @DELETE("v1/works/{id}")
    suspend fun deleteWork(
        @Path("id") id: Int
    )

}
