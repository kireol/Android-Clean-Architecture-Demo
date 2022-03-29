package com.example.restraurantfinderapp.restaurants.mvvm.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.restraurantfinderapp.R
import com.example.restraurantfinderapp.RestaurantFinderApplication
import com.example.restraurantfinderapp.restaurants.mvvm.models.Restaurant
import com.example.restraurantfinderapp.restaurants.mvvm.repository.RestaurantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RestaurantsViewModel @Inject constructor(
    private val restaurantRepository: RestaurantRepository
) : ViewModel() {
    var restaurants: MutableLiveData<ArrayList<Restaurant>> = MutableLiveData()

    fun fetchRestaurants(keyword: String = RestaurantFinderApplication.instance.getString(R.string.default_keyword)) {
        restaurantRepository.getAllRestaurants(keyword)
        viewModelScope.launch {
            restaurantRepository.getResults().collect {
                restaurants.postValue(it)
            }
        }
    }
}