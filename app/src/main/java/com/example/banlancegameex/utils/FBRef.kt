package com.example.banlancegameex.utils

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FBRef {
    companion object{
        private val database = Firebase.database

        val bookmarkRef = database.getReference("bookmark_list")
        val postRef = database.getReference("post")
        val userdataRef = database.getReference("userdata")
        val userlocateRef = database.getReference("user_locate")
        val countRef = database.getReference("count")
        val commentRef = database.getReference("comment")
        val todayRef = database.getReference("today")
    }
}