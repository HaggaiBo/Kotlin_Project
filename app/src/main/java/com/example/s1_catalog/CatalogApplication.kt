package com.example.s1_catalog

import android.app.Application
import com.example.s1_catalog.model.CatalogRepository
import com.example.s1_catalog.model.UserRepository

class CatalogApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize repositories
        CatalogRepository.init(this)
        UserRepository.init(this)
    }
}
