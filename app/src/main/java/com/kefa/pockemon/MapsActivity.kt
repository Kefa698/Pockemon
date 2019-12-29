package com.kefa.pockemon

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity() : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        checkPermission()
        LoadPockemon()

    }

    var ACCESSLOCATION = 123
    fun checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat
                    .checkSelfPermission(
                        this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    ACCESSLOCATION
                )
                return
            }
        }
        getUserLocation()
    }

    fun getUserLocation() {
        Toast.makeText(this, "this is the user location", Toast.LENGTH_LONG).show()
        //TODO:will imlement later
        var myLocation = MyLocationListener()
        var locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3, 3f, myLocation)
        var mythread = myThread()
        mythread.start()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            ACCESSLOCATION -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getUserLocation()
                } else {
                    Toast.makeText(this, "we cannot access to your location", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera

    }

    var location: Location? = null
    //Get user location


    init {
        location = Location("start")
        location!!.latitude = 0.0
        location!!.longitude = 0.0
    }

    inner class MyLocationListener : LocationListener {
        override fun onLocationChanged(p0: Location?) {
            location = p0
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onProviderEnabled(provider: String?) {
            //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onProviderDisabled(provider: String?) {
            //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }

    var oldLocation: Location? = null

    inner class myThread : Thread {
        constructor() : super() {

            oldLocation = Location("start")
            oldLocation!!.latitude = 0.0
            oldLocation!!.longitude = 0.0

        }

        override fun run() {
            while (true) {

                try {
                    if (oldLocation!!.distanceTo(location) == 0f) {
                        continue
                    }
                    oldLocation = location
                    runOnUiThread {
                        mMap!!.clear()

                        //shows myself

                        val sydney = LatLng(location!!.longitude, location!!.latitude)
                        mMap.addMarker(
                            MarkerOptions()
                                .position(sydney)
                                .title("myself")
                                .snippet("here i am")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.avengers))
                        )
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 2f))

                        //show pockemons

                        for (i in 0..listPockemons.size) {
                            var newPockemon = listPockemons[i]
                            if (newPockemon.IsCatch == false) {

                                val pockemonLocation = LatLng(
                                    newPockemon.location!!.latitude,
                                    newPockemon.location!!.latitude
                                )
                                mMap.addMarker(
                                    MarkerOptions()
                                        .position(pockemonLocation)
                                        .title(newPockemon.name!!)
                                        .snippet(newPockemon.des!! + ",power:" + newPockemon!!.power)
                                        .icon(BitmapDescriptorFactory.fromResource(newPockemon.image!!))
                                )
                                if (location!!.distanceTo(newPockemon.location) < 2) {
                                    newPockemon.IsCatch = true
                                    listPockemons[i] = newPockemon
                                    playerPower += newPockemon.power!!
                                    Toast.makeText(
                                        applicationContext,
                                        "you cought new pockemon you new power is " + playerPower,
                                        Toast.LENGTH_LONG
                                    ).show()

                                }


                            }
                        }
                    }
                    Thread.sleep(10000)
                } catch (ex: Exception) {

                }
            }


        }
    }

    var playerPower = 0.0
    var listPockemons = ArrayList<Pockemons>()
    fun LoadPockemon() {
        listPockemons.add(
            Pockemons(
                R.drawable.shark, "shark",
                "living in Janpan", 50.0, 37.77789999489305, -122.40184664
            )
        )
        listPockemons.add(
            Pockemons(
                R.drawable.battlefield, "battlefield",
                "here I am in usa", 90.5, 37.79495, -122.44049
            )
        )
        listPockemons.add(
            Pockemons(
                R.drawable.ironman, "ironMan",
                "here I iraq", 52.0, 34.7816621, -122.41225
            )
        )

    }
}
