package com.example.restaurantfinder.restaurants

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.paging.PagingData
import com.example.restaurantfinder.R
import com.example.restaurantfinder.databinding.ListFragmentBinding
import com.example.restaurantfinder.restaurants.adapters.BoundRecyclerViewAdapter
import com.example.restaurantfinder.restaurants.mvvm.models.ErrorMessage
import com.example.restaurantfinder.restaurants.mvvm.models.GPSLocation
import com.example.restaurantfinder.restaurants.mvvm.models.Restaurant
import com.example.restaurantfinder.restaurants.mvvm.models.RestaurantHolder
import com.example.restaurantfinder.restaurants.mvvm.viewmodels.RestaurantListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RestaurantListFragment : Fragment() {
    @Inject
    lateinit var location: GPSLocation

    @Inject
    lateinit var restaurantHolder: RestaurantHolder

    @Inject
    lateinit var errorMessage: ErrorMessage

    private var searchJob: Job? = null

    private var _binding: ListFragmentBinding? = null
    private val binding get() = _binding!!
    private val restaurantListViewModel: RestaurantListViewModel by activityViewModels()
    private var boundRecyclerViewAdapter: BoundRecyclerViewAdapter =
        BoundRecyclerViewAdapter { position ->
            restaurantListViewModel.updateFavorite(position)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ListFragmentBinding.inflate(inflater, container, false)
        context ?: return binding.root
        binding.itemList.adapter = boundRecyclerViewAdapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.listButtonBg.isEnabled = false
        binding.listButtonBg.isClickable = false

        binding.sendButton.isEnabled = false
        binding.sendButton.isClickable = false

        binding.swipeContainer.setOnRefreshListener {
            fetchRestaurants()
        }

        if (restaurantHolder.restaurants.value?.size == 0) {
            setLocationObserver()
        }

        setToMapButtonListener()
        setSearchButtonListener()
        setDataReadyListener()
        binding.swipeContainer.isRefreshing = true

        errorMessage.errorMessage.observe(viewLifecycleOwner) {
            binding.swipeContainer.isRefreshing = false
            Toast.makeText(activity, it, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setDataReadyListener() {
        restaurantHolder.restaurants.observe(viewLifecycleOwner) {
            restaurantHolder.restaurants.value?.let { restaurants ->
                lifecycleScope.launch {
                    var pagingData: PagingData<Restaurant> = PagingData.empty()
                    for (restaurant in restaurants) {
                        pagingData = PagingData.from(restaurants)
                    }
                    boundRecyclerViewAdapter.submitData(pagingData)
                }

                if (restaurants.size > 0) {
                    binding.swipeContainer.isRefreshing = false

                    binding.sendButton.isEnabled = true
                    binding.sendButton.isClickable = true
                    binding.listButtonBg.isEnabled = true
                    binding.listButtonBg.isClickable = true
                }
            }
        }
    }

    private fun setSearchButtonListener() {
        binding.sendButton.setOnClickListener {
            binding.swipeContainer.isRefreshing = true
            restaurantHolder.restaurants.value?.clear()
            fetchRestaurants()
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

    private fun setLocationObserver() {
        location.location.observe(viewLifecycleOwner) {
            fetchRestaurants()
        }
    }

    private fun fetchRestaurants() {
        searchJob?.cancel()

        searchJob = lifecycleScope.launch {
            var searchString: String ? = null
            if (binding.searchQuery.text.toString().isNotEmpty()) {
                searchString = binding.searchQuery.text.toString()
            }
            restaurantListViewModel.fetchRestaurants(searchString)
//            .collectLatest{
//                boundRecyclerViewAdapter.submitData(it)
//            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

