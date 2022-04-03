package com.example.restraurantfinderapp.restaurants.mvvm.models.itemviewmodels

import androidx.databinding.BaseObservable
import com.example.restraurantfinderapp.R

class RestaurantItemViewModel(
    val name: String,
    val rating: String,
    var favorite: Boolean
) : BaseObservable(), ItemViewModel {

    override val layoutId: Int = R.layout.item_restaurant

}