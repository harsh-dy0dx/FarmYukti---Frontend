package com.example.farmyukti

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.location.FusedLocationProviderClient




import com.google.android.gms.tasks.CancellationTokenSource



@SuppressLint("MissingPermission")
fun Context.requestCurrentLocation(onSuccess: (Double, Double) -> Unit, onFailure: (Exception) -> Unit) {

    // The FusedLocationProviderClient is initialized using 'this' (the Context)
    val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(this)

    // 1. Check Permissions (Crucial Step):
    // Check is performed on 'this' (the Context)
    if (ActivityCompat.checkSelfPermission(
            this, // Context instance
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this, // Context instance
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        // Since permissions are checked in the Composable, we just return if they are missing.
        onFailure(SecurityException("Location permissions not granted."))
        return
    }

    // 2. Get Location (Try last known location first)
    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
        if (location != null) {
            onSuccess(location.latitude, location.longitude)
        } else {
            // If last location is null, request a fresh, high-accuracy one.
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                CancellationTokenSource().token
            ).addOnSuccessListener { newLocation ->
                newLocation?.let { onSuccess(it.latitude, it.longitude) }
            }.addOnFailureListener { exception ->
                // Handle case where location cannot be retrieved
                onFailure(exception)
            }
        }
    }.addOnFailureListener { exception ->
        // Handle failure to get last location
        onFailure(exception)
    }
}