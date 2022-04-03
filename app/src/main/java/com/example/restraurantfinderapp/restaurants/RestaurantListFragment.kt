package com.example.restraurantfinderapp.restaurants

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.restraurantfinderapp.R
import com.example.restraurantfinderapp.databinding.ListFragmentBinding
import com.example.restraurantfinderapp.restaurants.mvvm.models.GPSLocation
import com.example.restraurantfinderapp.restaurants.mvvm.models.BindableRecyclerViewAdapter
import com.example.restraurantfinderapp.restaurants.mvvm.viewmodels.RestaurantListViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RestaurantListFragment : Fragment() {
    private var _binding: ListFragmentBinding? = null
    private val binding get() = _binding!!
    private val swipeContainer: SwipeRefreshLayout? = null

    @Inject
    lateinit var location: GPSLocation

    private val restaurantsViewModel: RestaurantListViewModel by activityViewModels()

    private var bindableRecyclerViewAdapter: BindableRecyclerViewAdapter =
        BindableRecyclerViewAdapter { position -> restaurantsViewModel.favoriteClicked(position) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = ListFragmentBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = this
        binding.viewModel = restaurantsViewModel

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
        setLocationObserver()
        setToMapButtonListener()
        setSearchButtonListener()
        setDataReadyListener()
        restaurantsViewModel.data.observe(viewLifecycleOwner) {
            bindableRecyclerViewAdapter.updateItems(it)
        }
    }

    private fun setDataReadyListener() {
        restaurantsViewModel.restaurants.observe(viewLifecycleOwner) {
            binding.indeterminateProgressIndicator.visibility = INVISIBLE
            binding.sendButton.isEnabled = true
            binding.sendButton.isClickable = true
            binding.listButtonBg.isEnabled = true
            binding.listButtonBg.isClickable = true
        }
    }

    private fun setSearchButtonListener() {
        binding.sendButton.setOnClickListener {
            binding.indeterminateProgressIndicator.visibility = VISIBLE
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
            restaurantsViewModel.fetchRestaurants(binding.searchQuery.text.toString())
        } else {
            restaurantsViewModel.fetchRestaurants()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

