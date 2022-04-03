package com.example.restraurantfinderapp.restaurants.fragments

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.restraurantfinderapp.databinding.SettingsFragmentBinding
import com.example.restraurantfinderapp.restaurants.mvvm.models.GPSLocation
import com.example.restraurantfinderapp.restaurants.mvvm.models.itemviewmodels.BindableRecyclerViewAdapter
import com.example.restraurantfinderapp.restaurants.mvvm.models.itemviewmodels.RestaurantListViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment() {
    private var _binding: SettingsFragmentBinding? = null
    private val binding get() = _binding!!
    private val swipeContainer: SwipeRefreshLayout? = null

    @Inject
    lateinit var location: GPSLocation

    private val restaurantsViewModel: RestaurantListViewModel by activityViewModels()

    var bindableRecyclerViewAdapter: BindableRecyclerViewAdapter =
        BindableRecyclerViewAdapter { position -> restaurantsViewModel.favoriteClicked(position) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = SettingsFragmentBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = this
        binding.viewModel = restaurantsViewModel

        return binding.root
    }

//    override fun onContextItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.home -> {
//                findNavController().navigateUp()
//                return true
//            }
//        }
//        return super.onContextItemSelected(item)
//    }
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.getItemId()) {
//            R.id.home -> {
//                findNavController().navigateUp()
//                return false
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

