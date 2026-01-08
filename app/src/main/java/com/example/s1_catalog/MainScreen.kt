package com.example.s1_catalog

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight

@Composable
fun MainScreen() {
    val context = LocalContext.current
    val allItems = remember { sampleItems() }

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("הכל") }

    val categories = listOf("הכל", "Action", "Comedy", "Drama", "Sci-Fi")

    val filteredItems = remember(searchQuery, selectedCategory) {
        allItems.filter { item ->
            val matchSearch =
                searchQuery.isBlank() || item.title.contains(searchQuery.trim(), ignoreCase = true)

            val matchCategory =
                (selectedCategory == "הכל") || (item.category == selectedCategory)

            matchSearch && matchCategory
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        Text(
            text = "CineBox",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 52.dp),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.ExtraBold,
            )

        Spacer(Modifier.height(12.dp))

        // ===== Search =====
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("חיפוש לפי שם סרט") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        // ===== Category Spinner (Compose dropdown) =====
        CategoryDropdown(
            categories = categories,
            selected = selectedCategory,
            onSelected = { selectedCategory = it }
        )

        Spacer(Modifier.height(12.dp))

        // ===== List =====
        LazyColumn {
            items(filteredItems) { item ->
                MovieCard(
                    item = item,
                    onPlayClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.videoUrl))
                        context.startActivity(intent)
                    }
                )
            }
        }
    }
}

@Composable
private fun MovieCard(
    item: CatalogItem,
    onPlayClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(item.imageRes),
                contentDescription = item.title,
                modifier = Modifier.size(70.dp)
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(item.title, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(2.dp))
                Text(item.description, maxLines = 2)
                Spacer(Modifier.height(4.dp))
                Text(
                    text = item.category,
                    style = MaterialTheme.typography.labelMedium
                )
            }

            Spacer(Modifier.width(8.dp))

            Button(onClick = onPlayClick) {
                Text("Play")
            }
        }
    }
}

@Composable
fun CategoryDropdown(
    categories: List<String>,
    selected: String,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = true }
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            enabled = false, // חשוב: מונע מהטקסטפילד "לבלוע" קליקים
            label = { Text("קטגוריה") },
            modifier = Modifier.fillMaxWidth()
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            categories.forEach { option ->
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



