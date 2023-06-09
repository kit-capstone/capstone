package com.abtwin.banlancegameex

import android.Manifest.permission
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.work.*
import com.abtwin.banlancegameex.R.string.default_web_client_id
import com.abtwin.banlancegameex.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.RuntimeExecutionException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk.keyHash
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001
        const val REQUEST_CODE_PERMISSIONS = 1001
    }

    private lateinit var activityResultLauncher: ActivityResultLauncher<Array<String>>

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding
    private var userstate = 0

    val BACKGROUND_PERMISSION_REQUEST = 100

    private lateinit var googleSignInClient : GoogleSignInClient

    val database = Firebase.database.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        activityResultLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (it.all { permission -> permission.value == true }) {

            } else {
                Toast.makeText(this, "권한 거부", Toast.LENGTH_SHORT).show()
            }
        }

        requestPermission()

        if(auth.currentUser?.email != null){
            val intent = Intent(this, com.abtwin.banlancegameex.ContentsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }


        binding.kakaoLoginButton.setOnClickListener {
            kakaoLogin()
        }

        binding.googleLoginButton.setOnClickListener {
            googleLogin()
        }

        binding.emailSigninButton.setOnClickListener {
            val intent = Intent(this, com.abtwin.banlancegameex.EmailJoinActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.emailLoginButton.setOnClickListener {
            val email = binding.textEmail.text.toString().trim()
            val password = binding.textPassword.text.toString().trim()
            if(email.isNullOrEmpty()||password.isNullOrEmpty())
            {
                Toast.makeText(this,"이메일과 비밀번호를 입력해주세요.",Toast.LENGTH_SHORT).show()
            }
            else {
                emailLogin(email, password)
            }
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.forgotPasswordTv.setOnClickListener {
            val intent = Intent(this,ForgotPasswordActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)

        }
    }
    override fun onDestroy() {
        super.onDestroy()
    }

    private fun emailLogin(email:String,password:String) {
            auth.signInWithEmailAndPassword(email, password) // 로그인
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if (userstate == 0 && auth.currentUser?.isEmailVerified == true) {
                            Toast.makeText(this, "이메일 로그인에 성공했습니다.", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, com.abtwin.banlancegameex.ContentsActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }
                        else if(auth.currentUser?.isEmailVerified != true){
                            Toast.makeText(this, "이메일 인증 메시지를 확인해주세요.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "이메일과 비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show()
                    }
            }
    }

    private fun googleLogin() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("사용자 정보 확인", auth.currentUser?.uid.toString())
                    // Sign in success, update UI with the signed-in user's information
                    // Toast.makeText(this, "구글 로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show()
                    database.child("userdata").orderByChild("email").equalTo(auth.currentUser?.email.toString())
                        .addListenerForSingleValueEvent(object : ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if(snapshot.exists()) {
                                    // 로그인한 유저의 uid가 db에 존재할 경우
                                    userstate = 0
                                }
                                else {
                                    // 로그인한 유저의 uid가 db에 없는 경우
                                    userstate = 1
                                }
                                moveIntent()
                            }

                            override fun onCancelled(error: DatabaseError) {
                                // 오류 처리 수행
                            }
                        })
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(this, "구글 로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                }
            }
    }
    private fun kakaoLogin() {
        // 카카오톡 어플이 있으면 카카오톡으로 로그인, 없으면 카카오 계정으로 로그인
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
            UserApiClient.instance.loginWithKakaoTalk(this) {token, error ->
                if (error != null){
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled){
                        return@loginWithKakaoTalk
                    }
                    loginWithKakaoAccount(this)
                } else if(token != null) {
                    getCustomToken(token.accessToken)
                }
            }
        } else {
            loginWithKakaoAccount(this)
        }
    }

    //카카오 계정을 로그인
    private fun loginWithKakaoAccount(context: Context){
        UserApiClient.instance.loginWithKakaoAccount(context) {token: OAuthToken?, error:Throwable? ->
            if (token != null) {
                getCustomToken(token.accessToken)
            }
        }
    }

    private fun getCustomToken(accessToken: String) {
        val functions: FirebaseFunctions = Firebase.functions("asia-northeast3")

        val data = hashMapOf(
            "token" to accessToken
        )

        Toast.makeText(this, "로그인에 5초 정도의 시간이 소요됩니다.", Toast.LENGTH_LONG).show()

        functions
                //firebase에 배포한 "kakaoCustomAuth" 함수 호출
            .getHttpsCallable("kakaoCustomAuth")
            .call(data)
            .addOnCompleteListener{task ->
                try {
                    // firebase functions의 호출 성공 시
                    val result = task.result?.data as HashMap<*, *>
                    var mKey: String? = null
                    for(key in result.keys){
                        mKey = key.toString()
                    }

                    val customToken = result[mKey!!].toString()

                    //호출 성공으로 받은 커스텀 토큰을 firebase 인증 기능 사용
                    firebaseAuthWithKakao(customToken)
                } catch (e: RuntimeExecutionException){
                    // 토스트 메세지로 호출 실패 알리기
                    Toast.makeText(this, "카카오톡 로그인에 실패하였습니다.", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun firebaseAuthWithKakao(customToken: String){
        auth.signInWithCustomToken(customToken).addOnCompleteListener{ result ->
            if (result.isSuccessful){
                // 인증 성공 후 로직 작성
                Log.d("사용자 정보 확인", auth.currentUser?.email.toString())
                database.child("userdata").orderByChild("email").equalTo(auth.currentUser?.email.toString())
                    .addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if(snapshot.exists()){
                                // firebase database에 해당 유저의 email이 존재 시
                                userstate = 0
                                Log.d("사용자 상태 확인", userstate.toString())
                            }
                            else {
                                // firebase database에 해당 유저의 email이 없을 시
                                userstate = 1
                                Log.d("사용자 상태 확인", userstate.toString())
                            }
                            Log.d("사용자 상태 확인", userstate.toString())
                            moveIntent()
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }
                    })
            } else {
                // 실패 후 로직 작성
                Toast.makeText(this, "DB연동에 실패하였습니다.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun moveIntent(){
        if(userstate == 0){
            Toast.makeText(this, "로그인에 성공하였습니다.", Toast.LENGTH_LONG).show()
            val intent = Intent(this, com.abtwin.banlancegameex.ContentsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        // firebase auth에 해당 유저의 uid가 없을 시
        // 이후 Toast 메세지가 아닌 회원가입 페이지로 이동하는 코드 작성 필요
        else if(userstate == 1){
            Toast.makeText(this, "회원가입 페이지로 이동합니다.", Toast.LENGTH_LONG).show()
            val intent = Intent(this, JoinActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){
            BACKGROUND_PERMISSION_REQUEST -> {
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Background Permission Granted", Toast.LENGTH_SHORT).show()
                    // Go Main Function
                }else{
                    Toast.makeText(this, "BAckground Permission Denied", Toast.LENGTH_SHORT).show()
                    // Finish() or Show Guidance on the need for permission
                }
            }
        }
    }

    private fun backgroundPermission(){
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            ), 2)
    }

    private fun permissionDialog(context : Context){
        var builder = AlertDialog.Builder(context)
        builder.setTitle("백그라운드 위치 권한을 위해 항상 허용으로 설정해주세요.")

        var listener = DialogInterface.OnClickListener { _, p1 ->
            when (p1) {
                DialogInterface.BUTTON_POSITIVE ->
                    backgroundPermission()
            }
        }
        builder.setPositiveButton("네", listener)
        builder.setNegativeButton("아니오", null)

        builder.show()
    }

    private fun requestPermission(){
        // 이미 권한이 있으면 그냥 리턴
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return
        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    ), 1)
                permissionDialog(this)
            }
            // API 23 미만 버전에서는 ACCESS_BACKGROUND_LOCATION X
            else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    ), 1)
            }
        }
    }
}