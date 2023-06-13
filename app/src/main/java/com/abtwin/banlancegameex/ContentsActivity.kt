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

        val locationWorkRequest = PeriodicWorkRequestBuilder<LocationReceiver>(3, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        // 이전에 등록된 작업을 취소시킴
        WorkManager.getInstance(applicationContext).cancelAllWork()
        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork("locationWork",
            ExistingPeriodicWorkPolicy.KEEP, locationWorkRequest)

        val task = WorkManager.getInstance(this).getWorkInfosForUniqueWork("locationWork").get()
        Log.d("워크 큐 확인", task.toString())
    }
}