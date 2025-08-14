package com.example.myapplication.network

import com.example.myapplication.model.Work
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("v1/works")
    suspend fun getWorks(): List<Work>
    @POST("v1/users")
    suspend fun registerUser()
}
