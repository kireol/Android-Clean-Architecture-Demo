package com.example.restaurantfinder

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class RestaurantFinderApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: RestaurantFinderApplication
            private set
    }
}