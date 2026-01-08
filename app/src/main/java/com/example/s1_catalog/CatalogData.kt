package com.example.s1_catalog

import androidx.annotation.DrawableRes

data class CatalogItem(
    val title: String,
    val description: String,
    val category: String,
    @DrawableRes val imageRes: Int,
    val videoUrl: String
)

fun sampleItems(): List<CatalogItem> {
    val img = R.drawable.ic_launcher_foreground

    return listOf(
        CatalogItem(
            "Mad Max: Fury Road",
            "Post-apocalyptic action packed chase.",
            "Action",
            R.drawable.mad_max_fury_road,
            "https://www.youtube.com/watch?v=hEJnMQG9ev8"
        ),
        CatalogItem(
            "The Dark Knight",
            "Batman faces the Joker in Gotham City.",
            "Action",
            R.drawable.the_dark_knight,
            "https://www.youtube.com/watch?v=_PZpmTj1Q8Q"
        ),
        CatalogItem(
            "John Wick",
            "A retired hitman seeks vengeance.",
            "Action",
            R.drawable.john_wick,
            "https://www.youtube.com/watch?v=C0BMx-qxsP4"
        ),
        CatalogItem(
            "Mission: Impossible – Fallout",
            "Ethan Hunt races against time.",
            "Action",
            R.drawable.mission_impossible,
            "https://www.youtube.com/watch?v=wb49-oV0F78"
        ),

        // ===== COMEDY (4) =====
        CatalogItem(
            "The Hangover",
            "A bachelor party gone terribly wrong.",
            "Comedy",
            R.drawable.the_hangover,
            "https://www.youtube.com/watch?v=tcdUhdOlz9M"
        ),
        CatalogItem(
            "Superbad",
            "Two friends try to enjoy their last days of high school.",
            "Comedy",
            R.drawable.superbad,
            "https://www.youtube.com/watch?v=4eaZ_48ZYog"
        ),
        CatalogItem(
            "The Mad Adventures of Rabbi Jacob",
            "A classic French comedy starring Louis de Funès, full of mistaken identities and chaos.",
            "Comedy",
            R.drawable.rabbi_jacob,
            "https://www.youtube.com/watch?v=6cZ0r0Zp6yI"
        ),
        CatalogItem(
            "The Mask",
            "A man discovers a magical mask.",
            "Comedy",
            R.drawable.the_mask,
            "https://www.youtube.com/watch?v=hOqVRwGVUkA"
        ),

        // ===== DRAMA (4) =====
        CatalogItem(
            "The Shawshank Redemption",
            "Hope and friendship inside a prison.",
            "Drama",
            R.drawable.the_shawshank_redemption,
            "https://www.youtube.com/watch?v=PLl99DlL6b4"
        ),
        CatalogItem(
            "Forrest Gump",
            "Life story of an extraordinary man.",
            "Drama",
            R.drawable.forrest_gump,
            "https://www.youtube.com/watch?v=bLvqoHBptjg"
        ),
        CatalogItem(
            "Whiplash",
            "A drummer pushed beyond his limits.",
            "Drama",
            R.drawable.whiplash,
            "https://www.youtube.com/watch?v=7d_jQycdQGo"
        ),
        CatalogItem(
            "The Pursuit of Happyness",
            "A father fights for a better future.",
            "Drama",
            R.drawable.the_pursuit_of_happyness,
            "https://www.youtube.com/watch?v=DMOBlEcRuw8"
        ),

        // ===== SCIENCE FICTION (3) =====
        CatalogItem(
            "Inception",
            "A thief steals information through dreams.",
            "Sci-Fi",
            R.drawable.inception,
            "https://www.youtube.com/watch?v=YoHD9XEInc0"
        ),
        CatalogItem(
            "Interstellar",
            "A journey beyond the stars to save humanity.",
            "Sci-Fi",
            R.drawable.interstellar,
            "https://www.youtube.com/watch?v=zSWdZVtXT7E"
        ),
        CatalogItem(
            "The Matrix",
            "A hacker discovers the truth about reality.",
            "Sci-Fi",
            R.drawable.the_matrix,
            "https://www.youtube.com/watch?v=vKQi3bBA1y8"
        )
    )
}
