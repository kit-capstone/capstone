package com.example.banlancegameex.utils

import com.google.firebase.auth.FirebaseAuth

class FBAuth {
    companion object {
        private lateinit var auth: FirebaseAuth

        fun getuid() : String {
            auth = FirebaseAuth.getInstance()

            return auth.currentUser?.uid.toString()
        }
    }
}