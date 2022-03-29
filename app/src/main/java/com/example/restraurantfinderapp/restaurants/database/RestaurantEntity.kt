package com.example.restraurantfinderapp.restaurants.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "restaurants")
data class RestaurantEntity(
    @ColumnInfo(name = "reference")
    var reference: String,

    @ColumnInfo(name = "favorite")
    var favorite: Boolean
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}