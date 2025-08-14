package com.example.myapplication.network

import com.example.myapplication.model.Work
import retrofit2.http.GET

interface ApiService {
    @GET("v1/works")
    suspend fun getWorks(): List<Work>
}
