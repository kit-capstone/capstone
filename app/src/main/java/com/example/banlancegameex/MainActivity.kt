package com.example.banlancegameex

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.banlancegameex.databinding.ActivityMainBinding
import com.google.android.gms.tasks.RuntimeExecutionException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient


class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding
    var userstate = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        // 이후 계정 정보 삭제 시 이용 가능
//        UserApiClient.instance.unlink { error ->
//            if (error != null) {
//                Toast.makeText(this@MainActivity, "연결끊기 실패 sdk에 토큰이 남아있습니다.", Toast.LENGTH_LONG).show()
//            }
//            else {
//                Toast.makeText(this@MainActivity, "연결끊기 성공 토큰이 삭제되었습니다.", Toast.LENGTH_LONG).show()
//            }
//            auth.currentUser?.delete()
//        }

        binding.kakaoLoginButton.setOnClickListener {
            kakaoLogin()
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