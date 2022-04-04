# Restaurant-Finder
Clean code version of Android GPS Restaurant finder.

Compile with Android Studio.  Bumblebee was used.

## Installation
Load project in Android Studio

Edit local.properties add your api keys to the end of the file

```
GOOGLE_MAPS_API_KEY = your api key
GOOGLE_PLACES_API_KEY = your api key
```

Run and install

## Current Features

* MVVM - mvvm pattern
* Single Activity - fragments
* Repository pattern - able to join data from multiple sources
* All Kotlin - All code in Kotlin
* Kotlin flow - Kotlin flow usage
* Retrofit - API calls
* Android Permission Requests
* LiveData - for observing
* Data Binding
* Nav Graph - Nav between screens
* Room Db - database/DAO/Layers for sqlite
* Hilt - Dependency Injection
* compose - programmatic layouts

## TODO: Features to be added

* Fix reload on list frag onstart
* 2 way data binding https://developer.android.com/topic/libraries/data-binding/two-way
* diffutils algorithm in android in recycler view https://developer.android.com/codelabs/kotlin-android-training-diffutil-databinding#3
* use paging token from places api
* pagination in recycler view https://medium.com/dwarsoft/using-pagination-library-for-android-recycler-view-pagination-in-mvvm-architecture-8ae077c8ca48
* Use glide for images
* lazy load images https://stackoverflow.com/questions/541966/how-to-lazy-load-images-in-listview-in-android or Glide for image loading
* related to above load more on scroll down https://stackoverflow.com/questions/26543131/how-to-implement-endless-list-with-recyclerview
* filter https://medium.com/nerd-for-tech/implem-search-in-recyclerview-5bc18b547f4f
* detekt  https://proandroiddev.com/how-to-use-detekt-in-a-multi-module-android-project-6781937fbef2  https://proandroiddev.com/detecting-kotlin-code-smells-with-detekt-e79c52a35faf
* dynamic animation https://developer.android.com/jetpack/androidx/releases/dynamicanimation
* slidingPane https://developer.android.com/jetpack/androidx/releases/slidingpanelayout
* transition https://developer.android.com/jetpack/androidx/releases/transition  https://github.com/Shivamdhuria/flows_guide/tree/added_transition_using_material_motion
* animated vector drawables https://developer.android.com/guide/topics/graphics/vector-drawable-resources
* viewpager https://developer.android.com/jetpack/androidx/releases/viewpager
* material design components https://material.io/develop/android
* Material Components https://material.io/components?platform=android
* navigation https://developer.android.com/jetpack/androidx/releases/navigation
* facebook stetho https://github.com/facebook/stetho
* lifecycle https://developer.android.com/topic/libraries/architecture/lifecycle
* benchmark https://developer.android.com/jetpack/androidx/releases/benchmark
* metrics https://developer.android.com/jetpack/androidx/releases/metrics
* tracing https://developer.android.com/jetpack/androidx/releases/tracing

## Other good projects

* https://github.com/Zhuinden/guide-to-kotlin/wiki - good kotlin learning for those that know Android previously
* https://github.com/bachhoan88/CleanArchitecture - good example of clean architecture
* https://github.com/Lajesh/clean-architecture-kotlin - good example of clean architecture
