package com.example.restraurantfinderapp.restaurants.mvvm.repository

import android.util.Log
import com.example.restraurantfinderapp.restaurants.api.RestaurantUseCase
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
    appDatabaseHolder: AppDatabaseHolder
) {
    private val restaurantDb = appDatabaseHolder.restaurantDb
    fun getAllRestaurants(
        keyword: String
    ) {
        restaurantUseCase.fetchAllRestaurantsFromAPI(keyword,
            object : Callback<NearbyRestaurantSearchResp?> {
                override fun onResponse(
                    call: Call<NearbyRestaurantSearchResp?>,
                    response: Response<NearbyRestaurantSearchResp?>
                ) {
                    val restaurants: ArrayList<Restaurant> = ArrayList()
                    if (response.isSuccessful) {
                        processSuccessfulResults(response, restaurants)
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

    private val restaurantList = MutableSharedFlow<ArrayList<Restaurant>>()

    fun getResults(): MutableSharedFlow<ArrayList<Restaurant>> {
        return restaurantList
    }

    private fun processSuccessfulResults(
        response: Response<NearbyRestaurantSearchResp?>,
        restaurants: ArrayList<Restaurant>
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
                getRestaurantsFromJsonObject(nearbyRestaurantSearchResp, restaurants)
                restaurantList.emit(restaurants)
            }

        }
    }

    private fun getRestaurantsFromJsonObject(
        nearbyRestaurantSearchResp: NearbyRestaurantSearchResp,
        restaurants: ArrayList<Restaurant>
    ): ArrayList<Restaurant> {
        for (restaurantJsonObject in nearbyRestaurantSearchResp.results!!) {
            var isFavorite = false
            val restaurantDBObj: RestaurantEntity? = restaurantDb.restaurantDao().
                findByReference(restaurantJsonObject.reference)
            restaurantDBObj?.let {
                isFavorite = restaurantDBObj.favorite
            }
            restaurants.add(jsonToRestaurant(restaurantJsonObject, isFavorite))
        }
        restaurants.sortWith(compareBy<Restaurant> { it.isFavorite }.thenBy { it.rating })
        restaurants.reverse()
        return restaurants
    }

    private fun jsonToRestaurant(
        restaurantJsonObject: Business,
        isFavorite: Boolean
    ) = Restaurant(
        restaurantJsonObject.name!!,
        restaurantJsonObject.geometry.location.lat,
        restaurantJsonObject.geometry.location.lng,
        restaurantJsonObject.rating,
        restaurantJsonObject.user_ratings_total,
        restaurantJsonObject.reference,
        isFavorite
    )
}