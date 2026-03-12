package com.example.s1_catalog.controller

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text

import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.s1_catalog.model.CatalogItem
import com.example.s1_catalog.model.CatalogRepository
import com.example.s1_catalog.model.UserRepository

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
    var rating by remember { mutableIntStateOf(0) }
    var originalUpdaterId by remember { mutableStateOf("") }

    val context = LocalContext.current
    val currentUser = UserRepository.currentUser

    // Logique de permission : vrai si création d'un nouveau, ou si admin, ou si auteur original
    val canEdit = remember(itemId, originalUpdaterId, currentUser) {
        itemId == null || currentUser.role == "admin" || currentUser.id == originalUpdaterId
    }

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
                rating = item.rating
                originalUpdaterId = item.id_updater
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
            text = when {
                itemId == null -> "Add New Restaurant"
                canEdit -> "Edit Restaurant"
                else -> "Restaurant Details (Read Only)"
            },
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = title, 
            onValueChange = { title = it }, 
            label = { Text("שם של המסעדה") }, 
            singleLine = true, 
            enabled = canEdit,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = description, 
            onValueChange = { description = it }, 
            label = { Text("תיאור") }, 
            enabled = canEdit,
            modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 100.dp)
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = address, 
            onValueChange = { address = it }, 
            label = { Text("כתובת") }, 
            singleLine = true, 
            enabled = canEdit,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = imageUrl, 
            onValueChange = { imageUrl = it }, 
            label = { Text("Image URL") }, 
            singleLine = true, 
            enabled = canEdit,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = videoUrl, 
            onValueChange = { videoUrl = it }, 
            label = { Text("Video URL") }, 
            singleLine = true, 
            enabled = canEdit,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))
        AdminKashrutDropdown(
            kashrutOptions = kashrutOptions, 
            selected = selectedKashrut, 
            onSelected = { selectedKashrut = it },
            enabled = canEdit
        )
        Spacer(Modifier.height(12.dp))
        AdminCuisineDropdown(
            cuisineOptions = cuisineOptions, 
            selected = selectedCuisine, 
            onSelected = { selectedCuisine = it },
            enabled = canEdit
        )
        
        Spacer(Modifier.height(16.dp))
        
        Text("דירוג המסעדה:", style = MaterialTheme.typography.titleMedium)
        RatingPicker(rating = rating, onRatingSelected = { if (canEdit) rating = it })

        Spacer(Modifier.height(16.dp))

        if (imageUrl.isNotBlank()) {
            GlideImage(model = imageUrl, contentDescription = null, modifier = Modifier.size(150.dp).padding(4.dp))
        }

        Spacer(Modifier.height(24.dp))

        if (canEdit) {
            Row(Modifier.fillMaxWidth()) {
                Button(
                    onClick = {
                        if (title.isBlank() || address.isBlank()) {
                            Toast.makeText(context, "שם וכתובת הם שדות חובה", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        val currentUserId = UserRepository.currentUser.id

                        if (itemId != null) {
                            val updatedItem = CatalogItem(
                                id = itemId,
                                title = title,
                                description = description,
                                kashrut = selectedKashrut,
                                cuisine = selectedCuisine,
                                address = address,
                                imageUrl = imageUrl,
                                videoUrl = videoUrl,
                                rating = rating,
                                id_updater = originalUpdaterId // On garde l'id original ou on met à jour si on veut
                            )
                            CatalogRepository.updateItem(context, updatedItem)
                        } else {
                            val newItem = CatalogItem(
                                title = title,
                                description = description,
                                kashrut = selectedKashrut,
                                cuisine = selectedCuisine,
                                address = address,
                                imageUrl = imageUrl,
                                videoUrl = videoUrl,
                                rating = rating,
                                id_updater = currentUserId
                            )
                            CatalogRepository.addItem(newItem)
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
        } else {
            Button(onClick = onSaveComplete, modifier = Modifier.fillMaxWidth()) {
                Text("Back")
            }
        }
    }
}

@Composable
fun RatingPicker(rating: Int, onRatingSelected: (Int) -> Unit) {
    Row {
        repeat(5) { index ->
            val starIndex = index + 1
            Icon(
                imageVector = if (starIndex <= rating) Icons.Default.Star else Icons.Outlined.StarOutline,
                contentDescription = null,
                tint = if (starIndex <= rating) Color(0xFFFFD700) else Color.Gray,
                modifier = Modifier
                    .size(40.dp)
                    .clickable { onRatingSelected(starIndex) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminKashrutDropdown(kashrutOptions: List<String>, selected: String, onSelected: (String) -> Unit, enabled: Boolean = true) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded && enabled, 
        onExpandedChange = { if (enabled) expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selected, 
            onValueChange = {}, 
            readOnly = true, 
            label = { Text("כשרות") }, 
            enabled = enabled,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }, 
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded && enabled, onDismissRequest = { expanded = false }) {
            kashrutOptions.forEach { option ->
                DropdownMenuItem(text = { Text(option) }, onClick = { onSelected(option); expanded = false })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCuisineDropdown(cuisineOptions: List<String>, selected: String, onSelected: (String) -> Unit, enabled: Boolean = true) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded && enabled, 
        onExpandedChange = { if (enabled) expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selected, 
            onValueChange = {}, 
            readOnly = true, 
            label = { Text("סוג מטבח") }, 
            enabled = enabled,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }, 
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded && enabled, onDismissRequest = { expanded = false }) {
            cuisineOptions.forEach { option ->
                DropdownMenuItem(text = { Text(option) }, onClick = { onSelected(option); expanded = false })
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdminScreenPreview() {
    AdminScreen(itemId = null, onSaveComplete = {})
}
