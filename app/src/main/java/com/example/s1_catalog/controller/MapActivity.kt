package com.example.s1_catalog.controller

import android.content.Context
import android.location.Geocoder
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.s1_catalog.model.CatalogRepository
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import java.util.*

class MapActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MapScreen(onBack = { finish() })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val restaurants = CatalogRepository.getItems()
    
    // Default location Israel
    val israel = LatLng(32.0853, 34.7818)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(israel, 10f)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Restaurants Map") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Back") }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState
            ) {
                restaurants.forEach { restaurant ->
                    val location = getLatLngFromAddress(context, restaurant.address)
                    if (location != null) {
                        Marker(
                            state = MarkerState(position = location),
                            title = restaurant.title,
                            snippet = "${restaurant.cuisine} - ${restaurant.kashrut}"
                        )
                    }
                }
            }
        }
    }
}

// Fonction utilitaire pour convertir une adresse en LatLng
fun getLatLngFromAddress(context: Context, strAddress: String): LatLng? {
    val coder = Geocoder(context, Locale.getDefault())
    return try {
        @Suppress("DEPRECATION")
        val address = coder.getFromLocationName(strAddress, 1)
        if (address != null && address.isNotEmpty()) {
            val location = address[0]
            LatLng(location.latitude, location.longitude)
        } else null
    } catch (e: Exception) {
        null
    }
}
