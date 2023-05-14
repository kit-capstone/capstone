package com.example.banlancegameex

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.banlancegameex.databinding.ActivityUserDataUpdateBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class UserDataUpdateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserDataUpdateBinding
    private lateinit var auth : FirebaseAuth

    private lateinit var _gender : String
    private lateinit var _agerange : String
    private lateinit var _job : String
    private lateinit var _nickname : String
    private lateinit var _locate : String
    val database = Firebase.database.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUserDataUpdateBinding.inflate(layoutInflater)
        auth = Firebase.auth

        setContentView(binding.root)

        val user = auth.currentUser

        database.child("userdata").orderByChild("email").equalTo(auth.currentUser?.email.toString())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(datamodel in snapshot.children){

                        _nickname = datamodel.getValue(UserDataModel::class.java)?.nickname?:"nothing"
                        _job = datamodel.getValue(UserDataModel::class.java)?.job?:"nothing"
                        _gender = datamodel.getValue(UserDataModel::class.java)?.gender?:"nothing"
                        _agerange = datamodel.getValue(UserDataModel::class.java)?.agerange?:"nothing"
                        _locate = datamodel.getValue(UserDataModel::class.java)?.locate?:"nothing"
                    }

                    binding.textEmail.text = auth.currentUser?.email.toString()
                    binding.textNickname.text = _nickname
                    binding.textJob.text = _job
                    binding.textBirthday.text = _agerange
                    binding.textGender.text = _gender
                    binding.textLocate.text = _locate
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(applicationContext, "DB연동에 실패하였습니다.", Toast.LENGTH_LONG).show()
                }
            })

        binding.passwordResentBtn.setOnClickListener {
            val intent = Intent(this,ForgotPasswordActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        binding.logoutBtn.setOnClickListener{
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        binding.updateBtn.setOnClickListener {
            val intent = Intent(this, UserDataRegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

}