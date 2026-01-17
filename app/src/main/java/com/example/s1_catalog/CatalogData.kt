package com.example.s1_catalog

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.UUID

data class CatalogItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val kashrut: String,
    val cuisine: String,
    val address: String,
    val imageUrl: String,
    val videoUrl: String
)

object CatalogRepository {
    private val items = mutableStateListOf<CatalogItem>()
    private const val PREFS_NAME = "CatalogPrefs"
    private const val ITEMS_KEY = "catalogItems"
    private val gson = Gson()

    fun init(context: Context) {
        loadItems(context)
    }

    private fun loadItems(context: Context) {
        if (items.isEmpty()) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val json = prefs.getString(ITEMS_KEY, null)

            val type = object : TypeToken<List<CatalogItem>>() {}.type
            val loadedItems: List<CatalogItem>? = gson.fromJson(json, type)

            if (loadedItems != null && loadedItems.isNotEmpty()) {
                items.addAll(loadedItems)
            } else {
                items.addAll(getSampleItems())
            }
        }
    }

    private fun saveItems(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = gson.toJson(items)
        prefs.edit().putString(ITEMS_KEY, json).apply()
    }

    fun getItems(): List<CatalogItem> {
        return items
    }

    fun addItem(context: Context, item: CatalogItem) {
        items.add(0, item)
        saveItems(context)
    }

    fun getItemById(id: String): CatalogItem? {
        return items.find { it.id == id }
    }

    fun updateItem(context: Context, updatedItem: CatalogItem) {
        val index = items.indexOfFirst { it.id == updatedItem.id }
        if (index != -1) {
            items[index] = updatedItem
            saveItems(context)
        }
    }

    fun removeItemById(context: Context, id: String) {
        val index = items.indexOfFirst { it.id == id }
        if (index != -1) {
            items.removeAt(index)
            saveItems(context)
        }
    }

    private fun getSampleItems(): List<CatalogItem> {
        val placeholderVideo = "https://www.youtube.com/watch?v=dQw4w9WgXcQ"

        return listOf(
            CatalogItem(title="BBB", description="המבורגרים וכל מה שטוב ליד. בשרים איכותיים.", kashrut="כשר", cuisine="בשרים", address="הארבעה 28, תל אביב", imageUrl="https://media.rest.co.il/Images/Interior/thumb_637848231252994968.jpg", videoUrl=placeholderVideo),
            CatalogItem(title="קפה נמרוד", description="ארוחות בוקר מפנקות ונוף לנמל.", kashrut="מהדרין", cuisine="חלבי", address="האנגר 1, נמל תל אביב", imageUrl="https://media.rest.co.il/Images/Interior/thumb_637848243300089255.jpg", videoUrl=placeholderVideo),
            CatalogItem(title="ג'ירף", description="אוכל אסייתי מגוון, נודלס וסושי.", kashrut="ללא תעודה", cuisine="אסייתי", address="אבן גבירול 49, תל אביב", imageUrl="https://media.rest.co.il/Images/Interior/thumb_637848218670868195.jpg", videoUrl=placeholderVideo),
            CatalogItem(title="M25", description="בשרים איכותיים היישר מהקצב ליד שוק הכרמל.", kashrut="כשר", cuisine="בשרים", address="שוק הכרמל 30, תל אביב", imageUrl="https://media.rest.co.il/Images/Interior/thumb_637848234858972848.jpg", videoUrl=placeholderVideo),
            CatalogItem(title="La Lasagneria", description="לזניות אמיתיות בעבודת יד, ממש כמו באיטליה.", kashrut="מהדרין", cuisine="איטלקי", address="דיזנגוף 187, תל אביב", imageUrl="https://media.rest.co.il/Images/Interior/thumb_637848227096645396.jpg", videoUrl=placeholderVideo),
            CatalogItem(title="Taizu", description="פיוז'ן אסייתי יוקרתי עם מנות מחמישה מטבחים.", kashrut="ללא תעודה", cuisine="אסייתי", address="מנחם בגין 23, תל אביב", imageUrl="https://media.rest.co.il/Images/Interior/thumb_637848253459146059.jpg", videoUrl=placeholderVideo),
            CatalogItem(title="Pankina", description="מסעדה איטלקית חלבית עם אווירה ביתית.", kashrut="כשר", cuisine="איטלקי", address="גורדון 39, תל אביב", imageUrl="https://media.rest.co.il/Images/Interior/thumb_637848240590488273.jpg", videoUrl=placeholderVideo),
            CatalogItem(title="ויטרינה", description="ההמבורגר והנקניקיות המפורסמים ביותר בעיר.", kashrut="ללא תעודה", cuisine="בשרים", address="אבן גבירול 54, תל אביב", imageUrl="https://media.rest.co.il/Images/Interior/thumb_637848256333903126.jpg", videoUrl=placeholderVideo),
            CatalogItem(title="Machneyuda", description="מסעדת שוק סואנת ותוססת בלב ירושלים.", kashrut="כשר", cuisine="בשרים", address="בית יעקב 10, ירושלים", imageUrl="https://media.rest.co.il/Images/Interior/thumb_637848233301077759.jpg", videoUrl=placeholderVideo),
            CatalogItem(title="TYO", description="סושי בר יוקרתי עם קוקטיילים מיוחדים.", kashrut="כשר", cuisine="יפני", address="מונטיפיורי 7, תל אביב", imageUrl="https://media.rest.co.il/Images/Interior/thumb_637848254558597370.jpg", videoUrl=placeholderVideo)
        )
    }
}
