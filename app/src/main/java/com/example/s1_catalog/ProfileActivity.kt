package com.example.s1_catalog

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
    val currentProfile = UserProfileRepository.userProfile
    
    // Valeurs initiales chargées depuis le dépôt
    var name by remember { mutableStateOf(currentProfile.name) }
    var email by remember { mutableStateOf(currentProfile.email) }
    var phone by remember { mutableStateOf(currentProfile.phone) }

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
            placeholder = { Text(if (currentProfile.name.isEmpty()) "Enter your name" else currentProfile.name) },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            placeholder = { Text(if (currentProfile.email.isEmpty()) "Enter your email" else currentProfile.email) },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone") },
            placeholder = { Text(if (currentProfile.phone.isEmpty()) "Enter your phone" else currentProfile.phone) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                UserProfileRepository.saveProfile(context, UserProfile(name, email, phone))
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
