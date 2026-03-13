package com.example.s1_catalog.view

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.s1_catalog.controller.MainActivity
import com.example.s1_catalog.controller.RegisterActivity
import com.example.s1_catalog.model.UserRepository


@Composable
fun LoginScreen() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var stayConnected by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Login",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                errorText = null
            },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                errorText = null
            },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )
        
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(
                checked = stayConnected,
                onCheckedChange = { stayConnected = it },
                enabled = !isLoading
            )
            Text(
                text = "תשאיר אותי מחובר",
                modifier = Modifier.clickable { stayConnected = !stayConnected }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        TextButton(
            onClick = {
                context.startActivity(Intent(context, RegisterActivity::class.java))
            },
            enabled = !isLoading
        ) {
            Text(
                "new user? click here to register",
                textDecoration = TextDecoration.Underline,
                color = MaterialTheme.colorScheme.primary
            )
        }

        if (errorText != null) {
            Spacer(Modifier.height(10.dp))
            Text(
                text = errorText!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = {
                    val e = email.trim()
                    val p = password

                    if (e.isEmpty() || p.isEmpty()) {
                        errorText = "נא למלא אימייל וסיסמה"
                        return@Button
                    }

                    isLoading = true
                    UserRepository.findUserByEmail(e) { user ->
                        isLoading = false
                        if (user != null && user.password == p) {
                            errorText = null
                            UserRepository.setStayConnected(context, user.id, stayConnected)
                            UserRepository.init(context, user.id)
                            context.startActivity(
                                Intent(context, MainActivity::class.java).apply {
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                                            Intent.FLAG_ACTIVITY_CLEAR_TASK
                                }
                            )
                        } else {
                            errorText = "אימייל או סיסמה שגויים"
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("התחבר")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}
