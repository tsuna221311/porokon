package com.example.myapplication.network

import com.example.myapplication.model.Work
import com.google.firebase.auth.FirebaseAuth
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

private const val BASE_URL = "http://10.0.2.2:8080/"

// --- UIDを自動で付与するInterceptor ---
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
    .baseUrl(BASE_URL)
    .client(okHttpClient)
    .build()

// --- API定義 ---
interface ApiService {
    @GET("v1/works")
    suspend fun getWorks(): List<Work>
}

object ApiClient {
    val service: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
