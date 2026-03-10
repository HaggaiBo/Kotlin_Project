package com.example.s1_catalog.controller

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.s1_catalog.model.UserData
import com.example.s1_catalog.model.UserRepository

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RegisterScreen(
                onRegisterSuccess = {
                    Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            )
        }
    }
}

@Composable
fun RegisterScreen(onRegisterSuccess: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    
    var errorText by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Register",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it; errorText = null },
            label = { Text("Full Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it; errorText = null },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it; errorText = null },
            label = { Text("Phone") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it; errorText = null },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it; errorText = null },
            label = { Text("Confirm Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        if (errorText != null) {
            Spacer(Modifier.height(10.dp))
            Text(
                text = errorText!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(Modifier.height(24.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = {
                    val n = name.trim()
                    val e = email.trim()
                    val ph = phone.trim()
                    val p = password
                    val cp = confirmPassword

                    if (n.isEmpty() || e.isEmpty() || ph.isEmpty() || p.isEmpty()) {
                        errorText = "נא למלא את כל השדות"
                        return@Button
                    }

                    if (p != cp) {
                        errorText = "הסיסמאות אינן תואמות"
                        return@Button
                    }

                    isLoading = true
                    
                    // Check if email already exists
                    UserRepository.findUserByEmail(e) { existingUser ->
                        if (existingUser != null) {
                            isLoading = false
                            errorText = "האימייל כבר קיים במערכת"
                        } else {
                            val newUser = UserData(
                                name = n,
                                email = e,
                                phone = ph,
                                password = p
                            )
                            UserRepository.saveUser(newUser)
                            isLoading = false
                            onRegisterSuccess()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Register")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(onRegisterSuccess = {})
}
