package com.example.restaurantfinder.restaurants.mvvm.models

import androidx.lifecycle.MutableLiveData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RestaurantHolder @Inject constructor() {
    var restaurants: MutableLiveData<ArrayList<Restaurant>> = MutableLiveData(arrayListOf())
}