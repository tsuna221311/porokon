package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.myapplication.ui.LoginScreen // ★★★ このimport文を追加 ★★★
import com.example.myapplication.ui.theme.AmuNaviTheme
import com.example.myapplication.viewmodel.LoginViewModel
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : ComponentActivity() {

    private val viewModel: LoginViewModel by viewModels()

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { result ->
        val user = FirebaseAuth.getInstance().currentUser
        if (result.resultCode == RESULT_OK && user != null) {
            viewModel.registerUser()
        } else {
            viewModel.setLoginFailure("ログインがキャンセルされました。")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AmuNaviTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val state by viewModel.loginState.collectAsState()

                    // ログイン成功を検知したらMainActivityに遷移
                    LaunchedEffect(state) {
                        if (state is LoginViewModel.LoginState.Success) {
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        }
                    }

                    // LoginScreenを呼び出す
                    LoginScreen(
                        state = state,
                        onRetry = { showSignInScreen() }
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // アプリ起動時にログイン状態を確認
        viewModel.checkLoginState()
    }

    // Firebase UIのログイン画面を表示する関数
    private fun showSignInScreen() {
        val providers = listOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
        signInLauncher.launch(signInIntent)
    }
}
