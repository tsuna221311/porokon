package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.AmuNaviTheme
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : ComponentActivity() {

    private val loginState = mutableStateOf<LoginState>(LoginState.Checking)

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { result ->
        val user = FirebaseAuth.getInstance().currentUser
        loginState.value = if (result.resultCode == RESULT_OK && user != null) {
            LoginState.Success(user.uid)
        } else {
            LoginState.Failure("ログイン失敗")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AmuNaviTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.padding(16.dp), contentAlignment = Alignment.Center) {
                        when (val state = loginState.value) {
                            is LoginState.Checking -> Text("ログイン状態を確認中...", fontSize = 20.sp)

                            is LoginState.Success -> {
                                // 画面遷移
                                LaunchedEffect(Unit) {
                                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                    startActivity(intent)
                                    finish() // LoginActivity を閉じる
                                }
                                Text("ログイン成功！UID: ${state.uid}", fontSize = 20.sp)
                            }

                            is LoginState.Failure -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("ログイン失敗：${state.reason}", fontSize = 20.sp)
                                Spacer(Modifier.height(16.dp))
                                Button(onClick = { launchSignIn() }) {
                                    Text("再試行")
                                }
                            }
                        }

                    }
                }
            }
        }

        // 起動時にログイン状態を確認
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            loginState.value = LoginState.Success(user.uid)
        } else {
            launchSignIn()
        }
    }

    private fun launchSignIn() {
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

    sealed class LoginState {
        object Checking : LoginState()
        data class Success(val uid: String) : LoginState()
        data class Failure(val reason: String) : LoginState()
    }
}
