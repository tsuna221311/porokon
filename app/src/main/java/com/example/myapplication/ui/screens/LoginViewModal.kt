package com.example.myapplication.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.network.ApiClient
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Checking)
    val loginState: StateFlow<LoginState> = _loginState

    fun checkLoginState() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            _loginState.value = LoginState.ApiLoading(user.uid)
            registerUser(user.uid)
        } else {
            _loginState.value = LoginState.Failure("未ログイン")
        }
    }

    fun registerUser(uid: String) {
        viewModelScope.launch {
            try {
                val response = ApiClient.service.registerUser()
                if (response.isSuccessful) {
                    _loginState.value = LoginState.Success(uid)
                } else {
                    _loginState.value =
                        LoginState.Failure("ユーザー登録失敗: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "registerUser error", e)
                _loginState.value = LoginState.Failure("エラー: ${e.message}")
            }
        }
    }

    sealed class LoginState {
        object Checking : LoginState()
        data class ApiLoading(val uid: String) : LoginState()
        data class Success(val uid: String) : LoginState()
        data class Failure(val reason: String) : LoginState()
    }
}
