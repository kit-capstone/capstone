package com.example.banlancegameex

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.banlancegameex.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.RuntimeExecutionException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import org.koin.android.ext.koin.ERROR_MSG


class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding
    private var userstate = 0

    private lateinit var googleSignInClient : GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()


        // 이후 계정 정보 삭제 시 이용 가능
        // auth.currentUser?.delete()
        Log.d("테스트입", auth.currentUser?.uid?:"0")
        val Userdata = Firebase.auth.currentUser?:"0"

        if(Userdata == "0"){
            UserApiClient.instance.unlink { error ->
                if (error != null) {
                    Log.w(ERROR_MSG, error)
                    Toast.makeText(this@MainActivity, "연결끊기 실패 sdk에 토큰이 남아있습니다.", Toast.LENGTH_LONG).show()
                }
                else {
                    Toast.makeText(this@MainActivity, "연결끊기 성공 토큰이 삭제되었습니다.", Toast.LENGTH_LONG).show()
                }
            }
        }


        binding.kakaoLoginButton.setOnClickListener {
            kakaoLogin()
        }

        binding.googleLoginButton.setOnClickListener {
            googleLogin()
        }

        binding.emailSigninButton.setOnClickListener {
            val email = binding.textEmail.text.toString().trim()
            val password = binding.textPassword.text.toString().trim()
            if(email.isNullOrEmpty()||password.isNullOrEmpty())
            {
                Toast.makeText(this,"이메일과 비밀번호를 입력해주세요.",Toast.LENGTH_SHORT).show()
            }
            else {
                emailSignin(email, password)
            }
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
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser?.uid?:"0"
        if(currentUser != "0") {
            updateUI(currentUser)
        }
    }

    private fun updateUI(user: String?) {
        val intent = Intent(this,TestActivity::class.java)
        startActivity(intent)

    }
    private fun emailSignin(email:String,password:String) {

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){task->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(this,"회원가입이 완료되었습니다.",Toast.LENGTH_SHORT).show()

                    if(auth.currentUser!=null){
                        Toast.makeText(this,"회원가입이 완료되었습니다.",Toast.LENGTH_SHORT).show()
                    }
                }
                else if(task.exception?.message.isNullOrEmpty()){
                    Toast.makeText(this,"이메일과 비밀번호를 확인해주세요.",Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(this,"이미 가입된 이메일이 존재합니다.",Toast.LENGTH_SHORT).show()
                }
            }

    }
    private fun emailLogin(email:String,password:String) {

        auth.signInWithEmailAndPassword(email, password) // 로그인
            .addOnCompleteListener{task->
                if (task.isSuccessful) {
                    Toast.makeText(this, "이메일 로그인에 성공했습니다.", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, TestActivity::class.java)
                    startActivity(intent)
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
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(this, "구글 로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show()
                    val user = auth.currentUser
                    val intent = Intent(this, TestActivity::class.java)
                    startActivity(intent)
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
                    // firebase auth에 해당 유저의 uid가 존재 시
                    if(auth.currentUser?.uid != null){
                        userstate = 0
                        getCustomToken(token.accessToken)
                        Log.d("상태 확인", userstate.toString())
                    }
                    // firebase auth에 해당 유저의 uid가 없을 시
                    else{
                        userstate = 1
                        getCustomToken(token.accessToken)
                        Log.d("상태 확인", userstate.toString())
                    }
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
                    Toast.makeText(this@MainActivity, "카카오톡 로그인에 실패하였습니다.", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun firebaseAuthWithKakao(customToken: String){
        auth.signInWithCustomToken(customToken).addOnCompleteListener{ result ->
            if (result.isSuccessful){

                // 인증 성공 후 로직 작성
                // firebase auth에 해당 유저의 uid가 존재 시
                if(userstate == 0){
                    Toast.makeText(this@MainActivity, "카카오톡 로그인에 성공하였습니다.", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, TestActivity::class.java)
                    startActivity(intent)
                }
                // firebase auth에 해당 유저의 uid가 없을 시
                // 이후 Toast 메세지가 아닌 회원가입 페이지로 이동하는 코드 작성 필요
                else if(userstate == 1){
                    Toast.makeText(this@MainActivity, "sns 회원가입을 진행합니다.", Toast.LENGTH_LONG).show()
                }
            } else {
                // 실패 후 로직 작성
                Toast.makeText(this@MainActivity, "DB연동에 실패하였습니다.", Toast.LENGTH_LONG).show()
            }
        }
    }
}