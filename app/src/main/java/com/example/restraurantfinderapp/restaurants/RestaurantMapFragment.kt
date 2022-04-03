package com.example.restraurantfinderapp.restaurants

import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.restraurantfinderapp.R
import com.example.restraurantfinderapp.databinding.FragmentItemDetailBinding
import com.example.restraurantfinderapp.restaurants.mvvm.models.GPSLocation
import com.example.restraurantfinderapp.restaurants.mvvm.models.Restaurant
import com.example.restraurantfinderapp.restaurants.mvvm.models.RestaurantHolder
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RestaurantMapFragment : Fragment() {
    @Inject
    lateinit var location: GPSLocation
    @Inject
    lateinit var restaurantHolder: RestaurantHolder

    private lateinit var _binding: FragmentItemDetailBinding
    private var cameraWasSet = false
    private var googleMap: GoogleMap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentItemDetailBinding.inflate(inflater, container, false)

        setupMap(savedInstanceState)
        listButtonOnclickSetup()

        return _binding.root
    }

    private fun listButtonOnclickSetup() {
        _binding.listButtonBg.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupMap(savedInstanceState: Bundle?) {
        _binding.mapView.onCreate(savedInstanceState)
        _binding.mapView.onResume()
        try {
            MapsInitializer.initialize(requireActivity().applicationContext)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        _binding.mapView.getMapAsync { mMap ->
            googleMap = mMap
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapApplyPinsAndMoveCamera()
    }

    override fun onResume() {
        super.onResume()
        _binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        _binding.mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding.mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        _binding.mapView.onLowMemory()
    }

    private fun mapApplyPinsAndMoveCamera() {
        _binding.mapView.getMapAsync {
            location.location.observe(viewLifecycleOwner) { lastLocation ->
                addRestaurantPins()
                val currentLocation = addYourLocationPin(lastLocation)
                moveCamera(currentLocation)
            }
        }
    }

    private fun addRestaurantPins() {
        for (restaurant in restaurantHolder.restaurants.value!!) {
            val restaurantLocation = LatLng(restaurant.latitude, restaurant.longitude)
            googleMap!!.addMarker(
                MarkerOptions().icon(
                    BitmapDescriptorFactory.defaultMarker(getRestaurantPinColor(restaurant))
                ).position(restaurantLocation).title(restaurant.name)
                    .snippet(getFavoriteString(restaurant))
            )
        }
    }

    private fun getFavoriteString(restaurant: Restaurant) =
        if (restaurant.isFavorite) {
            getString(R.string.your_favorite)
        } else {
            getString(R.string.not_favorite)
        }

    private fun getRestaurantPinColor(restaurant: Restaurant) =
        if (restaurant.isFavorite) {
            BitmapDescriptorFactory.HUE_GREEN
        } else {
            BitmapDescriptorFactory.HUE_BLUE
        }

    private fun addYourLocationPin(lastLocation: Location): LatLng {
        val currentLocation = LatLng(lastLocation.latitude, lastLocation.longitude)
        googleMap!!.addMarker(
            MarkerOptions().position(currentLocation)
                .title(getString(R.string.your_location))
                .snippet(getString(R.string.you_are_here))
        )
        return currentLocation
    }

    private fun moveCamera(currentLocation: LatLng) {
        if (!cameraWasSet) {
            val cameraPosition =
                CameraPosition.Builder().target(currentLocation).zoom(12f).build()
            googleMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            cameraWasSet = true
        }
    }
}