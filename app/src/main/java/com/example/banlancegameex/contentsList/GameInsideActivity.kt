package com.example.banlancegameex.contentsList

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.banlancegameex.R
import com.example.banlancegameex.UserDataModel
import com.example.banlancegameex.comment.CommentModel
import com.example.banlancegameex.comment.CommentRVAdapter
import com.example.banlancegameex.databinding.ActivityGameInsideBinding
import com.example.banlancegameex.utils.CountResult
import com.example.banlancegameex.utils.FBAuth
import com.example.banlancegameex.utils.FBRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class GameInsideActivity : AppCompatActivity() {

    private val TAG = GameInsideActivity::class.java.simpleName
    var game_name = ""
    lateinit var game_count : CountModel
    var opt1_count = 0.0
    var opt2_count = 0.0
    private lateinit var key: String

    private lateinit var binding : ActivityGameInsideBinding

    private var alertDialog: AlertDialog? = null

    private val commentDataList = mutableListOf<CommentModel>()

    // recycler view를 위한 adapter
    private lateinit var commentAdapter : CommentRVAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_game_inside)

        //데이터베이스에서 받아온 게시물의 키값으로 post(game)접근
        key = intent.getStringExtra("key").toString()
        getBoardData(key.toString())

        binding.option1.setOnClickListener {
            Log.d("테스트용", game_count.toString())
            game_count.total_opt1 ++
            var total = game_count.total_opt1 + game_count.total_opt2
            gameplayResult(total)
            updateCountData("A")
        }

        binding.option2.setOnClickListener {
            game_count.total_opt2 ++
            var total = game_count.total_opt1 + game_count.total_opt2
            gameplayResult(total)
            updateCountData("B")
        }

        //메뉴
        binding.gameSettingIcon.setOnClickListener{
            showDialog()
        }

        //댓글
        binding.commentBtn.setOnClickListener {
            insertComment(key)
        }

        binding.commentVisibleControll.setOnClickListener {
            if(binding.commentFrame.visibility == View.GONE){
                binding.commentFrame.visibility = View.VISIBLE
                binding.commentFrame.layoutParams.height = 1000
            }
            else if(binding.commentFrame.visibility == View.VISIBLE){
                binding.commentFrame.visibility = View.GONE
                binding.commentFrame.layoutParams.height = 0
            }
        }

        getCommentData(key)

        commentAdapter = CommentRVAdapter(commentDataList)
        val rv : RecyclerView = binding.commentRV
        rv.adapter = commentAdapter
        rv.layoutManager = LinearLayoutManager(this)

        //binding.commentRV.adapter = commentAdapter


    }

    private fun getCommentData(key : String){

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                commentDataList.clear()

                for (dataModel in dataSnapshot.children) {

                    val item = dataModel.getValue(CommentModel::class.java)
                    commentDataList.add(item!!)
                }

                commentAdapter.notifyDataSetChanged()




            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FBRef.commentRef.child(key).addValueEventListener(postListener)




    }

    private fun insertComment(key : String){
        // comment
        //   - BoardKey
        //        - CommentKey
        //            - CommentData
        //            - CommentData
        //            - CommentData
        FBRef.commentRef
            .child(key)
            .push()
            .setValue(
                CommentModel(
                    binding.commentArea.text.toString(),
                    FBAuth.getTime()
                )
            )

        Toast.makeText(this, "댓글 입력 완료", Toast.LENGTH_SHORT).show()
        binding.commentArea.setText("")

    }

    override fun onPause() {
        // 액티비티가 일시정지될 때 다이얼로그를 닫음
        super.onPause()
        alertDialog?.dismiss()
    }

    private fun getBoardData(key : String){

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                try{
                    val dataModel = dataSnapshot.getValue(ContentModel::class.java)
                    binding.titleArea.text = dataModel!!.title
                    game_name = dataModel!!.title
                    binding.timeArea.text = dataModel!!.time
                    binding.opt1Text.text = dataModel!!.option1
                    binding.opt1SubText.text = dataModel!!.option1Sub
                    binding.opt2Text.text = dataModel!!.option2
                    binding.opt2SubText.text = dataModel!!.option2Sub

                    val myUid = FBAuth.getuid()
                    val writerUid = dataModel.uid

                    if(myUid.equals(writerUid)){
                        Log.d(TAG, "내가 쓴 글")
                        binding.gameSettingIcon.isVisible = true
                    } else {
                        Log.d(TAG, "내가 쓴 글 아님")
                    }

                } catch (e : Exception){
                    Log.d(TAG, "삭제완료")

               }



            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FBRef.postRef.child(key).addValueEventListener(postListener)

        val countListener = object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (data in dataSnapshot.children) {
                    if(data.key.toString() == game_name){
                        game_count = data.getValue(CountModel::class.java)!!
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        FBRef.countRef.addValueEventListener(countListener)
    }

    private fun showDialog(){

        val mDialogView = LayoutInflater.from(this).inflate(R.layout.game_setting_dialog, null)
        val mBuilder = AlertDialog.Builder(this)
            .setView(mDialogView)
            .setTitle("게시글 수정/삭제")

        alertDialog = mBuilder.show()

        //다이얼로그의 백그라운드를 둥글게 깎기 위해선 이 코드가 필요
        alertDialog?.window?.setBackgroundDrawableResource(R.drawable.background_radius)

        alertDialog?.findViewById<Button>(R.id.editBtn)?.setOnClickListener{
            Toast.makeText(this,"aa", Toast.LENGTH_SHORT).show()
        }
        alertDialog?.findViewById<Button>(R.id.removeBtn)?.setOnClickListener{
            FBRef.postRef.child(key).removeValue()
            Toast.makeText(this,"삭제완료", Toast.LENGTH_SHORT).show()
            finish()
        }

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

    fun updateCountData(choose : String) {
        FBRef.userdataRef.orderByChild("email").equalTo(CountResult.currentUser)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // firebase database에 해당 유저의 email이 존재 시
                        for (data in snapshot.children) {
                            CountResult.userdata = data.getValue(UserDataModel::class.java)!!
                        }
                        CountResult.delaycount(choose, game_count)
                        FBRef.countRef
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    for(childSnapshot in snapshot.children){
                                        if(childSnapshot.key.toString() == game_name){
                                            FBRef.countRef.child(game_name).setValue(game_count)
                                        }
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {

                                }
                            })
                        Log.d("관리용", game_count.toString())
                    } else {
                        // firebase database에 해당 유저의 email이 없을 시
                        Log.w("통계 데이터 관리 에러", "DB에 정상적으로 사용자 정보가 존재하지 않습니다.")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w("통계 데이터 관리 에러", "DB에서 정상적으로 사용자 정보를 가져오지 못했습니다.")
                }
            })
    }
}