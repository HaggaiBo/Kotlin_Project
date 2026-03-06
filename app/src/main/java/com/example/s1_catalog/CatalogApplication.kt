package com.example.s1_catalog

import android.app.Application

class CatalogApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialiser les référentiels avec le contexte de l'application
        CatalogRepository.init(this)
        UserProfileRepository.init(this)
    }
}
