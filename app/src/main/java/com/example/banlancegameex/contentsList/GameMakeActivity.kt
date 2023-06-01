package com.example.banlancegameex.contentsList

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.banlancegameex.R
import com.example.banlancegameex.databinding.ActivityGameMakeBinding
import com.example.banlancegameex.utils.FBAuth
import com.example.banlancegameex.utils.FBRef
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference

class GameMakeActivity : AppCompatActivity() {

    private lateinit var binding : ActivityGameMakeBinding
    private lateinit var _gameTag : String
    // error 메세지의 내용을 담을 문자열
    private lateinit var errormesage : String
    var key : String = ""

    private val TAG = GameMakeActivity::class.java

    private fun setSpinnergameTag(){
        val job = resources.getStringArray(R.array.spinner_gameTag)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, job)
        binding.tagSpin.adapter = adapter
    }

    private fun setupSpinnerHandler() {

        binding.tagSpin.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // 드롭 다운 버튼 클릭 시 이벤트
                _gameTag = binding.tagSpin.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                // 아무 것도 선택 하지 않았을 시 이벤트
                errormesage = "태그를 입력해주세요."
            }
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this,R.layout.activity_game_make)

        setSpinnergameTag()
        setupSpinnerHandler()

        binding.writeBtn.setOnClickListener{
            val title = binding.gameTitleArea.text.toString()
            val option1 = binding.option1Area.text.toString()
            val option1Sub = binding.option1SubArea.text.toString()
            val option2 = binding.option2Area.text.toString()
            val option2Sub = binding.option2SubArea.text.toString()

            val uid = FBAuth.getuid()
            val time = FBAuth.getTime()


            // 게시물 생성 후 DB 저장 시 만들어지는 Key 값을 사용하여 해당 게시물의 통계 모델도 같이 생성
            FBRef.postRef
                .push()
                .setValue(ContentModel(title, option1, option1Sub, option2, option2Sub, 0, _gameTag, uid, time),
                    object : DatabaseReference.CompletionListener{
                    override fun onComplete(error: DatabaseError?, ref: DatabaseReference) {
                        if(error == null) {
                            key = ref.key.toString()
                            Log.d("키 값 테스트", key)
                            FBRef.countRef
                                .child(key)
                                .setValue(CountModel())
                        }
                    }
                })

            Toast.makeText(this,"게시글 입력 완료", Toast.LENGTH_SHORT).show()

            finish()

        }



    }
}