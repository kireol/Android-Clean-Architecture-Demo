package com.example.restraurantfinderapp.restaurants.mvvm.models

data class Restaurant(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    var rating: Double,
    var ratingsTotal: Double,
    var reference: String,
    var isFavorite: Boolean = false
)