package com.abtwin.banlancegameex

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.work.*
import com.abtwin.banlancegameex.utils.FBAuth
import com.kakao.sdk.common.KakaoSdk
import java.util.concurrent.TimeUnit

class GlobalApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        // KaKao SDK  초기화
        KakaoSdk.init(this, "177df88419ea0808e71d06432ebfdda6")

        Log.d("디버그 확인용", FBAuth.getuid())

        // 백그라운드 작업 등록
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val locationWorkRequest = PeriodicWorkRequestBuilder<LocationReceiver>(3, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        // 이전에 등록된 작업을 취소시킴
        WorkManager.getInstance().cancelAllWork()
        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork("locationWork",
            ExistingPeriodicWorkPolicy.KEEP, locationWorkRequest)
    }
}