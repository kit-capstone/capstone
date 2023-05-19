package com.example.banlancegameex

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.banlancegameex.databinding.ActivityUserDataRegisterBinding
import com.example.banlancegameex.databinding.ActivityUserDataUpdateBinding
import com.example.banlancegameex.utils.FBAuth
import com.google.apphosting.datastore.testing.DatastoreTestTrace.FirestoreV1Action.UpdateDocumentOrBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.kakao.usermgmt.StringSet.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.HashMap

class UserDataRegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserDataRegisterBinding
    private lateinit var auth : FirebaseAuth

    private var calendar = Calendar.getInstance()
    private var year = calendar.get(Calendar.YEAR)
    private var month = calendar.get(Calendar.MONTH)
    private var day = calendar.get(Calendar.DAY_OF_MONTH)
    private lateinit var errormessage : String
    private lateinit var _gender : String
    private lateinit var _agerange : String
    private lateinit var _job : String
    private lateinit var _nickname : String
    private lateinit var _locate : String
    var user_data = ""
    val database = Firebase.database.reference

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUserDataRegisterBinding.inflate(layoutInflater)
        auth = Firebase.auth

        setContentView(binding.root)

        setSpinnerJob()
        setSpinnerLocate()
        setupSpinnerHandler()

        database.child("userdata").orderByChild("email").equalTo(auth.currentUser?.email.toString())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(datamodel in snapshot.children){

                        _nickname = datamodel.getValue(UserDataModel::class.java)?.nickname?:"nothing"
                        _gender = datamodel.getValue(UserDataModel::class.java)?.gender?:"nothing"
                        _job = datamodel.getValue(UserDataModel::class.java)?.job?:"nothing"
                        _locate = datamodel.getValue(UserDataModel::class.java)?.locate?:"nothing"
                        _agerange = datamodel.getValue(UserDataModel::class.java)?.agerange?:"nothing"
                    }

                    binding.textNickname.text = _nickname
                    binding.textBirthday.text = _agerange
                    binding.textEmail.text = auth.currentUser?.email.toString()

                    if(_gender == "남성"){
                        binding.radioBtnMan.isChecked = true
                    }
                    else {
                        binding.radioBtnWoman.isChecked = true
                    }

                    if(_job == "학생"){
                        binding.jobSpin.setSelection(0)
                    }
                    else if(_job == "직장인"){
                        binding.jobSpin.setSelection(1)
                    }
                    else if(_job == "무직"){
                        binding.jobSpin.setSelection(2)
                    }

                    if(_locate == "경기도"){
                        binding.locateSpin.setSelection(0)
                    }
                    else if(_locate == "강원도"){
                        binding.locateSpin.setSelection(1)
                    }
                    else if(_locate == "충청북도"){
                        binding.locateSpin.setSelection(2)
                    }
                    else if(_locate == "충청남도"){
                        binding.locateSpin.setSelection(3)
                    }
                    else if(_locate == "경상북도"){
                        binding.locateSpin.setSelection(4)
                    }
                    else if(_locate == "경상남도"){
                        binding.locateSpin.setSelection(5)
                    }
                    else if(_locate == "전라북도"){
                        binding.locateSpin.setSelection(6)
                    }
                    else if(_locate == "전라북도"){
                        binding.locateSpin.setSelection(7)
                    }
                    else if(_locate == "제주특별자치도"){
                        binding.locateSpin.setSelection(8)
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(applicationContext, "DB연동에 실패하였습니다.", Toast.LENGTH_LONG).show()
                }
            })

        binding.radioGender.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.radio_btn_man -> _gender = "남성"

                R.id.radio_btn_woman -> _gender = "여성"
            }
        }

        binding.birthdayInputBtn.setBackgroundResource(R.drawable.ripple_background)
        binding.birthdayInputBtn.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                this,
                AlertDialog.THEME_HOLO_LIGHT,
                { _, year, month, day ->
                    val birthDate = (year * 10000 + (month + 1) * 100 + day).toString()
                    val _age = getAmericanAge(birthDate)
                    _agerange = when (_age / 10) {
                        0 -> "10대 미만"
                        1 -> "10대"
                        2 -> "20대"
                        3 -> "30대"
                        4 -> "40대"
                        else -> "50대 이상"
                    }
                    binding.textBirthday.text = _agerange
                },
                year,
                month,
                day
            )
            datePickerDialog.show()
        }

        binding.registerBtn.setOnClickListener {
            var gotomain = true
            val dbRef = FirebaseDatabase.getInstance().getReference("userdata")
            val query = dbRef.orderByChild("email").equalTo(auth.currentUser?.email.toString())

                query.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                           gotomain = true
                        }

                        if(_agerange ==""){
                            gotomain = false
                            errormessage = "생년월일을 입력해주세요."
                            sendErrorMessage()
                        }

                        if(gotomain) {
                            UpdateToDatabase()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        gotomain = false
                        errormessage = "데이터베이스 연동 중 문제가 발생하였습니다."
                        sendErrorMessage()
                    }
                })
        }
    }

    private fun setSpinnerJob(){
        val job = resources.getStringArray(R.array.spinner_job)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, job)
        binding.jobSpin.adapter = adapter
    }

    private fun setSpinnerLocate(){
        val locate = resources.getStringArray(R.array.spinner_locate)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, locate)
        binding.locateSpin.adapter = adapter
    }

    private fun setupSpinnerHandler() {
        binding.jobSpin.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // 드롭 다운 버튼 클릭 시 이벤트
                _job = binding.jobSpin.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                // 아무 것도 선택 하지 않았을 시 이벤트
                _job = "학생"
            }
        }

        binding.locateSpin.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // 드롭 다운 버튼 클릭 시 이벤트
                _locate = binding.locateSpin.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                // 아무 것도 선택 하지 않았을 시 이벤트
                _locate = "경기도"
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getAmericanAge(birthDate: String) : Int{

        val now = LocalDate.now();
        val parsedBirthDate = LocalDate.parse(birthDate, DateTimeFormatter.ofPattern("yyyyMMdd"));

        var americanAge = now.minusYears(parsedBirthDate.getYear().toLong()).getYear(); // (1)

        if (parsedBirthDate.plusYears(americanAge.toLong()).isAfter(now)) {
            americanAge -= 1;
        }
        return americanAge;
    }

    private fun UpdateToDatabase(){
        val database = Firebase.database
        val myRef = database.getReference("userdata")

        // database에 userdata 입력
        myRef.child(FBAuth.getuid()).setValue(
            UserDataModel(auth.currentUser?.email.toString(), _nickname, _gender, _agerange, _job, _locate)
        )
        Toast.makeText(this, "사용자 정보 변경 완료", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, UserDataUpdateActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
    private fun sendErrorMessage() {
        Toast.makeText(this, errormessage, Toast.LENGTH_SHORT).show()
    }
}



