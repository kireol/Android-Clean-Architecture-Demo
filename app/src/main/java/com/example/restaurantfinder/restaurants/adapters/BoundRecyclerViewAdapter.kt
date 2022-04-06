package com.example.restaurantfinder.restaurants.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantfinder.databinding.ItemRestaurantBinding
import com.example.restaurantfinder.restaurants.mvvm.models.Restaurant

class BoundRecyclerViewAdapter(val favoriteClicked: (Int) -> Unit) : PagingDataAdapter<Restaurant,
        BoundRecyclerViewAdapter.GalleryViewHolder>(GalleryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        return GalleryViewHolder(
            ItemRestaurantBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        val restaurant = getItem(position)
        if (restaurant != null) {
            holder.bind(restaurant)
        }

        holder.binding.favorite.setOnClickListener {
//            restaurantListViewModel.updateFavorite(position)
            favoriteClicked(position)
            notifyItemChanged(position)
        }

    }

    class GalleryViewHolder(
        val binding: ItemRestaurantBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Restaurant) {
            binding.apply {
                restaurant = item
                executePendingBindings()
            }
        }
    }
}

private class GalleryDiffCallback : DiffUtil.ItemCallback<Restaurant>() {
    override fun areItemsTheSame(oldItem: Restaurant, newItem: Restaurant): Boolean {
        return oldItem.reference == newItem.reference
    }

    override fun areContentsTheSame(oldItem: Restaurant, newItem: Restaurant): Boolean {
        return oldItem == newItem
    }
}
////////////////////////////////////////////////////
//class BoundRecyclerViewAdapter @Inject constructor(private val onFavoriteChanged: (Int) -> Unit) :
//    PagingDataAdapter<Restaurant, BindableViewHolder>(RestaurantDiffCallback()) {
//    private var itemViewModels: List<ItemViewModel> = emptyList()
//    private val viewTypeToLayoutId: MutableMap<Int, Int> = mutableMapOf()
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindableViewHolder {
//        return BindableViewHolder(
//            ItemRestaurantBinding.inflate(
//                LayoutInflater.from(parent.context),
//                parent,
//                false
//            )
//        )
//    }
//
//    override fun onBindViewHolder(
//        holder: BindableViewHolder,
//        position: Int,
//        payloads: MutableList<Any>
//    ) {
//        super.onBindViewHolder(holder, position, payloads)
//        holder.binding.favorite.setOnClickListener {
//            onFavoriteChanged(position)
//            notifyItemChanged(position)
//        }
//    }
//
//    override fun getItemViewType(position: Int): Int {
//        val item = itemViewModels[position]
//        if (!viewTypeToLayoutId.containsKey(item.viewType)) {
//            viewTypeToLayoutId[item.viewType] = item.layoutId
//        }
//        return item.viewType
//    }
//
//    override fun getItemCount(): Int = itemViewModels.size
//
//    override fun onBindViewHolder(holder: BindableViewHolder, position: Int) {
//        val rest = getItem(position)
//        if (rest != null) {
//            holder.bind(rest)
//        }
//
////        holder.bind(itemViewModels[position])
//    }
//
////    fun updateItems(items: List<ItemViewModel>?) {
////        itemViewModels = items ?: emptyList()
////        notifyDataSetChanged()
////    }
//}
//
////class BindableViewHolder(internal val binding: ItemRestaurantBinding) :
////    RecyclerView.ViewHolder(binding.root) {
////
////    fun bind(itemViewModel: ItemViewModel) {
////        binding.setVariable(BR.itemViewModel, itemViewModel)
////    }
////}
//class BindableViewHolder(
//    val binding: ItemRestaurantBinding
//) : RecyclerView.ViewHolder(binding.root) {
////    init {
////        binding.setClickListener { view ->
////            binding.photo?.let { photo ->
////                val uri = Uri.parse(photo.user.attributionUrl)
////                val intent = Intent(Intent.ACTION_VIEW, uri)
////                view.context.startActivity(intent)
////            }
////        }
////    }
//
//    fun bind(item: Restaurant) {
//        binding.apply {
//            itemViewModel = item
//            executePendingBindings()
//        }
//    }
//}
//
//private class RestaurantDiffCallback : DiffUtil.ItemCallback<Restaurant>() {
//    override fun areItemsTheSame(oldItem: Restaurant, newItem: Restaurant): Boolean {
//        return oldItem.reference == newItem.reference
//    }
//
//    override fun areContentsTheSame(oldItem: Restaurant, newItem: Restaurant): Boolean {
//        return oldItem == newItem
//    }
//}
