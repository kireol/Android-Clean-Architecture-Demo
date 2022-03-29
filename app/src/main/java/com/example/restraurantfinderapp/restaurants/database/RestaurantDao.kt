package com.example.restraurantfinderapp.restaurants.database

import androidx.room.*

@Dao
interface RestaurantDao {
    @Query("SELECT * FROM restaurants")
    fun getAll(): List<RestaurantEntity>

    @Query("SELECT * FROM restaurants WHERE reference = :reference LIMIT 1")
    fun findByReference(reference: String): RestaurantEntity?

    @Insert
    fun insertAll(vararg restaurants: RestaurantEntity)

    @Update
    fun update(restaurant: RestaurantEntity)

    @Delete
    fun delete(restaurantEntity: RestaurantEntity)
}
