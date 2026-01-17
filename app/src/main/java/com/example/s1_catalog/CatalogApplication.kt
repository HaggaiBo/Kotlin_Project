package com.example.s1_catalog

import android.app.Application

class CatalogApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialiser le référentiel avec le contexte de l'application
        CatalogRepository.init(this)
    }
}
