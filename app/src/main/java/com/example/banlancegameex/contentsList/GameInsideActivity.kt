package com.example.banlancegameex.contentsList

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.banlancegameex.R
import com.example.banlancegameex.databinding.ActivityGameInsideBinding
import com.example.banlancegameex.utils.FBRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class GameInsideActivity : AppCompatActivity() {

    private val TAG = GameInsideActivity::class.java.simpleName
    var game_name = ""
    var game_count : CountModel = CountModel()
    var opt1_count = 0.0
    var opt2_count = 0.0

    private lateinit var binding : ActivityGameInsideBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_game_inside)

        //데이터베이스에서 받아온 게시물의 키값으로 post(game)접근
        val key = intent.getStringExtra("key")
        getBoardData(key.toString())

        binding.option1.setOnClickListener {
            game_count.total_opt1 ++
            var total = game_count.total_opt1 + game_count.total_opt2
            gameplayResult(total)
        }

        binding.option2.setOnClickListener {
            game_count.total_opt2 ++
            var total = game_count.total_opt1 + game_count.total_opt2
            gameplayResult(total)
        }
    }

    private fun getBoardData(key : String){

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val dataModel = dataSnapshot.getValue(ContentModel::class.java)
                Log.d(TAG, dataModel!!.title)

                binding.titleArea.text = dataModel!!.title
                game_name = dataModel!!.title
                binding.timeArea.text = dataModel!!.time
                binding.opt1Text.text = dataModel!!.option1
                binding.opt1SubText.text = dataModel!!.option1Sub
                binding.opt2Text.text = dataModel!!.option2
                binding.opt2SubText.text = dataModel!!.option2Sub

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FBRef.postRef.child(key).addValueEventListener(postListener)

        val countListener = object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val dataModel = dataSnapshot.getValue(CountModel::class.java)
                game_count = dataSnapshot.getValue(CountModel::class.java)!!
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        FBRef.countRef.child(game_name).addValueEventListener(countListener)
    }

    fun gameplayResult(total : Int) {
        binding.opt1Mask.visibility = View.VISIBLE
        binding.opt2Mask.visibility = View.VISIBLE
        opt1_count = ((game_count.total_opt1.toDouble() / total.toDouble()) * 100)
        opt2_count = ((game_count.total_opt2.toDouble() / total.toDouble()) * 100)
        binding.opt1MaskText.setText("$opt1_count %")
        binding.opt2MaskText.setText("$opt2_count %")
        binding.option1.apply {
            isClickable = false
            isEnabled = false
        }
        binding.option2.apply {
            isClickable = false
            isEnabled = false
        }
    }
}