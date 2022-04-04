package com.example.restaurantfinder.restaurants.api

import com.example.restaurantfinder.restaurants.api.geogleplaces.ApiKeys
import com.example.restaurantfinder.restaurants.api.geogleplaces.NearbyRestaurantSearchResp
import com.example.restaurantfinder.restaurants.api.geogleplaces.PlacesService
import com.example.restaurantfinder.restaurants.mvvm.models.GPSLocation
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

class RestaurantUseCase @Inject constructor(private val gpsLocation: GPSLocation, private val apiKeys: ApiKeys) {
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://maps.googleapis.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service: PlacesService = retrofit.create(PlacesService::class.java)

    fun fetchAllRestaurantsFromAPI(
        keyword: String,
        callback: Callback<NearbyRestaurantSearchResp?>
    ) {

        val location = gpsLocation.getLocationFromGPSLocation().first.toString() +
                "," + gpsLocation.getLocationFromGPSLocation().second.toString()
        val call = service.getRestaurants(
            location,
            keyword,
            "restaurant",
            apiKeys.googlePlacesKey.value,
            "1500",
            ApiKeys().pageToken
        )
        call?.enqueue(callback)
    }
}


