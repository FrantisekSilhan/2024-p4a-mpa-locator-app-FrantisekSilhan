package com.example.locatorapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val latitude = findViewById<TextView>(R.id.latitude)
        val longitude = findViewById<TextView>(R.id.longitude)
        val openMapsButton = findViewById<Button>(R.id.openMaps)

        var location = Location("")

        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            CONTEXT_INCLUDE_CODE
        )

        if (hasLocationPermissions(this)) {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location1 ->
                latitude.text = getString(R.string.latitude, location1.latitude.toString())
                longitude.text = getString(R.string.longitude, location1.longitude.toString())

                location = location1
            }
        }

        openMapsButton.setOnClickListener {
            val gmmIntentUri =
                Uri.parse("geo:${location.latitude},${location.longitude}?q=${location.latitude},${location.longitude}(My Location)")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")

            startActivity(mapIntent)
        }
    }

    fun hasLocationPermissions(activity: Activity): Boolean {
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasFineLocationPermission || !hasCoarseLocationPermission) {
            val dialog = AlertDialog.Builder(activity).setTitle("Permission Denied")
                .setMessage("Location permission is required to use this app.").show()
            dialog.setOnDismissListener {
                activity.finishAffinity()
            }
            return false
        }

        return true
    }
}