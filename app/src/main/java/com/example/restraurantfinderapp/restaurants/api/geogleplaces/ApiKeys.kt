package com.example.restraurantfinderapp.restaurants.api.geogleplaces

import androidx.lifecycle.MutableLiveData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiKeys @Inject constructor(){
    val googlePlacesKey : MutableLiveData<String> = MutableLiveData()
    var pageToken : String = ""
}
