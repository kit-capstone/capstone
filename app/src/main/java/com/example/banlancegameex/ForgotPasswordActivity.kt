package com.example.banlancegameex

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.banlancegameex.databinding.ActivityForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding : ActivityForgotPasswordBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)

        auth = Firebase.auth

        setContentView(binding.root)

        binding.passwordResentBtn.setOnClickListener {
            val email = binding.textEmail.text.toString()
            if(CheckEmail()){
                auth.sendPasswordResetEmail(email).addOnCompleteListener {
                    if(it.isSuccessful){
                        Toast.makeText(this,"이메일 전송",Toast.LENGTH_SHORT).show()
                        val intent = Intent(this,MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

                    else if(it.exception?.message != null) {
                        Toast.makeText(this, "등록되지 않은 이메일 입니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }

    }

    private fun CheckEmail() : Boolean {
        val email = binding.textEmail.text.toString()
        if(email == ""){
            Toast.makeText(this,"이메일을 입력해주세요",Toast.LENGTH_SHORT).show()
            return false
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this,"이메일 형식을 확인해주세요",Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this,MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)

    }
}