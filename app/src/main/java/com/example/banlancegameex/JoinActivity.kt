package com.example.banlancegameex

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.banlancegameex.databinding.ActivityJoinBinding
import com.kakao.usermgmt.StringSet.gender

class JoinActivity : AppCompatActivity() {
    lateinit var binding: ActivityJoinBinding
    lateinit var gender : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJoinBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
            Toast.makeText(this, "아직 테스트 중입니다.", Toast.LENGTH_SHORT).show()
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
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                // 아무 것도 선택 하지 않았을 시 이벤트
            }
        }

        binding.jobSpin.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // 드롭 다운 버튼 클릭 시 이벤트
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                // 아무 것도 선택 하지 않았을 시 이벤트
            }
        }
    }
}