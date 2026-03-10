package com.example.s1_catalog.model

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.util.UUID
import android.util.Log

data class CatalogItem(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val kashrut: String = "",
    val cuisine: String = "",
    val address: String = "",
    val imageUrl: String = "",
    val videoUrl: String = "",
    val rating: Int = 0
)

object CatalogRepository {
    private val db by lazy { Firebase.firestore }
    private const val COLLECTION_NAME = "Restaurants"
    
    val items = mutableStateListOf<CatalogItem>()

    fun init(context: Context) {
        listenToChanges()
        addSampleDataIfNeeded()
    }

    private fun listenToChanges() {
        db.collection(COLLECTION_NAME).addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.e("CatalogRepository", "Listen failed: ${e.message}", e)
                return@addSnapshotListener
            }
            
            if (snapshot != null) {
                val list = snapshot.toObjects(CatalogItem::class.java)
                items.clear()
                items.addAll(list)

                Log.d("CatalogRepository", "Items received from Firestore: ${list.size}")
            }
        }
    }

    fun getItems(): List<CatalogItem> = items

    fun getItemById(id: String): CatalogItem? = items.find { it.id == id }

    fun addItem(item: CatalogItem) {
        val finalItem =
            if (item.id.isEmpty())
                item.copy(id = UUID.randomUUID().toString())
            else item

        db.collection(COLLECTION_NAME)
            .document(finalItem.id)
            .set(finalItem)
            .addOnFailureListener {
                Log.e("CatalogRepository", "Error adding item", it)
            }
    }

    fun updateItem(context: Context, updatedItem: CatalogItem) {
        db.collection(COLLECTION_NAME).document(updatedItem.id)
            .set(updatedItem)
            .addOnFailureListener { Log.e("CatalogRepository", "Error updating item", it) }
    }

    fun removeItemById(context: Context, id: String) {
        db.collection(COLLECTION_NAME).document(id)
            .delete()
            .addOnFailureListener { Log.e("CatalogRepository", "Error deleting item", it) }
    }

    private fun addSampleDataIfNeeded() {
        db.collection(COLLECTION_NAME).limit(1).get().addOnSuccessListener { snapshot ->
            if (snapshot.isEmpty) {
                getSampleItems().forEach { addItem( it) }
            }
        }.addOnFailureListener {
            Log.e("CatalogRepository", "Error checking for sample data", it)
        }
    }

    private fun getSampleItems(): List<CatalogItem> {
        val placeholderVideo = "https://www.youtube.com/watch?v=dQw4w9WgXcQ"
        return listOf(
            CatalogItem(id = UUID.randomUUID().toString(), title="BBB", description="המבורגרים וכל מה שטוב ליד. בשרים איכותיים.", kashrut="כשר", cuisine="בשרים", address="הארבעה 28, תל אביב", imageUrl="https://media.rest.co.il/Images/Interior/thumb_637848231252994968.jpg", videoUrl=placeholderVideo, rating = 4),
            CatalogItem(id = UUID.randomUUID().toString(), title="קפה נמרוד", description="ארוחות בוקר מפנקות ונוף לנמל.", kashrut="מהדרין", cuisine="חלבי", address="האנגר 1, נמל תל אביב", imageUrl="https://media.rest.co.il/Images/Interior/thumb_637848243300089255.jpg", videoUrl=placeholderVideo, rating = 5),
            CatalogItem(id = UUID.randomUUID().toString(), title="ג'ירף", description="אוכל אסייתי מגוון, נודלס וסושי.", kashrut="ללא תעודה", cuisine="אסייתי", address="אבן גבירול 49, תל אביב", imageUrl="https://media.rest.co.il/Images/Interior/thumb_637848218670868195.jpg", videoUrl=placeholderVideo, rating = 3)
        )
    }
}
