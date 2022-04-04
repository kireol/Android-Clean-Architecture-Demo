package com.example.restaurantfinder.restaurants.database

import androidx.room.Room
import com.example.restaurantfinder.RestaurantFinderApplication
import javax.inject.Inject

class AppDatabaseHolder @Inject constructor() {
    val restaurantDb = Room.databaseBuilder(
        RestaurantFinderApplication.instance,
        AppDatabase::class.java, "database-name"
    ).build()
}