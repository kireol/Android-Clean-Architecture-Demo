import com.example.restaurantfinder.restaurants.mvvm.models.GPSLocation
import org.junit.Assert.assertTrue
import org.junit.Test

class GPSLocationTest {
    @Test
    fun getLocationFromGPSLocation_Returns_default_values() {
        val gpsLocation = GPSLocation()
        assertTrue(gpsLocation.getLocationFromGPSLocation().first == 0.0)
        assertTrue(gpsLocation.getLocationFromGPSLocation().second == 0.0)
    }
}

