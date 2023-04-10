package com.example.banlancegameex

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.banlancegameex.databinding.ActivityJoinBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class JoinActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJoinBinding
    private lateinit var gender : String
    private lateinit var _agerange : String
    private lateinit var _job : String
    private lateinit var errormesage : String
    private lateinit var auth: FirebaseAuth
    var gotomain = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJoinBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        gender = "남성"

        setSpinnerJob()
        setupSpinnerAgeRange()
        setupSpinnerHandler()

        binding.radioGender.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.radio_btn_man -> gender = "남성"

                R.id.radio_btn_woman -> gender = "여성"
            }
        }

        binding.joinBtn.setOnClickListener {
            // Toast.makeText(this, "아직 테스트 중입니다.", Toast.LENGTH_SHORT).show()
            val _nickname = binding.nickname.text.toString().trim()
            if(_nickname.isNullOrEmpty()){
                gotomain = false
                errormesage = "닉네임을 입력해주세요."
            }
            if(gotomain == false){
                Toast.makeText(this, errormesage, Toast.LENGTH_SHORT).show()
            }
            else {
                val database = Firebase.database
                val myRef = database.getReference("userdata")

                myRef.push().setValue(
                    UserDataModel(auth.currentUser?.uid.toString(), _nickname, gender, _agerange, _job)
                )
                Toast.makeText(this, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
            }
        }
    }

    private fun setupSpinnerAgeRange(){
        val age_range = resources.getStringArray(R.array.spinner_age_range)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, age_range)
        binding.ageRangeSpin.adapter = adapter
    }

    private fun setSpinnerJob(){
        val job = resources.getStringArray(R.array.spinner_job)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, job)
        binding.jobSpin.adapter = adapter
    }

    private fun setupSpinnerHandler() {
        binding.ageRangeSpin.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // 드롭 다운 버튼 클릭 시 이벤트
                _agerange = binding.ageRangeSpin.getItemAtPosition(position).toString()
                gotomain = true
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                // 아무 것도 선택 하지 않았을 시 이벤트
                gotomain = false
                errormesage = "나이대를 입력해주세요."
            }
        }

        binding.jobSpin.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // 드롭 다운 버튼 클릭 시 이벤트
                _job = binding.jobSpin.getItemAtPosition(position).toString()
                gotomain = true
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                // 아무 것도 선택 하지 않았을 시 이벤트
                gotomain = false
                errormesage = "직업을 입력해주세요."
            }
        }
    }
}