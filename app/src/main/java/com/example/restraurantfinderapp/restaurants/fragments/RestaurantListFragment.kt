package com.example.restraurantfinderapp.restaurants.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.restraurantfinderapp.R
import com.example.restraurantfinderapp.databinding.ListFragmentBinding
import com.example.restraurantfinderapp.restaurants.adapters.RestaurantsAdapter
import com.example.restraurantfinderapp.restaurants.database.AppDatabaseHolder
import com.example.restraurantfinderapp.restaurants.mvvm.models.GPSLocation
import com.example.restraurantfinderapp.restaurants.mvvm.viewmodels.RestaurantsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class RestaurantListFragment : Fragment() {
    private lateinit var listAdapter: RestaurantsAdapter
    private var _binding: ListFragmentBinding? = null
    private val binding get() = _binding!!
    private val swipeContainer: SwipeRefreshLayout? = null

    @Inject
    lateinit var location: GPSLocation

    @Inject
    lateinit var appDatabaseHolder: AppDatabaseHolder

    private val restaurantsViewModel: RestaurantsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.listButtonBg.isEnabled = false

        binding.swipeContainer.setOnRefreshListener {
            fetchRestaurants()
        }
        swipeContainer?.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        )
        setLocationObserver()
        setRestaurantListObserver()
        setToMapButtonListener()
        setSearchButtonListener()
        setupAdapterBinding()
    }

    private fun setSearchButtonListener() {
        binding.sendButton.setOnClickListener {
            binding.indeterminateProgressIndicator.visibility = VISIBLE
            fetchRestaurants()
        }
    }

    private fun fetchRestaurants() {
        if (binding.searchQuery.text.toString().isNotEmpty()) {
            restaurantsViewModel.fetchRestaurants(binding.searchQuery.text.toString())
        } else {
            restaurantsViewModel.fetchRestaurants()
        }
    }

    private fun setToMapButtonListener() {
        binding.listButtonBg.setOnClickListener {
            val bundle = Bundle()
            this.view?.let {
                Navigation.findNavController(it).navigate(R.id.show_map_detail, bundle)
            }
        }
    }

    private fun setRestaurantListObserver() {
        restaurantsViewModel.restaurants.observe(viewLifecycleOwner) { restaurants ->
            listAdapter.clear()
            listAdapter.setRestaurants(restaurants)
            binding.indeterminateProgressIndicator.visibility = GONE
            binding.swipeContainer.isRefreshing =false
        }
    }

    private fun setLocationObserver() {
        location.location.observe(viewLifecycleOwner) {
            binding.listButtonBg.isEnabled = true
            binding.listButtonBg.isClickable = true
            if (restaurantsViewModel.restaurants.value == null) {
                restaurantsViewModel.fetchRestaurants()
            }
        }
    }

    private fun setupAdapterBinding(
    ) {
        listAdapter = RestaurantsAdapter(appDatabaseHolder)
        _binding?.itemList?.adapter = listAdapter
        _binding?.itemList?.layoutManager = LinearLayoutManager(activity)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}