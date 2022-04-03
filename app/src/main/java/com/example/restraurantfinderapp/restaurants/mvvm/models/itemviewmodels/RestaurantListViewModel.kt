package com.example.restraurantfinderapp.restaurants.mvvm.models.itemviewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.restraurantfinderapp.R
import com.example.restraurantfinderapp.RestaurantFinderApplication
import com.example.restraurantfinderapp.restaurants.database.AppDatabaseHolder
import com.example.restraurantfinderapp.restaurants.database.RestaurantEntity
import com.example.restraurantfinderapp.restaurants.mvvm.models.Restaurant
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
    private val appDatabaseHolder: AppDatabaseHolder
) : ViewModel() {
    val data: LiveData<List<ItemViewModel>>
        get() = _data
    private val _data = MutableLiveData<List<ItemViewModel>>(emptyList())

    var restaurants: MutableLiveData<ArrayList<Restaurant>> = MutableLiveData()
    init {
        setDataCollection()
    }

    fun fetchRestaurants(keyword: String = RestaurantFinderApplication.instance.getString(R.string.default_keyword)) {
        restaurantRepository.requestRestaurants(keyword)
    }

    private fun setDataCollection() {
        viewModelScope.launch {
            restaurantRepository.getResults().collect { it ->
                restaurants.postValue(it)
                val restaurantsByFavorite = it.groupBy {
                    it.isFavorite
                }

                val viewData = createViewData(restaurantsByFavorite)
                _data.postValue(viewData)
            }
        }
    }

    private fun createViewData(restaurantsByFavorite: Map<Boolean, List<Restaurant>>): List<ItemViewModel> {
        val viewData = mutableListOf<ItemViewModel>()
        restaurantsByFavorite.keys.forEach {
            val restaurants = restaurantsByFavorite[it]
            restaurants?.forEach { restaurant: Restaurant ->
                restaurant.let {
                    val item = RestaurantItemViewModel(
                        restaurant.name,
                        restaurant.rating.toString(),
                        restaurant.isFavorite
                    )
                    viewData.add(item)
                }
            }
        }

        return viewData
    }

    fun favoriteClicked(position: Int) {
        restaurants.value?.get(position)?.let {
            it.isFavorite = !it.isFavorite
            var restaurant = _data.value?.get(position) as RestaurantItemViewModel
            restaurant.favorite = it.isFavorite
        }
        val restaurantDao = appDatabaseHolder.restaurantDb.restaurantDao()
         restaurants.value?.let {
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