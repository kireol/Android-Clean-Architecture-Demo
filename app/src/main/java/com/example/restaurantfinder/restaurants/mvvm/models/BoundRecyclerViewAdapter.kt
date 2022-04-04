package com.example.restaurantfinder.restaurants.mvvm.models

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantfinder.BR
import com.example.restaurantfinder.databinding.ItemRestaurantBinding
import com.example.restaurantfinder.restaurants.mvvm.viewmodels.ItemViewModel
import javax.inject.Inject

class BoundRecyclerViewAdapter @Inject constructor(private val onFavoriteChanged: (Int) -> Unit) :
    RecyclerView.Adapter<BindableViewHolder>() {
    private var itemViewModels: List<ItemViewModel> = emptyList()
    private val viewTypeToLayoutId: MutableMap<Int, Int> = mutableMapOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindableViewHolder {
        val binding: ItemRestaurantBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            viewTypeToLayoutId[viewType] ?: 0,
            parent,
            false
        )
        return BindableViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: BindableViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        super.onBindViewHolder(holder, position, payloads)
        holder.binding.favorite.setOnClickListener {
            onFavoriteChanged(position)
            notifyItemChanged(position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = itemViewModels[position]
        if (!viewTypeToLayoutId.containsKey(item.viewType)) {
            viewTypeToLayoutId[item.viewType] = item.layoutId
        }
        return item.viewType
    }

    override fun getItemCount(): Int = itemViewModels.size

    override fun onBindViewHolder(holder: BindableViewHolder, position: Int) {
        holder.bind(itemViewModels[position])
    }

    fun updateItems(items: List<ItemViewModel>?) {
        itemViewModels = items ?: emptyList()
        notifyDataSetChanged()
    }
}

class BindableViewHolder(internal val binding: ItemRestaurantBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(itemViewModel: ItemViewModel) {
        binding.setVariable(BR.itemViewModel, itemViewModel)
    }
}