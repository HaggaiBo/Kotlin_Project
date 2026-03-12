package com.example.s1_catalog.view

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.PlayArrow
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

import androidx.compose.material3.*

import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.s1_catalog.R
import com.example.s1_catalog.controller.AdminActivity
import com.example.s1_catalog.controller.MapActivity
import com.example.s1_catalog.controller.ProfileActivity
import com.example.s1_catalog.model.CatalogItem
import com.example.s1_catalog.model.CatalogRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    
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
        val matchKashrut = (selectedKashrut == "הכל") || (item.kashrut == selectedKashrut)
        val matchCuisine = (selectedCuisine == "הכל") || (item.cuisine == selectedCuisine)
        matchSearch && matchKashrut && matchCuisine
    }

    Scaffold(
        modifier = Modifier.systemBarsPadding(),
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "Missada On Your Way", 
                        fontWeight = FontWeight.ExtraBold,
                        style = MaterialTheme.typography.titleLarge
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { context.startActivity(Intent(context, MapActivity::class.java)) }) {
                        Icon(Icons.Default.Map, contentDescription = "Map")
                    }
                },
                actions = {
                    IconButton(onClick = { context.startActivity(Intent(context, ProfileActivity::class.java)) }) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Profile")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 12.dp)
                .fillMaxSize()
        ) {
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

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
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
                            if (item.videoUrl.isNotBlank()) {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.videoUrl))
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Could not open video", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(context, "No video available for this restaurant", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
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
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Utilisation du nouveau nom de fichier placeholder
            val imageModel = if (item.imageUrl.isBlank()) R.drawable.misada_place_holder else item.imageUrl
            
            GlideImage(
                model = imageModel,
                contentDescription = item.title,
                modifier = Modifier.size(80.dp)
            ) {
                it.error(R.drawable.misada_place_holder).placeholder(R.drawable.misada_place_holder)
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(item.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, null, Modifier.size(14.dp), Color.Gray)
                    Text(item.address, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }

                Text(item.description, maxLines = 2, style = MaterialTheme.typography.bodySmall)
                
                RatingBar(rating = item.rating)
                
                Row {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.padding(top = 4.dp, end = 4.dp)
                    ) {
                        Text(item.kashrut, Modifier.padding(horizontal = 6.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall)
                    }
                    Surface(
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(item.cuisine, Modifier.padding(horizontal = 6.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            IconButton(onClick = onPlayClick) {
                Icon(
                    imageVector = Icons.Default.PlayArrow, 
                    contentDescription = "Watch video", 
                    tint = if (item.videoUrl.isNotBlank()) MaterialTheme.colorScheme.primary else Color.Gray
                )
            }
        }
    }
}

@Composable
fun RatingBar(rating: Int) {
    Row(modifier = Modifier.padding(vertical = 2.dp)) {
        repeat(5) { index ->
            Icon(
                imageVector = if (index < rating) Icons.Default.Star else Icons.Outlined.StarOutline,
                contentDescription = null,
                tint = if (index < rating) Color(0xFFFFD700) else Color.Gray,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
fun KashrutDropdown(kashrutOptions: List<String>, selected: String, onSelected: (String) -> Unit, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier.clickable { expanded = true }) {
        OutlinedTextField(value = selected, onValueChange = {}, readOnly = true, enabled = false, label = { Text("כשרות") }, modifier = Modifier.fillMaxWidth())
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.fillMaxWidth(0.45f)) {
            kashrutOptions.forEach { option ->
                DropdownMenuItem(text = { Text(option) }, onClick = { onSelected(option); expanded = false })
            }
        }
    }
}

@Composable
fun CuisineDropdown(cuisineOptions: List<String>, selected: String, onSelected: (String) -> Unit, modifier: Modifier = Modifier) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier.clickable { expanded = true }) {
        OutlinedTextField(value = selected, onValueChange = {}, readOnly = true, enabled = false, label = { Text("סוג מטבח") }, modifier = Modifier.fillMaxWidth())
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.fillMaxWidth(0.45f)) {
            cuisineOptions.forEach { option ->
                DropdownMenuItem(text = { Text(option) }, onClick = { onSelected(option); expanded = false })
            }
        }
    }
}
