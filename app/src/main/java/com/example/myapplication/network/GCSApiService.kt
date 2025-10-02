package com.example.myapplication.network

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface GCSApiService {
    @GET
    suspend fun downloadCsv(
        @Url signedUrl: String
    ): Response<ResponseBody>
}