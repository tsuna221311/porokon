package com.example.myapplication.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit

private val downloadClient = OkHttpClient.Builder()
    .build() // 認証なし

private val downloadRetrofit = Retrofit.Builder()
    .baseUrl("https://storage.googleapis.com/") // ダミーでも必要
    .client(downloadClient)
    .build()

object GCSApiClient {
    val service: GCSApiService by lazy {
        downloadRetrofit.create(GCSApiService::class.java)
    }
}