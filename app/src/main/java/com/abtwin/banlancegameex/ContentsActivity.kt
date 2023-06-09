package com.abtwin.banlancegameex

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

        val locationWorkRequest = PeriodicWorkRequestBuilder<com.abtwin.banlancegameex.LocationReceiver>(20, TimeUnit.SECONDS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance().enqueueUniquePeriodicWork("locationWork",
            ExistingPeriodicWorkPolicy.KEEP, locationWorkRequest)

        val task = WorkManager.getInstance(this).getWorkInfosForUniqueWork("locationWork").get()
        Log.d("워크 큐 확인", task.toString())
    }
}