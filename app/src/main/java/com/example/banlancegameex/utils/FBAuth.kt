package com.example.banlancegameex.utils

import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

class FBAuth {
    companion object {
        private lateinit var auth: FirebaseAuth

        fun getuid() : String {
            auth = FirebaseAuth.getInstance()

            return auth.currentUser?.uid.toString()
        }

        //@RequiresApi(Build.VERSION_CODES.O)

        fun getemail() : String {
            auth = FirebaseAuth.getInstance()

            return auth.currentUser?.email.toString()
        }
        fun getTime() : String {

//            var now = LocalDate.now()
//            var Strnow = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
//            var Intnow = Strnow.toInt()
//            val nowYear = Intnow / 10000
//            val nowMonth = (Intnow % 10000) / 100
//            val nowDay = (Intnow % 10000) % 100

            val currentDateTime = Calendar.getInstance().time
            val dateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.KOREA).format(currentDateTime)

            return dateFormat

        }

        fun getProfile() : String {
            auth = FirebaseAuth.getInstance()

            var currentUser = (auth.currentUser?.photoUrl).toString()
            return currentUser
        }
    }


}