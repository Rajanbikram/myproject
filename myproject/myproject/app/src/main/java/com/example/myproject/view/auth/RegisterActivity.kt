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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myproject.model.User
import com.example.myproject.repository.UserRepoImpl
import com.example.myproject.view.screens.AuthButton
import com.example.myproject.view.screens.AuthTextField
import com.example.myproject.view.ui.theme.AccentPurple
import com.example.myproject.view.ui.theme.DarkBackground
import com.example.myproject.view.ui.theme.DarkPurple
import com.example.myproject.view.ui.theme.TextGray
import com.example.myproject.view.ui.theme.TextWhite
import com.example.myproject.viewmodel.UserViewModel

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RegisterScreen()
        }
    }
}

@Composable
fun RegisterScreen() {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var context = LocalContext.current
    var activity = context as? Activity

    val userViewModel = UserViewModel(UserRepoImpl())



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

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Create Your Account",
                color = TextWhite,
                fontSize = 24.sp,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Username Field
            AuthTextField(
                value = username,
                onValueChange = { username = it },
                label = "Username",
                icon = Icons.Default.Person
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Email Field
            AuthTextField(
                value = email,
                onValueChange = { email = it },
                label = "E-Mail",
                icon = Icons.Default.Email
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password Field
            AuthTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                icon = Icons.Default.Lock,
                isPassword = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm Password Field
            AuthTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = "Confirm Password",
                icon = Icons.Default.Lock,
                isPassword = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Sign Up Button
            AuthButton(
                text = "SIGN UP",
                onClick = {
                    userViewModel.register(email, password) { success, message, userId ->
                        if (success) {
                            val user = User(
                                uid = userId,
                                username = username,
                                email = email
                            )
                            userViewModel.addUserToDatabase(userId, user) { success, message ->
                                if (success) {
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                    activity?.finish()
                                } else {
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    }

                },

                )


            Spacer(modifier = Modifier.height(32.dp))

            // Sign In Link
            Row {
                Text(
                    text = "Already have an account? ",
                    color = TextGray,
                    fontSize = 14.sp,
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
fun regPrev(){
    RegisterScreen()
}