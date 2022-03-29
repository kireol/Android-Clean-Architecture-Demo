package com.example.restraurantfinderapp.restaurants.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.restraurantfinderapp.R
import com.example.restraurantfinderapp.RestaurantFinderApplication
import com.example.restraurantfinderapp.databinding.ItemListContentBinding
import com.example.restraurantfinderapp.restaurants.database.AppDatabaseHolder
import com.example.restraurantfinderapp.restaurants.database.RestaurantEntity
import com.example.restraurantfinderapp.restaurants.mvvm.models.Restaurant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class RestaurantsAdapter @Inject constructor(
    private val appDatabaseHolder: AppDatabaseHolder
) : RecyclerView.Adapter<RestaurantsAdapter.RestaurantHolder>() {
    private var boundRestaurants: ArrayList<Restaurant> = arrayListOf()
    var listItemClickCallback: ((id: Int) -> Unit)? = null
    private val appContext: Context = RestaurantFinderApplication.instance.applicationContext
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantHolder {
        val viewBinding: ItemListContentBinding =
            ItemListContentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RestaurantHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: RestaurantHolder, position: Int) {
        holder.restaurantItemBinding.apply {
            val restaurant = boundRestaurants[position]
            val ratingText =
                appContext.getString(R.string.rating) + ": " + restaurant.rating.toString() +
                        "  (" + restaurant.ratingsTotal.toString() + ")"
            restaurantName.text = restaurant.name
            rating.text = ratingText
            favorite.isActivated = restaurant.isFavorite
            setOnClickListener(holder, position)
        }
    }

    private fun setOnClickListener(
        holder: RestaurantHolder,
        position: Int
    ) {
        holder.restaurantItemBinding.favorite.setOnClickListener {
            val clickedRestaurant = boundRestaurants[position]
            clickedRestaurant.isFavorite = clickedRestaurant.isFavorite != true
            holder.restaurantItemBinding.favorite.isActivated =
                boundRestaurants[position].isFavorite == true

            saveFavoriteToDb(boundRestaurants[position])
        }
    }

    private fun saveFavoriteToDb(restaurant: Restaurant) {
        val restaurantDao = appDatabaseHolder.restaurantDb.restaurantDao()
        CoroutineScope(Dispatchers.IO).launch {
            val restaurantDBObj: RestaurantEntity? = restaurantDao.findByReference(restaurant.reference)
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

    @SuppressLint("NotifyDataSetChanged")
    fun clear() {
        boundRestaurants.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return boundRestaurants.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setRestaurants(restaurants: ArrayList<Restaurant>) {
        boundRestaurants = restaurants
        notifyDataSetChanged()
    }

    inner class RestaurantHolder(var restaurantItemBinding: ItemListContentBinding) :
        RecyclerView.ViewHolder(restaurantItemBinding.root) {
        init {
            restaurantItemBinding.navigateContainer.setOnClickListener {
                listItemClickCallback?.invoke(absoluteAdapterPosition)
            }
        }
    }
}
