package com.example.restraurantfinderapp.restaurants.mvvm.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.restraurantfinderapp.restaurants.api.RestaurantUseCase
import com.example.restraurantfinderapp.restaurants.api.geogleplaces.ApiKeys
import com.example.restraurantfinderapp.restaurants.api.geogleplaces.Business
import com.example.restraurantfinderapp.restaurants.api.geogleplaces.NearbyRestaurantSearchResp
import com.example.restraurantfinderapp.restaurants.database.AppDatabaseHolder
import com.example.restraurantfinderapp.restaurants.database.RestaurantEntity
import com.example.restraurantfinderapp.restaurants.mvvm.models.Restaurant
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class RestaurantRepository @Inject constructor(
    private val restaurantUseCase: RestaurantUseCase,
    private val appDatabaseHolder: AppDatabaseHolder
) {
    private var restaurants: MutableLiveData<ArrayList<Restaurant>> = MutableLiveData(arrayListOf())
    private val restaurantList = MutableSharedFlow<MutableLiveData<ArrayList<Restaurant>>>()

    fun requestRestaurants(
        keyword: String
    ) {
        restaurantUseCase.fetchAllRestaurantsFromAPI(keyword,
            object : Callback<NearbyRestaurantSearchResp?> {
                override fun onResponse(
                    call: Call<NearbyRestaurantSearchResp?>,
                    response: Response<NearbyRestaurantSearchResp?>
                ) {
                    if (response.isSuccessful) {
                        processSuccessfulResults(response)
                    } else {
                        Log.e("Retrofit", response.code().toString())
                    }
                }

                override fun onFailure(call: Call<NearbyRestaurantSearchResp?>, t: Throwable) {
                    Log.e("Retrofit", "Error getting google places api data")
                }
            }
        )
    }

    fun getResults(): MutableSharedFlow<MutableLiveData<ArrayList<Restaurant>>> {
        return restaurantList
    }

    private fun processSuccessfulResults(
        response: Response<NearbyRestaurantSearchResp?>
    ) {
        val gson = GsonBuilder().setPrettyPrinting().create()

        runBlocking {
            val restaurantJson = gson.toJson(response.body())
            val nearbyRestaurantSearchResp =
                gson.fromJson(
                    restaurantJson,
                    NearbyRestaurantSearchResp::class.java
                )
            CoroutineScope(Dispatchers.IO).launch {
                getRestaurantsFromJsonObject(nearbyRestaurantSearchResp)
                restaurantList.emit(restaurants)
            }
        }
    }

    private fun getRestaurantsFromJsonObject(
        nearbyRestaurantSearchResp: NearbyRestaurantSearchResp
    ) {
        val apiKeys = ApiKeys()
        apiKeys.pageToken = nearbyRestaurantSearchResp.next_page_token.toString()
        restaurants = MutableLiveData(arrayListOf())

        nearbyRestaurantSearchResp.results?.let { restaurantsFromResponse ->
            for (restaurantJsonObject in restaurantsFromResponse) {
                var isFavorite = false
                appDatabaseHolder.restaurantDb
                val restaurantDBObj: RestaurantEntity? =
                    appDatabaseHolder.restaurantDb.restaurantDao()
                        .findByReference(restaurantJsonObject.reference)
                restaurantDBObj?.let {
                    isFavorite = restaurantDBObj.favorite
                }
                restaurants.value?.add(jsonToRestaurant(restaurantJsonObject, isFavorite))
            }
        }
        restaurants.value?.let { restaurantList ->
            restaurantList.sortWith(compareBy<Restaurant> { it.isFavorite }.thenBy { it.rating })
            restaurantList.reverse()
        }
    }

    private fun jsonToRestaurant(
        restaurantJsonObject: Business,
        isFavorite: Boolean
    ) = Restaurant(
        restaurantJsonObject.name,
        restaurantJsonObject.rating,
        isFavorite,
        restaurantJsonObject.geometry.location.lat,
        restaurantJsonObject.geometry.location.lng,
        restaurantJsonObject.user_ratings_total,
        restaurantJsonObject.reference
    )
}