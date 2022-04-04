package com.example.restaurantfinder.restaurants.api.geogleplaces

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PlacesService {
    @GET("/maps/api/place/nearbysearch/json?")
    fun getRestaurants(
        @Query("location") location: String?,
        @Query("keyword") keyword: String?,
        @Query("type") type: String?,
        @Query("key") key: String?,
        @Query("radius") radius: String?,
        @Query("pagetoken") pagetoken: String?
    ): Call<NearbyRestaurantSearchResp?>?

}