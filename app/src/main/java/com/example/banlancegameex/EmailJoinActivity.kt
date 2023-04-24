package com.example.banlancegameex

import android.content.DialogInterface
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
import androidx.appcompat.app.AlertDialog
import com.example.banlancegameex.databinding.ActivityEmailJoinBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class EmailJoinActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEmailJoinBinding
    private lateinit var gender : String
    private lateinit var _agerange : String
    private lateinit var _job : String
    // error 메세지의 내용을 담을 문자열
    private lateinit var errormesage : String
    // 사용자의 uid를 가져오기 위한 firebase인증 인스턴스
    private lateinit var auth: FirebaseAuth
    // 회원가입이 정상적으로 완료 시 true, 아니면 false
    var gotomain = true

    val _database = Firebase.database.reference
    private var userstate = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmailJoinBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        gender = "남성"

        setSpinnerJob()
        //    setupSpinnerAgeRange()
        setupSpinnerHandler()


        binding.radioGender.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.radio_btn_man -> gender = "남성"

                R.id.radio_btn_woman -> gender = "여성"
            }
        }

        binding.joinBtn.setOnClickListener {
            // Toast.makeText(this, "아직 테스트 중입니다.", Toast.LENGTH_SHORT).show()

            // firebase realtime database 연결
            val database = Firebase.database
            val myRef = database.getReference("userdata")

            val _nickname = binding.nickname.text.toString()
            val email = binding.textEmail.text.toString().trim()
            val password = binding.textPassword.text.toString().trim()
            val check_password = binding.textCheckPassword.text.toString().trim()

            _database.child("userdata").orderByChild("nickname").equalTo(_nickname)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            gotomain = false
                            errormesage = "중복된 닉네임입니다."
                        }
                        else {
                            gotomain = true
                            if(_nickname.isEmpty()){
                                gotomain = false
                                errormesage = "닉네임을 입력해주세요."
                            }
                            if(_agerange ==""){
                                gotomain = false
                                errormesage = "생년월일을 입력해주세요."
                            }
                            if(email.isEmpty()||password.isEmpty()||check_password.isEmpty())
                            {
                                gotomain = false
                                errormesage = "이메일과 비밀번호를 입력해주세요."
                            }
                        }
                        pushToDatabase()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        gotomain = false
                        errormesage = "데이터베이스 연동 중 문제가 발생하였습니다."
                        pushToDatabase()
                    }
                })
        }

        binding.emailVerifyBtn.setOnClickListener {
            val email = binding.textEmail.text.toString().trim()
            val password = binding.textPassword.text.toString().trim()
            val check_password = binding.textCheckPassword.text.toString().trim()
            if(email.isEmpty()||password.isEmpty()||check_password.isEmpty())
            {
                Toast.makeText(this,"이메일과 비밀번호를 입력해주세요.",Toast.LENGTH_SHORT).show()
            }
            else {
                emailSignin(email, password, check_password)
            }
        }

        binding.birthdayInputBtn.setOnClickListener {
            val iYear : Int = binding.datePicker.year
            val iMonth : Int = binding.datePicker.month+1
            val iDay : Int = binding.datePicker.dayOfMonth
            val birthDate = (iYear * 10000 + iMonth * 100 + iDay).toString()

            val builder = AlertDialog.Builder(this)
                .setTitle("생년월일")
                .setMessage("생일이 ${iYear}년 ${iMonth}월 ${iDay}일이 맞습니까?")
                .setPositiveButton("네",DialogInterface.OnClickListener{dialog, which ->
                    Toast.makeText(this,"생년월일 입력 완료",Toast.LENGTH_SHORT).show()
                    val _age = getAmericanAge(birthDate)
                    when(_age/10) {
                        0 -> _agerange = "10대 미만"
                        1 -> _agerange = "10대"
                        2 -> _agerange = "20대"
                        3 -> _agerange = "30대"
                        4 -> _agerange = "40대"
                        5 -> _agerange = "50대"
                        else -> _agerange = "60대 이상"
                    }
                })
                .setNegativeButton("아니오",DialogInterface.OnClickListener { dialog, which ->
                    Toast.makeText(this,"생년월일을 다시 설정해주세요",Toast.LENGTH_SHORT).show()
                })
            builder.show()
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

    private fun emailSignin(email:String,password:String,check_password:String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){task->
                if (task.isSuccessful) {
                    auth.currentUser?.sendEmailVerification()
                        ?.addOnCompleteListener { sendTask ->
                            if (sendTask.isSuccessful) {
                                Log.d("TAG", "Email sent.")
                                Toast.makeText(this, "${email}로 인증 메일 전송.", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this, "메일 발송 실패", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
                else if(task.exception?.message.isNullOrEmpty()){
                    Toast.makeText(this,"이메일과 비밀번호를 확인해주세요.",Toast.LENGTH_SHORT).show()
                }
                else if(password.length < 6){
                    Toast.makeText(this,"비밀 번호를 최소 6자리 이상 입력해주세요.",Toast.LENGTH_SHORT).show()
                }
                else if(!(password.equals(check_password))){
                        Toast.makeText(this,"두 비밀 번호를 일치시켜주세요.",Toast.LENGTH_SHORT).show()
                    }
                else {
                    Toast.makeText(this,"이미 가입된 이메일이 존재합니다.",Toast.LENGTH_SHORT).show()
                }

            }
    }

    private fun setSpinnerJob(){
        val job = resources.getStringArray(R.array.spinner_job)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, job)
        binding.jobSpin.adapter = adapter
    }

    private fun setupSpinnerHandler() {
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

    private fun pushToDatabase(){
        val database = Firebase.database
        val myRef = database.getReference("userdata")
        val _nickname = binding.nickname.text.toString().trim()

        if(!gotomain){
            Toast.makeText(this, errormesage, Toast.LENGTH_SHORT).show()
        }
        else {
            // database에 userdata 입력
            myRef.push().setValue(
                UserDataModel(auth.currentUser?.email.toString(), _nickname, gender, _agerange, _job)
            )
            Toast.makeText(this,"회원가입을 완료했습니다.",Toast.LENGTH_SHORT).show()
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }
}