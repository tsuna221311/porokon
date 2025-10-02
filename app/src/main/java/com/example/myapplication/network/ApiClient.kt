package com.example.myapplication.network

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.android.gms.tasks.Tasks
import java.util.concurrent.TimeUnit

val baseUrl = "http://10.0.2.2:8080/"

class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val user = FirebaseAuth.getInstance().currentUser
        var token: String? = null

        if (user != null) {
            val task = user.getIdToken(false) // false = キャッシュ使う
            token = Tasks.await(task, 5, TimeUnit.SECONDS).token
        }

        val request = chain.request().newBuilder()
            .apply {
                if (!token.isNullOrBlank()) {
                    Log.d("token", token)
                    addHeader("Authorization", "Bearer $token")
                }
            }
            .build()

        return chain.proceed(request)
    }
}

private val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(AuthInterceptor())
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