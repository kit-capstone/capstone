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

        val task = WorkManager.getInstance(this).getWorkInfosForUniqueWork("locationWork").get()
        Log.d("워크 큐 확인", task.toString())
    }
}