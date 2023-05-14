package com.example.banlancegameex

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.banlancegameex.databinding.ActivityUserDataUpdateBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.Calendar

class UserDataUpdateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserDataUpdateBinding
    private lateinit var auth : FirebaseAuth

    private var calendar = Calendar.getInstance()
    private var year = calendar.get(Calendar.YEAR)
    private var month = calendar.get(Calendar.MONTH)
    private var day = calendar.get(Calendar.DAY_OF_MONTH)

    val database = Firebase.database.reference

    @SuppressLint("SetTextI18n", "SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUserDataUpdateBinding.inflate(layoutInflater)
        auth = Firebase.auth

        setContentView(binding.root)




        binding.birthdayInputBtn.setOnClickListener {
            val datePickerDialog = DatePickerDialog(this,AlertDialog.THEME_HOLO_LIGHT,{ _ , year, month, day ->
                binding.textBirthday.text =
                    year.toString() + "." + (month+1).toString() + "." +day.toString()}
                , year,month,day)
                datePickerDialog.show()
            }
        binding.passwordResentBtn.setOnClickListener {
            val intent = Intent(this,ForgotPasswordActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}