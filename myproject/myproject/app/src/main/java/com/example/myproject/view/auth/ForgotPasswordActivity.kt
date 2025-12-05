package com.example.myproject.view.auth

import android.app.Activity
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myproject.repository.UserRepoImpl
import com.example.myproject.view.screens.AuthButton
import com.example.myproject.view.screens.AuthTextField
import com.example.myproject.view.ui.theme.AccentPurple
import com.example.myproject.view.ui.theme.DarkBackground
import com.example.myproject.view.ui.theme.DarkPurple
import com.example.myproject.view.ui.theme.TextGray
import com.example.myproject.view.ui.theme.TextWhite
import com.example.myproject.viewmodel.UserViewModel

class ForgotPasswordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ForgotPasswordScreen()
        }
    }
}

@Composable
fun ForgotPasswordScreen() {
    val userViewModel = UserViewModel(UserRepoImpl())

    var email by remember { mutableStateOf("") }
    var showSuccessMessage by remember { mutableStateOf(false) }

    var context = LocalContext.current
    var activity = context as? Activity

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
            // Icon
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = "Email",
                modifier = Modifier.size(80.dp),
                tint = AccentPurple
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Reset Password",
                color = TextWhite,
                fontSize = 28.sp,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Enter your email address and we'll send you instructions to reset your password",
                color = TextGray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Email Field
            AuthTextField(
                value = email,
                onValueChange = {
                    email = it
                    showSuccessMessage = false
                },
                label = "E-Mail",
                icon = Icons.Default.Email
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Reset Button
            AuthButton(
                text = "RESET PASSWORD",
                onClick = {
                    userViewModel.forgetPassword(email) { success, message ->
                        if (success) {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            activity?.finish()
                        } else {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }

                    }
                },

                )

            Spacer(modifier = Modifier.height(16.dp))

            // Success/Error Messages
            if (showSuccessMessage) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF2E7D32).copy(alpha = 0.2f)
                    )
                ) {
                    Text(
                        text = "Password reset email sent! Please check your inbox.",
                        color = Color(0xFF4CAF50),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }



            Spacer(modifier = Modifier.height(32.dp))

            // Back to Login
            Row {
                Text(
                    text = "Remember your password? ",
                    color = TextGray,
                    fontSize = 14.sp
                )
                Text(
                    text = "SIGN IN",
                    color = AccentPurple,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable {
                        activity?.finish()
                    }
                )
            }
        }
    }
}


@Preview
@Composable
fun forgotPrev(){
    ForgotPasswordScreen()
}