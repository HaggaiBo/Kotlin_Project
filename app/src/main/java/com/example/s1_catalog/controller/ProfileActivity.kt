package com.example.s1_catalog.controller

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.s1_catalog.model.UserData
import com.example.s1_catalog.model.UserRepository

class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProfileScreen(onBack = { finish() })
        }
    }
}

@Composable
fun ProfileScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val currentUser = UserRepository.currentUser
    
    var name by remember(currentUser.name) { mutableStateOf(currentUser.name) }
    var email by remember(currentUser.email) { mutableStateOf(currentUser.email) }
    var phone by remember(currentUser.phone) { mutableStateOf(currentUser.phone) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "User Profile", style = MaterialTheme.typography.headlineMedium)
        
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                val updatedUser = currentUser.copy(
                    name = name,
                    email = email,
                    phone = phone
                )
                UserRepository.saveUser(updatedUser)
                Toast.makeText(context, "Profile Saved", Toast.LENGTH_SHORT).show()
                onBack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Profile")
        }
        
        TextButton(onClick = onBack) {
            Text("Cancel")
        }
    }
}
