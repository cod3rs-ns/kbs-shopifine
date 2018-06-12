package util

object DistanceCalculator {

  case class Location(lon: Double, lat: Double)

  def calculateDistanceInKilometer(userLocation: Location, orderLocation: Location): Double = {
    val latDistance = Math.toRadians(userLocation.lat - orderLocation.lat)
    val lngDistance = Math.toRadians(userLocation.lon - orderLocation.lon)
    val sinLat      = Math.sin(latDistance / 2)
    val sinLng      = Math.sin(lngDistance / 2)

    val a = sinLat * sinLat +
      (Math.cos(Math.toRadians(userLocation.lat)) * Math.cos(Math.toRadians(orderLocation.lat)) * sinLng * sinLng)
    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

    AverageRadiusOfEarthKm * c
  }

  private val AverageRadiusOfEarthKm = 6371

}
