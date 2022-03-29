package com.example.restraurantfinderapp.restaurants.api

import android.content.Context
import com.example.restraurantfinderapp.R
import com.example.restraurantfinderapp.RestaurantFinderApplication
import com.example.restraurantfinderapp.restaurants.api.geogleplaces.NearbyRestaurantSearchResp
import com.example.restraurantfinderapp.restaurants.mvvm.models.GPSLocation
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

class RestaurantUseCase @Inject constructor(private val gpsLocation: GPSLocation) {
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
            "<YOUR GOOGLE API KEY>",
            "1500"
        )
        call?.enqueue(callback)
    }
}


