package com.example.restraurantfinderapp.restaurants.mvvm.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.restraurantfinderapp.R
import com.example.restraurantfinderapp.RestaurantFinderApplication
import com.example.restraurantfinderapp.restaurants.database.AppDatabaseHolder
import com.example.restraurantfinderapp.restaurants.database.RestaurantEntity
import com.example.restraurantfinderapp.restaurants.mvvm.models.Restaurant
import com.example.restraurantfinderapp.restaurants.mvvm.models.RestaurantHolder
import com.example.restraurantfinderapp.restaurants.mvvm.repository.RestaurantRepository
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
    val restaurantHolder: RestaurantHolder
) : ViewModel() {
    val data: MutableLiveData<List<ItemViewModel>?>
        get() = _data
    private val _data = MutableLiveData<List<ItemViewModel>?>(emptyList())


    init {
        setDataCollection()
    }

    fun fetchRestaurants(keyword: String = RestaurantFinderApplication.instance.getString(R.string.default_keyword)) {
        restaurantRepository.requestRestaurants(keyword)
    }

    private fun setDataCollection() {
        viewModelScope.launch {
            restaurantRepository.getResults().collect { it ->
                restaurantHolder.restaurants.value?.addAll(it.value as Collection<Restaurant>)
                val restaurantsByFavorite = it.value?.groupBy {
                    it.isFavorite
                }

                val viewData = restaurantsByFavorite?.let { restaurants ->
                    createViewData(restaurants)
                }
                viewData?.let {
                    _data.postValue(viewData)
                }
            }
        }
    }

    private fun createViewData(restaurantsByFavorite: Map<Boolean, List<Restaurant>>): List<ItemViewModel> {
        val viewData = mutableListOf<ItemViewModel>()
        restaurantsByFavorite.keys.forEach {
            val restaurants = restaurantsByFavorite[it]
            restaurants?.forEach { restaurant: Restaurant ->
                restaurant.let {
                    val item = Restaurant(
                        restaurant.name,
                        restaurant.rating
                    )
                    viewData.add(item)
                }
            }
        }

        return viewData
    }

    fun updateFavorite(position: Int) {
        restaurantHolder.restaurants.value?.get(position)?.let {
            it.isFavorite = !it.isFavorite
            val restaurant = _data.value?.get(position) as Restaurant
            restaurant.isFavorite = it.isFavorite
        }
        val restaurantDao = appDatabaseHolder.restaurantDb.restaurantDao()
        restaurantHolder.restaurants.value?.let {
            val restaurant = it[position]
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