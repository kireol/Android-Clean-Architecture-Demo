package com.example.restaurantfinder.restaurants.mvvm.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.restaurantfinder.restaurants.api.RestaurantUseCase
import com.example.restaurantfinder.restaurants.api.geogleplaces.ApiKeys
import com.example.restaurantfinder.restaurants.api.geogleplaces.Business
import com.example.restaurantfinder.restaurants.api.geogleplaces.NearbyRestaurantSearchResp
import com.example.restaurantfinder.restaurants.database.AppDatabaseHolder
import com.example.restaurantfinder.restaurants.database.RestaurantEntity
import com.example.restaurantfinder.restaurants.mvvm.models.Restaurant
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject

class RestaurantRepository @Inject constructor(
    private val restaurantUseCase: RestaurantUseCase,
    private val appDatabaseHolder: AppDatabaseHolder
) {
    private var restaurants: MutableLiveData<ArrayList<Restaurant>> = MutableLiveData(arrayListOf())
    private val restaurantList = MutableSharedFlow<MutableLiveData<ArrayList<Restaurant>>>()
    private val errorString = MutableSharedFlow<MutableLiveData<String>>()

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
                        CoroutineScope(Dispatchers.IO).launch {
                            val error: MutableLiveData<String> =
                                MutableLiveData("Response not successful")
                            errorString.emit(error)
                        }
                    }
                }

                override fun onFailure(call: Call<NearbyRestaurantSearchResp?>, error: Throwable) {
                    val errorMessage: String = if (error is SocketTimeoutException) {
                        "Connection Timeout"
                    } else if (error is IOException) {
                        "Timeout"
                    } else {
                        if (call.isCanceled) {
                            "Call was cancelled forcefully"
                        } else {
                            "Network Error :: {${error.localizedMessage}"
                        }
                    }

                    Log.e("Retrofit", "Error getting data: $errorMessage")
                    CoroutineScope(Dispatchers.IO).launch {
                        val returnedError: MutableLiveData<String> = MutableLiveData(errorMessage)
                        errorString.emit(returnedError)
                    }
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

    fun getError(): MutableSharedFlow<MutableLiveData<String>> {
        return errorString
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