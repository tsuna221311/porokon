package com.example.myapplication.network

import com.example.myapplication.R
import com.google.firebase.auth.FirebaseAuth
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val baseUrl = "http://10.0.2.2:8080/"

class UidInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val uid = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
        val request = chain.request().newBuilder()
            .addHeader("uid", uid)
            .build()
        return chain.proceed(request)
    }
}

private val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(UidInterceptor())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(baseUrl)
    .client(okHttpClient)
    .build()

object ApiClient {
    val service: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}