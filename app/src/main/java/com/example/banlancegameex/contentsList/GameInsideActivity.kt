package com.example.banlancegameex.contentsList

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.example.banlancegameex.R
import com.example.banlancegameex.databinding.ActivityGameInsideBinding
import com.example.banlancegameex.utils.FBRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class GameInsideActivity : AppCompatActivity() {

    private val TAG = GameInsideActivity::class.java.simpleName

    private lateinit var binding : ActivityGameInsideBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_game_inside)

        //데이터베이스에서 받아온 게시물의 키값으로 post(game)접근
        val key = intent.getStringExtra("key")
        getBoardData(key.toString())
    }

    private fun getBoardData(key : String){

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val dataModel = dataSnapshot.getValue(ContentModel::class.java)
                Log.d(TAG, dataModel!!.title)

                binding.titleArea.text = dataModel!!.title
                binding.timeArea.text = dataModel!!.time

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FBRef.postRef.child(key).addValueEventListener(postListener)
    }
}