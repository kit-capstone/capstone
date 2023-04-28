package com.example.banlancegameex

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.work.*
import java.util.concurrent.TimeUnit

class ContentsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contents)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val locationWorkRequest = PeriodicWorkRequestBuilder<LocationReceiver>(10, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork("locationWork",
            ExistingPeriodicWorkPolicy.KEEP, locationWorkRequest)

        val task = WorkManager.getInstance(this).getWorkInfosForUniqueWork("locationWork").get()
        Log.d("워크 큐 확인", task.toString())

//        val registerAlarmWorkRequest = OneTimeWorkRequest.Builder(LocationReceiver::class.java).build()
//
//        WorkManager.getInstance(this).enqueue(registerAlarmWorkRequest)
    }
}