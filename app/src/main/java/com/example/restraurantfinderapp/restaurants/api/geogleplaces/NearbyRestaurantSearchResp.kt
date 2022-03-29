package com.example.restraurantfinderapp.restaurants.api.geogleplaces

data class NearbyRestaurantSearchResp(
    val results: List<Business>?,
    val status: String?
)

data class Business(
    val geometry: BusinessGeometry,
    val name: String?,
    val opening_hours: BusinessHours,
    val place_id: String?,
    val rating: Double,
    val user_ratings_total: Double,
    val reference: String
)

data class BusinessGeometry(
    val location: RestaurantLocation,
)

data class RestaurantLocation(
    val lat: Double,
    val lng: Double
)

data class BusinessHours(
    val open_now: Boolean
)