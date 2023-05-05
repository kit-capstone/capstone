package com.example.banlancegameex

import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.work.WorkManager
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

        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(applicationContext)
        // 해당 앱이 백그라운드로 사용자 위치정보를 관리할 수 있는 권한이 있는지 확인힌다.
        if((ContextCompat.checkSelfPermission(applicationContext, android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            == PackageManager.PERMISSION_GRANTED) && (FBAuth.getuid() != null)
        ){
            // 해당 사용자 위치정보 리스트 초기화
            locationList.clear()
            // firebase realtime database에서 수집한 사용자 위치정보 가져옴
            FBRef.userlocateRef.child(FBAuth.getuid()).addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(data in snapshot.children){
                        locationList.add(data.getValue(String::class.java)!!)
                    }
                    Log.d("파이어베이스 확인용", locationList.toString())
                    // 해당 사용자의 위치 정보가 20개 이상 모였다면
                    // 그 중 가장 많이 나타난 위치를 사용자의 주 생활지역으로 특정한다.
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

            // list에 저장된 사용자 위치정보가 20개 이상이면 해당 task를 database에 사용자 위치정보를 변경하고 종료한다.
            if(locationList.size >= 20) {
                Log.d("리시버 확인용", "백그라운드 작업이 완료되었습니다.")
                // 사용자 위치정보 변경 코드 필요
                // 구현 완료. 이후 사용자 회원가입, 사용자 정보 수정 페이지 수정 완료 후 주석 처리 해제 필요
//                FBRef.userdataRef.orderByChild("email").equalTo(FBAuth.getEmail())
//                    .addListenerForSingleValueEvent(object : ValueEventListener {
//                        override fun onDataChange(snapshot: DataSnapshot) {
//                            for(childSnapshot in snapshot.children){
//                                val user = childSnapshot.getValue(UserDataModel::class.java)
//                                user?.let {
//                                    it.locate = user_locate
//                                    FBRef.userdataRef.child(childSnapshot.key!!).setValue(it)
//                                }
//                            }
//                        }
//
//                        override fun onCancelled(error: DatabaseError) {
//                            Log.e("backgroundWorkError", "Fail to connect firebase")
//                        }
//                    })
                WorkManager.getInstance().cancelUniqueWork("locationWork")
                return Result.success()
            }

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