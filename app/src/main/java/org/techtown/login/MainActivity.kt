package org.techtown.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.core.net.toUri
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.KakaoSdkError
import com.kakao.sdk.user.UserApiClient
import org.techtown.login.MySharedPreferences.clearTOKEN
import org.techtown.login.databinding.ActivityMainBinding
import retrofit2.http.Url
import java.net.URI
import java.net.URL

//로그인 성공시 보이는 화면
class MainActivity : AppCompatActivity() {
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        val method = MySharedPreferences.getMethod(this)
        Log.i("sss", "method "+method)
        if (method == "kakao") {
            val token = MySharedPreferences.getToken(this)
            // 사용자 정보 요청 (기본)
            if(token != null){
                UserApiClient.instance.me { user, error ->
                    if (error != null) {
                        Log.d("sss", "사용자 정보 요청 실패", error)
                        val intent= Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                    }
                    else if (user != null) {
                        Log.d(
                            "sss", "사용자 정보 요청 성공" +
                                    "\n회원번호: ${user.id}" +
                                    "\n이메일: ${user.kakaoAccount?.email}" +
                                    "\n닉네임: ${user.kakaoAccount?.profile?.nickname}" +
                                    "\n프로필사진: ${user.kakaoAccount?.profile?.thumbnailImageUrl}"
                        )
                        binding.name.text = user.kakaoAccount?.profile?.nickname
                    }
                }
            }
        } else if (method == "google") {
            // Configure sign-in to request the user's ID, email address, and basic
            // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build()

            // Build a GoogleSignInClient with the options specified by gso.
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

            val acct = GoogleSignIn.getLastSignedInAccount(this)
            if(acct != null)
                binding.email.text = acct?.email
            else{
                Log.d("sss", "사용자 정보 요청 실패")
                val intent= Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        } else {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.logoutBtn.setOnClickListener {
            clearTOKEN(this)
            if (method == "kakao") {
                kakaoLogout()
            } else if (method == "google") {
                googleLogout()
            }
        }
    }


    private fun googleLogout(){
        mGoogleSignInClient.signOut()
            .addOnCompleteListener(this, OnCompleteListener<Void?> {
                val intent= Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            })

    }
    private fun kakaoLogout(){
        UserApiClient.instance.logout{
            val intent= Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }



}





