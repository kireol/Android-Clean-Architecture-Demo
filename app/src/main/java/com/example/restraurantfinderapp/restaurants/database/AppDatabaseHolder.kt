package com.example.restraurantfinderapp.restaurants.database

import androidx.room.Room
import com.example.restraurantfinderapp.RestaurantFinderApplication
import javax.inject.Inject

class AppDatabaseHolder @Inject constructor() {
    val restaurantDb = Room.databaseBuilder(
        RestaurantFinderApplication.instance,
        AppDatabase::class.java, "database-name"
    ).build()
}