package com.example.myapplication.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.network.ApiClient
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    // ログイン画面が取りうる状態を定義
    sealed class LoginState {
        object Initial : LoginState() // 初期状態
        object Checking : LoginState() // ログイン確認中
        object ApiLoading : LoginState() // API通信中
        data class Success(val uid: String) : LoginState() // 成功
        data class Failure(val reason: String) : LoginState() // 失敗
    }

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Initial)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    /**
     * 現在のログイン状態を確認する。
     * ログイン済みであれば、バックエンドへのユーザー登録処理を開始する。
     */
    fun checkLoginState() {
        viewModelScope.launch {
            _loginState.value = LoginState.Checking
            val currentUser = auth.currentUser
            if (currentUser != null) {
                // ログイン済みならユーザー登録/同期処理へ
                registerUser()
            } else {
                // 未ログインなら失敗状態にしてUIに通知（Firebase UIの表示を促す）
                _loginState.value = LoginState.Failure("ログインしていません。")
            }
        }
    }

    /**
     * Firebaseで認証済みのユーザーを、バックエンドAPIに登録する。
     */
    fun registerUser() {
        viewModelScope.launch {
            val user = auth.currentUser
            if (user == null) {
                _loginState.value = LoginState.Failure("Firebaseユーザーが見つかりません。")
                return@launch
            }

            _loginState.value = LoginState.ApiLoading
            try {
                // APIを呼び出してユーザー登録を試みる
                val response = ApiClient.service.registerUser()

                // APIからのレスポンスが成功（2xx）または既に存在する（409 Conflict）場合は成功とみなす
                if (response.isSuccessful || response.code() == 409) {
                    _loginState.value = LoginState.Success(user.uid)
                } else {
                    throw Exception("API登録に失敗しました: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "API Registration failed", e)
                _loginState.value = LoginState.Failure(e.message ?: "APIエラーが発生しました")
            }
        }
    }

    /**
     * Firebase UIがキャンセルされた場合など、外部からログイン失敗状態を設定する。
     */
    fun setLoginFailure(reason: String) {
        _loginState.value = LoginState.Failure(reason)
    }
}
