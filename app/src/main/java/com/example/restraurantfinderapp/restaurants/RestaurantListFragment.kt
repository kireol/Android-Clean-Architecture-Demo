package com.example.restraurantfinderapp.restaurants

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.restraurantfinderapp.R
import com.example.restraurantfinderapp.databinding.ListFragmentBinding
import com.example.restraurantfinderapp.restaurants.mvvm.models.BindableRecyclerViewAdapter
import com.example.restraurantfinderapp.restaurants.mvvm.models.GPSLocation
import com.example.restraurantfinderapp.restaurants.mvvm.models.RestaurantHolder
import com.example.restraurantfinderapp.restaurants.mvvm.viewmodels.RestaurantListViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RestaurantListFragment : Fragment() {
    @Inject
    lateinit var location: GPSLocation

    @Inject
    lateinit var restaurantHolder: RestaurantHolder

    private var _binding: ListFragmentBinding? = null
    private val binding get() = _binding!!
    private val swipeContainer: SwipeRefreshLayout? = null
    private val restaurantListViewModel: RestaurantListViewModel by activityViewModels()
    private var bindableRecyclerViewAdapter: BindableRecyclerViewAdapter =
        BindableRecyclerViewAdapter { position -> restaurantListViewModel.updateFavorite(position) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = ListFragmentBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = this
        binding.viewModel = restaurantListViewModel
        binding.itemList.adapter = bindableRecyclerViewAdapter

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
        swipeContainer?.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        )
        if(restaurantHolder.restaurants.value?.size  == 0){
            setLocationObserver()
        }
        setToMapButtonListener()
        setSearchButtonListener()
        setDataReadyListener()
        binding.swipeContainer.isRefreshing = true

        restaurantListViewModel.data.observe(viewLifecycleOwner) {
            bindableRecyclerViewAdapter.updateItems(it)
        }
    }

    private fun setDataReadyListener() {
        restaurantHolder.restaurants.observe(viewLifecycleOwner) {
            restaurantHolder.restaurants.value?.let { restaurants ->
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
        if (binding.searchQuery.text.toString().isNotEmpty()) {
            restaurantListViewModel.fetchRestaurants(binding.searchQuery.text.toString())
        } else {
            restaurantListViewModel.fetchRestaurants()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

