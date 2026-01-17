package com.example.s1_catalog

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text

// 1. Importation de GlideImage
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
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

@OptIn(ExperimentalGlideComposeApi::class) // 2. Annotation nécessaire pour GlideImage
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
            // 3. Remplacement du composant Image par GlideImage
            GlideImage(
                model = item.imageUrl,
                contentDescription = item.title,
                modifier = Modifier.size(70.dp)
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(item.title, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(2.dp))
                Text(item.description, maxLines = 2)
                Spacer(Modifier.height(4.dp))
                Row {
                    Text(
                        text = item.kashrut,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = item.cuisine,
                        style = MaterialTheme.typography.labelMedium
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
