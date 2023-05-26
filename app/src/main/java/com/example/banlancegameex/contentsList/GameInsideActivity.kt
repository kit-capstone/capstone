package com.example.banlancegameex.contentsList

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
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
import com.example.banlancegameex.comment.CommentgroupModel
import com.example.banlancegameex.databinding.ActivityGameInsideBinding
import com.example.banlancegameex.fragment.AgeRangeFragment
import com.example.banlancegameex.fragment.GenderFragment
import com.example.banlancegameex.fragment.LocateFragment
import com.example.banlancegameex.utils.CountResult
import com.example.banlancegameex.utils.FBAuth
import com.example.banlancegameex.utils.FBRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener


class GameInsideActivity : AppCompatActivity() {

    private val TAG = GameInsideActivity::class.java.simpleName
    var game_name = ""
    var KeyList = ArrayList<String>()
    lateinit var game_count : CountModel
    var opt1_count = 0.0
    var opt2_count = 0.0
    private lateinit var key: String

    private lateinit var binding : ActivityGameInsideBinding

    private var alertDialog: AlertDialog? = null

    private val commentDataList = ArrayList<CommentModel>()
    private val commentgroup = ArrayList<CommentgroupModel>()
    private val commentKeyList = ArrayList<String>()

    // recycler view를 위한 adapter
    private lateinit var commentAdapter : CommentRVAdapter

    var bundle = Bundle()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_game_inside)

        //데이터베이스에서 받아온 게시물의 키값으로 post(game)접근
        key = intent.getStringExtra("key").toString()
        KeyList = (intent.getStringArrayListExtra("keylist"))?:ArrayList<String>()
        getBoardData(key.toString())

        binding.opt1Mask.apply {
            isClickable = false
            isEnabled = false
        }
        binding.opt2Mask.apply {
            isClickable = false
            isEnabled = false
        }

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

        binding.opt1Mask.setOnClickListener {
            nextpost()
        }

        binding.opt2Mask.setOnClickListener {
            nextpost()
        }

        //메뉴
        binding.gameSettingIcon.setOnClickListener{
            showDialog()
        }

        //댓글
        binding.commentBtn.setOnClickListener {
            val commentText = binding.commentArea.text.toString().trim() // 댓글 내용 가져오기

            if (commentText.isNotEmpty()) { // 댓글 내용이 비어있지 않은 경우에만 데이터베이스에 입력
                insertComment(key)
            } else {
                Toast.makeText(this, "댓글을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.commentVisibleControll.setOnClickListener {
            if(binding.commentFrame.visibility == View.GONE){

                if(binding.countFrame.visibility == View.VISIBLE){
                    binding.countFrame.visibility = View.GONE
                    binding.countFrame.layoutParams.height = 0
                }

                binding.commentFrame.visibility = View.VISIBLE
                binding.commentFrame.layoutParams.height = 1000

                binding.commentFrame.post {
                    val animator = ObjectAnimator.ofInt(binding.gameScroll, "scrollY", binding.commentFrame.top)
                    animator.duration = 800
                    animator.start()
                }
            }
            else if(binding.commentFrame.visibility == View.VISIBLE){
                binding.commentFrame.post {
                    val animator = ObjectAnimator.ofInt(binding.gameScroll, "scrollY", binding.commentFrame.top)
                    animator.duration = 800
                    animator.start()
                    binding.commentFrame.visibility = View.GONE
                    binding.commentFrame.layoutParams.height = 0
                }
            }
        }

        binding.countVisibleControll.setOnClickListener {
            if(binding.countFrame.visibility == View.GONE) {
                if(binding.commentFrame.visibility == View.VISIBLE) {
                    binding.commentFrame.visibility = View.GONE
                    binding.commentFrame.layoutParams.height = 0
                }
                binding.countFrame.visibility = View.VISIBLE
                binding.countFrame.layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT

                binding.countFrame.post {
                    val animator = ObjectAnimator.ofInt(binding.gameScroll, "scrollY", binding.countFrame.top)
                    animator.duration = 800
                    animator.start()
                }
            }
            else if(binding.countFrame.visibility == View.VISIBLE){
                binding.countFrame.post {
                    val animator = ObjectAnimator.ofInt(binding.gameScroll, "scrollY", binding.countFrame.top)
                    animator.duration = 800
                    animator.start()
                    binding.countFrame.visibility = View.GONE
                    binding.countFrame.layoutParams.height = 0
                }
            }
        }

        binding.commentArea.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND){
                binding.commentBtn.performClick()
                true
            } else {
                false
            }
        }

        getCommentData(key)

        commentAdapter = CommentRVAdapter(this, key, commentDataList, commentKeyList)
        val rv : RecyclerView = binding.commentRV
        rv.adapter = commentAdapter
        rv.layoutManager = LinearLayoutManager(this)

        //binding.commentRV.adapter = commentAdapter
    }

    private fun getCommentData(key : String){

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                commentDataList.clear()
                commentgroup.clear()
                commentKeyList.clear()

                for (dataModel in dataSnapshot.children) {

                    val item = dataModel.getValue(CommentModel::class.java)
                    commentgroup.add(CommentgroupModel(item!!, dataModel.key.toString()))
                }

                for(i in commentgroup) {
                    commentDataList.add(i.comment)
                    commentKeyList.add(i.key)
                }

                commentAdapter.notifyDataSetChanged()
                Log.d("테스트용", "commentDataList size: ${commentDataList.size}")
                for (commentData in commentDataList) {
                    Log.d("테스트용", "comment: ${commentData.commentTitle}")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        FBRef.commentRef.child(key).addValueEventListener(postListener)
    }

    private fun insertComment(key : String){

        val commentText = binding.commentArea.text.toString()

        FBAuth.getNickname { nickname ->
            if (nickname != null) {
                Log.d("CommentText", commentText)
                FBRef.commentRef.child(key)
                    .push()
                    .setValue(
                        CommentModel(
                            commentText,
                            FBAuth.getTime(),
                            nickname,
                            FBAuth.getuid()
                        )
                    )
            } else {
                // 닉네임이 없거나 에러가 발생한 경우
                println("닉네임을 가져올 수 없습니다.")
            }
        }


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

                    bundle.putString("title", dataModel!!.title)
                    bundle.putString("opt1", dataModel!!.option1)
                    bundle.putString("opt2", dataModel!!.option2)

                    val fragment = AgeRangeFragment()
                    fragment.arguments = bundle

                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.fragmentContainerView2, fragment)
                    transaction.commit()

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

                        bundle.putSerializable("count", game_count)

                        val fragment = AgeRangeFragment()
                        fragment.arguments = bundle

                        val transaction = supportFragmentManager.beginTransaction()
                        transaction.replace(R.id.fragmentContainerView2, fragment)
                        transaction.commitAllowingStateLoss()

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
            FBRef.countRef.child(game_name).removeValue()
            Toast.makeText(this,"삭제완료", Toast.LENGTH_SHORT).show()
            finish()
        }

    }

    @SuppressLint("SetTextI18n")
    fun gameplayResult(total : Int) {
        binding.opt1Mask.visibility = View.VISIBLE
        binding.opt2Mask.visibility = View.VISIBLE

        opt1_count = ((game_count.total_opt1.toDouble() / total.toDouble()) * 100)
        opt2_count = ((game_count.total_opt2.toDouble() / total.toDouble()) * 100)

        val opt1_percent = String.format("%.1f", opt1_count)
        val opt2_percent = String.format("%.1f", opt2_count)

        binding.opt1MaskText.text = "$opt1_percent %"
        binding.opt2MaskText.text = "$opt2_percent %"
        binding.option1.apply {
            isClickable = false
            isEnabled = false
        }
        binding.option2.apply {
            isClickable = false
            isEnabled = false
        }
        binding.opt1Mask.apply {
            isClickable = true
            isEnabled = true
        }
        binding.opt2Mask.apply {
            isClickable = true
            isEnabled = true
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

    fun openFragment(dir : Int, bundle: Bundle) {
        val transaction = supportFragmentManager.beginTransaction()
        val ageFragment = AgeRangeFragment()
        val genderFragment = GenderFragment()
        val locateFragment = LocateFragment()

        ageFragment.arguments = bundle
        genderFragment.arguments = bundle
        locateFragment.arguments = bundle

        when(dir) {
            1 -> transaction.replace(R.id.fragmentContainerView2, ageFragment)
            2 -> transaction.replace(R.id.fragmentContainerView2, genderFragment)
            3 -> transaction.replace(R.id.fragmentContainerView2, locateFragment)
        }
        transaction.commitAllowingStateLoss()
    }

    fun nextpost() {
        KeyList.remove(key)
        if(KeyList.isNotEmpty()){
            key = KeyList.random()
            val _intent = intent
            _intent.putExtra("key", key)
            _intent.putStringArrayListExtra("keylist", KeyList)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(_intent)
        }else{
            Toast.makeText(this, "현재 모든 게임을 완료했습니다.", Toast.LENGTH_LONG).show()
        }
    }
}