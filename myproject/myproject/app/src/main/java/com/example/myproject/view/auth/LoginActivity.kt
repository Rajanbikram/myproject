package com.example.myproject.view.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myproject.repository.UserRepoImpl
import com.example.myproject.view.screens.AuthButton
import com.example.myproject.view.screens.AuthTextField
import com.example.myproject.view.DashboardActivity
import com.example.myproject.view.ui.theme.AccentPurple
import com.example.myproject.view.ui.theme.DarkBackground
import com.example.myproject.view.ui.theme.DarkPurple
import com.example.myproject.view.ui.theme.TextGray
import com.example.myproject.viewmodel.UserViewModel

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Initialize repo once
            val repository = UserRepoImpl()
            val userViewModel = UserViewModel(repository)
            LoginScreen(userViewModel)
        }
    }
}

@Composable
fun LoginScreen(userViewModel: UserViewModel) { // Pass ViewModel as parameter
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) } // State for loading indicator

    val context = LocalContext.current
    val activity = context as? Activity

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(DarkPurple, DarkBackground)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Logo",
                modifier = Modifier.size(80.dp),
                tint = AccentPurple
            )

            Spacer(modifier = Modifier.height(48.dp))

            AuthTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email", // Changed from Username to Email for clarity
                icon = Icons.Default.Person
            )

            Spacer(modifier = Modifier.height(16.dp))

            AuthTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                icon = Icons.Default.Lock,
                isPassword = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Forgot your password?",
                color = TextGray,
                fontSize = 14.sp,
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable {
                        context.startActivity(Intent(context, ForgotPasswordActivity::class.java))
                    }
                    .padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            AuthButton(
                text = if (isLoading) "LOADING..." else "SIGN IN",
                onClick = {
                    if (email.isNotEmpty() && password.isNotEmpty()) {
                        isLoading = true
                        userViewModel.login(email, password) { success, message ->
                            isLoading = false
                            if (success) {
                                context.startActivity(
                                    Intent(
                                        context,
                                        DashboardActivity::class.java
                                    )
                                )
                                activity?.finish()
                            } else {
                                // CRITICAL: Show the error message!
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                            }
                        }
                    } else {
                        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                    }
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row {
                Text(text = "Don't have an account? ", color = TextGray, fontSize = 14.sp)
                Text(
                    text = "SIGN UP",
                    color = AccentPurple,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable {
                        context.startActivity(Intent(context, RegisterActivity::class.java))
                    }
                )
            }
        }
    }
}
