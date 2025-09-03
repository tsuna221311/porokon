package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.myapplication.ui.LoginScreen
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
            viewModel.registerUser(user.uid)
        } else {
            viewModel.checkLoginState() // ログイン失敗時も状態 // 更新
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AmuNaviTheme {
                val state by viewModel.loginState.collectAsState()

                if (state is LoginViewModel.LoginState.Success) {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }

                LoginScreen(
                    state = state,
                    onRetry = { showSignInScreen() }
                )
            }
        }

        viewModel.checkLoginState()
    }

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
