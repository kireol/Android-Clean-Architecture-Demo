package com.example.restraurantfinderapp.restaurants.mvvm.models

import android.location.Location
import androidx.lifecycle.MutableLiveData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GPSLocation @Inject constructor() {
    var location: MutableLiveData<Location> = MutableLiveData()

    fun getLocationFromGPSLocation(): Pair<Double, Double> {
        return Pair(location.value?.latitude ?: 0.0, location.value?.longitude ?: 0.0)
    }
}