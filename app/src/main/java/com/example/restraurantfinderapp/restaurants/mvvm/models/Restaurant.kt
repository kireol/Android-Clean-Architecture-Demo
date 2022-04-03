package com.example.restraurantfinderapp.restaurants.mvvm.models

import androidx.databinding.BaseObservable
import com.example.restraurantfinderapp.R
import com.example.restraurantfinderapp.restaurants.mvvm.viewmodels.ItemViewModel

data class Restaurant(
    val name: String,
    var rating: Double,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    var ratingsTotal: Double = 0.0,
    var reference: String = "",
    var isFavorite: Boolean = false,
) : BaseObservable(), ItemViewModel {
    override val layoutId: Int = R.layout.item_restaurant
}