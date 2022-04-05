package com.example.restaurantfinder.restaurants.mvvm.models

import androidx.lifecycle.MutableLiveData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ErrorMessage @Inject constructor(){
    var errorMessage: MutableLiveData<String> = MutableLiveData()
}