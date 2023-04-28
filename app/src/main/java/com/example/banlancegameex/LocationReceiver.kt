package com.example.banlancegameex

import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.banlancegameex.utils.FBAuth
import com.example.banlancegameex.utils.FBRef
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*

class LocationReceiver(context: Context, workerParameters: WorkerParameters) : Worker(context, workerParameters){

    val locationList = mutableListOf<String>()
    var user_locate : String = ""

    override fun doWork(): Result {
        Log.d("워크 큐 동작 확인", "workqueue가 동작을 시작했습니다.")
        locationList.clear()
        FBRef.userlocateRef.child(FBAuth.getuid()).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(data in snapshot.children){
                    locationList.add(data.getValue(String::class.java)!!)
                }
                Log.d("파이어베이스 확인용", locationList.toString())
                if(locationList.size >= 20) {
                    val counter = mutableMapOf<String, Int>()
                    for(item in locationList) {
                        counter[item] = counter.getOrDefault(item, 0) + 1
                    }

                    var maxCount = 0

                    for ((item, count) in counter) {
                        if (count > maxCount) {
                            maxCount = count
                            user_locate = item
                        }
                    }
                }
                Log.d("사용자 위치 특정", user_locate)

            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("backgroundWork", "Fail to connect firebase")
            }
        })
        if(locationList.size >= 20) {
            return Result.success()
        }
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(applicationContext)
        if(ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ){
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    val geocoder = Geocoder(applicationContext, Locale.getDefault())
                    val address = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    val state = address?.get(0)?.adminArea ?: ""
                    val city = address?.get(0)?.locality ?: ""
                    Log.d("locate_Tast" ,"$state $city")
                    val currentUser = FBAuth.getuid()

                    if (currentUser.isNotEmpty()) {
                        val locationString = "$state $city"

                        val currentTime = System.currentTimeMillis()
                        val sdf = SimpleDateFormat("yyyy-MM-dd-hh-mm")
                        val date = sdf.format(currentTime)

                        FBRef.userlocateRef.child(currentUser).push()
                            .setValue(locationString)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Log.d("LocationWorker", "Location updated successfully")
                                } else {
                                    Log.e(
                                        "LocationWorker",
                                        "Location update failed",
                                        task.exception
                                    )
                                }
                            }
                    }
                }
            }
        } else {
            Log.d("LocationWorker", "You are not permission")
            return Result.failure()
        }
        return Result.success()
    }
}