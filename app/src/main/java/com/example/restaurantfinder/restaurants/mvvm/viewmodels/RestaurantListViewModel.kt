package com.example.restaurantfinder.restaurants.mvvm.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.restaurantfinder.R
import com.example.restaurantfinder.RestaurantFinderApplication
import com.example.restaurantfinder.restaurants.database.AppDatabaseHolder
import com.example.restaurantfinder.restaurants.database.RestaurantEntity
import com.example.restaurantfinder.restaurants.mvvm.models.ErrorMessage
import com.example.restaurantfinder.restaurants.mvvm.models.Restaurant
import com.example.restaurantfinder.restaurants.mvvm.models.RestaurantHolder
import com.example.restaurantfinder.restaurants.mvvm.repository.RestaurantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RestaurantListViewModel @Inject constructor(
    private val restaurantRepository: RestaurantRepository,
    private val appDatabaseHolder: AppDatabaseHolder,
    private val restaurantHolder: RestaurantHolder,
    private val errorMessage: ErrorMessage
) : ViewModel() {
    private val _data = MutableLiveData<List<ItemViewModel>?>(emptyList())

    init {
        setupDataCollection()
    }

    fun fetchRestaurants(keyword: String? ) {
            restaurantRepository.requestRestaurants(keyword?: RestaurantFinderApplication.instance.getString(R.string.default_keyword))
    }

    private fun setupDataCollection() {
        viewModelScope.launch {
            restaurantRepository.getError().collect { errorString ->
                errorMessage.errorMessage.postValue(errorString.value)
            }
        }

        viewModelScope.launch {
            restaurantRepository.getResults().collect { incomingRestaurants ->
                val needsAdded: ArrayList<Restaurant> = arrayListOf()
                incomingRestaurants.value?.let { restaurantList ->
                    for (restaurant in restaurantList) {
                        if (restaurantHolder.restaurants.value?.contains(restaurant) != true) {
                            needsAdded.add(restaurant)
                        }
                    }
                    needsAdded.addAll(restaurantHolder.restaurants.value as Collection<Restaurant>)
                    restaurantHolder.restaurants.postValue(needsAdded)
                }
                if (needsAdded.size == 0) {
                    restaurantHolder.restaurants.forceRefresh()
                }

                val viewData = incomingRestaurants.value?.let { restaurants ->
                    createViewData(restaurants)
                }
                viewData?.let {
                    _data.postValue(viewData)
                }
            }
        }
    }

    private fun <T> MutableLiveData<T>.forceRefresh() {
        this.value = this.value
    }

    private fun createViewData(restaurantsByFavorite: ArrayList<Restaurant>): List<ItemViewModel> {
        val viewData = mutableListOf<ItemViewModel>()
        restaurantsByFavorite.forEach { restaurant: Restaurant ->
            restaurant.let {
                val item = Restaurant(
                    restaurant.name,
                    restaurant.rating,
                    restaurant.isFavorite
                )
                viewData.add(item)
            }
        }

        return viewData
    }

    fun updateFavorite(position: Int) {
        updateDb(position)
    }

    private fun updateDb(position: Int) {
        restaurantHolder.restaurants.value?.get(position)?.let {
            it.isFavorite = !it.isFavorite
            val restaurant = _data.value?.get(position) as Restaurant
            restaurant.isFavorite = it.isFavorite
            val restaurantDao = appDatabaseHolder.restaurantDb.restaurantDao()
            CoroutineScope(Dispatchers.IO).launch {
                val restaurantDBObj: RestaurantEntity? =
                    restaurantDao.findByReference(restaurant.reference)
                if (restaurantDBObj != null) {
                    if (!restaurant.isFavorite) {
                        restaurantDao.delete(restaurantDBObj)
                    }
                } else {
                    restaurantDao.insertAll(
                        RestaurantEntity(restaurant.reference, restaurant.isFavorite)
                    )
                }
            }
        }
    }
}