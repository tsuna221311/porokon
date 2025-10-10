package com.example.myapplication.network

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException


val baseUrl = "http://59.106.209.150:8080/"

class AuthInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val user = FirebaseAuth.getInstance().currentUser

        // 元のrequestを取得
        var request = chain.request()

        // ユーザーがログインしている場合のみ、トークンを付与する
        if (user != null) {
            // getIdToken(false) はキャッシュからトークンを取得するタスクを返す
            val task = user.getIdToken(false)

            // ★★★ 修正ポイント ★★★
            // Tasks.await() を使わずに、タスクが既に完了しているか（＝キャッシュにトークンがあったか）を確認する
            // これにより、スレッドをブロックせず、クラッシュを防ぐ
            if (task.isSuccessful) {
                val token = task.result?.token
                if (!token.isNullOrBlank()) {
                    Log.d("AuthInterceptor", "Token successfully attached.")
                    // トークンがある場合は、ヘッダーに付与した新しいrequestを作成
                    request = request.newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                        .build()
                }
            } else {
                // キャッシュにトークンがない場合、ここでは何もせずリクエストを続行する
                // ネットワークをブロックしないことが重要
                Log.w("AuthInterceptor", "Token is not immediately available from cache.")
            }
        }

        // 最終的なrequestで通信を実行
        return chain.proceed(request)
    }
}

private val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(AuthInterceptor())
    .build()

var gson = GsonBuilder()
    .setLenient()
    .create()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create(gson))
    .baseUrl(baseUrl)
    .client(okHttpClient)
    .build()

object ApiClient {
    val service: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}