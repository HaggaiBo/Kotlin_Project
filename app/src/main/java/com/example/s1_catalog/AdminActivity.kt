package com.example.s1_catalog

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color

import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage

class AdminActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val itemId = intent.getStringExtra("ITEM_ID")

        setContent {
            AdminScreen(
                itemId = itemId,
                onSaveComplete = { finish() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
fun AdminScreen(itemId: String?, onSaveComplete: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var videoUrl by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }

    val context = LocalContext.current

    val kashrutOptions = listOf(
        "כשר", "הרבנות המקומית", "מהדרין", "בד\"ץ בית יוסף",
        "בד\"ץ העדה החרדית", "בד\"ץ שארית ישראל", "מרפוד", "לנדה", "ללא תעודה"
    )
    var selectedKashrut by remember { mutableStateOf(kashrutOptions[0]) }

    val cuisineOptions = listOf("בשרים", "חלבי", "אסייתי", "טבעוני", "ארוחות בוקר", "דגים", "איטלקי", "יפני")
    var selectedCuisine by remember { mutableStateOf(cuisineOptions[0]) }

    LaunchedEffect(itemId) {
        if (itemId != null) {
            val item = CatalogRepository.getItemById(itemId)
            if (item != null) {
                title = item.title
                description = item.description
                address = item.address
                videoUrl = item.videoUrl
                selectedKashrut = item.kashrut
                selectedCuisine = item.cuisine
                imageUrl = item.imageUrl
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (itemId == null) "Add New Restaurant" else "Edit Restaurant",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("שם של המסעדה") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("תיאור") }, modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 100.dp))
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("כתובת") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = imageUrl, onValueChange = { imageUrl = it }, label = { Text("Image URL") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = videoUrl, onValueChange = { videoUrl = it }, label = { Text("Video URL") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
        AdminKashrutDropdown(kashrutOptions = kashrutOptions, selected = selectedKashrut, onSelected = { selectedKashrut = it })
        Spacer(Modifier.height(12.dp))
        AdminCuisineDropdown(cuisineOptions = cuisineOptions, selected = selectedCuisine, onSelected = { selectedCuisine = it })
        Spacer(Modifier.height(16.dp))

        if (imageUrl.isNotBlank()) {
            GlideImage(model = imageUrl, contentDescription = null, modifier = Modifier.size(150.dp).padding(4.dp))
        }

        Spacer(Modifier.weight(1f))

        Row(Modifier.fillMaxWidth()) {
            Button(
                onClick = {
                    if (title.isBlank() || address.isBlank()) {
                        Toast.makeText(context, "שם וכתובת הם שדות חובה", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (itemId != null) {
                        val updatedItem = CatalogItem(id = itemId, title = title, description = description, kashrut = selectedKashrut, cuisine = selectedCuisine, address = address, imageUrl = imageUrl, videoUrl = videoUrl)
                        CatalogRepository.updateItem(context, updatedItem)
                    } else {
                        val newItem = CatalogItem(title = title, description = description, kashrut = selectedKashrut, cuisine = selectedCuisine, address = address, imageUrl = imageUrl, videoUrl = videoUrl)
                        CatalogRepository.addItem(context, newItem)
                    }

                    onSaveComplete()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("שמור פריט")
            }

            if (itemId != null) {
                Spacer(Modifier.width(16.dp))
                Button(
                    onClick = {
                        CatalogRepository.removeItemById(context, itemId)
                        onSaveComplete()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("מחק פריט")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminKashrutDropdown(kashrutOptions: List<String>, selected: String, onSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(value = selected, onValueChange = {}, readOnly = true, label = { Text("כשרות") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }, modifier = Modifier.menuAnchor().fillMaxWidth())
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            kashrutOptions.forEach { option ->
                DropdownMenuItem(text = { Text(option) }, onClick = { onSelected(option); expanded = false })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCuisineDropdown(cuisineOptions: List<String>, selected: String, onSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(value = selected, onValueChange = {}, readOnly = true, label = { Text("סוג מטבח") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }, modifier = Modifier.menuAnchor().fillMaxWidth())
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            cuisineOptions.forEach { option ->
                DropdownMenuItem(text = { Text(option) }, onClick = { onSelected(option); expanded = false })
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdminScreenPreview() {
    AdminScreen(itemId = "some-id", onSaveComplete = {})
}
