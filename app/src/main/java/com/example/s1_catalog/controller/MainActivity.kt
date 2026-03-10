package com.example.s1_catalog.controller

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.s1_catalog.view.MainScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Scaffold(
                    floatingActionButton = {
                        FloatingActionButton(onClick = {
                            startActivity(Intent(this, AdminActivity::class.java))
                        }) {
                            Icon(Icons.Default.Add, contentDescription = "Add item")
                        }
                    }
                ) { innerPadding ->
                    MainScreen(modifier = Modifier.Companion.padding(innerPadding))
                }
            }
        }
    }
}