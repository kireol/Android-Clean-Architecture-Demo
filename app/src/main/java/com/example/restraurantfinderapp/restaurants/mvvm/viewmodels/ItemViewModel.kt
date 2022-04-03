package com.example.restraurantfinderapp.restaurants.mvvm.viewmodels

import androidx.annotation.LayoutRes

interface ItemViewModel {
    @get:LayoutRes
    val layoutId: Int
    val viewType: Int
        get() = 0
}