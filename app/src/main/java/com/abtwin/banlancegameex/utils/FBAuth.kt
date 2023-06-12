package com.abtwin.banlancegameex.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
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

            val currentDateTime = Calendar.getInstance().time
            val dateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.KOREA).format(currentDateTime)

            return dateFormat

        }

        fun getNickname(callback: (String?) -> Unit) {
            auth = FirebaseAuth.getInstance()
            val currentUserUid = auth.currentUser?.uid

            currentUserUid?.let { uid ->
                FBRef.userdataRef.child(uid).child("nickname")
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val nickname = snapshot.value as? String
                            callback(nickname)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            callback(null)
                        }
                    })
            } ?: run {
                callback(null)
            }
        }





        fun getProfile() : String {
            auth = FirebaseAuth.getInstance()

            var currentUser = (auth.currentUser?.photoUrl).toString()
            return currentUser
        }
    }


}