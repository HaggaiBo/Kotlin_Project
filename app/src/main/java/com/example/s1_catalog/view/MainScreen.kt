package com.example.s1_catalog.view

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text

import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.s1_catalog.controller.AdminActivity
import com.example.s1_catalog.controller.ProfileActivity
import com.example.s1_catalog.model.CatalogItem
import com.example.s1_catalog.model.CatalogRepository

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    
    // Initialisation du référentiel si nécessaire
    LaunchedEffect(Unit) {
        CatalogRepository.init(context)
    }

    val allItems = CatalogRepository.getItems()

    var searchQuery by remember { mutableStateOf("") }
    var selectedKashrut by remember { mutableStateOf("הכל") }
    var selectedCuisine by remember { mutableStateOf("הכל") }

    val kashrutOptions = listOf("הכל", "כשר", "מהדרין", "בד\"ץ בית יוסף", "בד\"ץ העדה החרדית", "ללא תעודה")
    val cuisineOptions = listOf("הכל", "בשרים", "חלבי", "אסייתי", "טבעוני", "ארוחות בוקר", "דגים", "איטלקי", "יפני")

    val filteredItems = allItems.filter { item ->
        val matchSearch =
            searchQuery.isBlank() || item.title.contains(searchQuery.trim(), ignoreCase = true)

        val matchKashrut =
            (selectedKashrut == "הכל") || (item.kashrut == selectedKashrut)

        val matchCuisine =
            (selectedCuisine == "הכל") || (item.cuisine == selectedCuisine)

        matchSearch && matchKashrut && matchCuisine
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        // En-tête avec titre et avatar
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Missada On Your Way",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 52.dp),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.ExtraBold,
            )
            
            IconButton(
                onClick = { context.startActivity(Intent(context, ProfileActivity::class.java)) },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Profile",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("חיפוש לפי שם מסעדה") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        Row {
            KashrutDropdown(
                modifier = Modifier.weight(1f),
                kashrutOptions = kashrutOptions,
                selected = selectedKashrut,
                onSelected = { selectedKashrut = it }
            )
            Spacer(Modifier.width(8.dp))
            CuisineDropdown(
                modifier = Modifier.weight(1f),
                cuisineOptions = cuisineOptions,
                selected = selectedCuisine,
                onSelected = { selectedCuisine = it }
            )
        }

        Spacer(Modifier.height(12.dp))

        LazyColumn {
            items(filteredItems, key = { it.id }) { item ->
                RestaurantCard(
                    item = item,
                    onClick = {
                        val intent = Intent(context, AdminActivity::class.java).apply {
                            putExtra("ITEM_ID", item.id)
                        }
                        context.startActivity(intent)
                    },
                    onPlayClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.videoUrl))
                        context.startActivity(intent)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun RestaurantCard(
    item: CatalogItem,
    onClick: () -> Unit,
    onPlayClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GlideImage(
                model = item.imageUrl,
                contentDescription = item.title,
                modifier = Modifier.size(70.dp)
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(item.title, style = MaterialTheme.typography.titleMedium)
                
                Spacer(Modifier.height(2.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Color.Gray
                    )
                    Text(
                        text = item.address,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                Spacer(Modifier.height(2.dp))
                Text(item.description, maxLines = 2, style = MaterialTheme.typography.bodyMedium)
                
                Spacer(Modifier.height(4.dp))
                
                RatingBar(rating = item.rating)
                
                Spacer(Modifier.height(4.dp))
                Row {
                    Text(
                        text = "כשרות: ${item.kashrut}",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = "מטבח: ${item.cuisine}",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            Spacer(Modifier.width(8.dp))

            Button(onClick = onPlayClick) {
                Text("וידאו")
            }
        }
    }
}

@Composable
fun RatingBar(rating: Int) {
    Row {
        repeat(5) { index ->
            Icon(
                imageVector = if (index < rating) Icons.Default.Star else Icons.Outlined.StarOutline,
                contentDescription = null,
                tint = if (index < rating) Color(0xFFFFD700) else Color.Gray,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun KashrutDropdown(
    kashrutOptions: List<String>,
    selected: String,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable { expanded = true }
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            enabled = false,
            label = { Text("כשרות") },
            modifier = Modifier.fillMaxWidth()
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            kashrutOptions.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun CuisineDropdown(
    cuisineOptions: List<String>,
    selected: String,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable { expanded = true }
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            enabled = false,
            label = { Text("סוג מטבח") },
            modifier = Modifier.fillMaxWidth()
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            cuisineOptions.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
