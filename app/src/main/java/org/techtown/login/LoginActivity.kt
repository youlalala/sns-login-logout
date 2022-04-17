package org.techtown.login

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import org.techtown.login.databinding.ActivityLoginBinding
import org.techtown.login.network.ApiWrapper
import org.techtown.login.network.LoginModel


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                //Login Fail
                Log.d("sss", "로그인 실패", error)
            } else if (token != null) {
                //Login Success

                MySharedPreferences.setToken(this, token.accessToken)
                MySharedPreferences.setMethod(this, "kakao")

                val userInfo = LoginModel(
                    token= MySharedPreferences.getToken(this)
                )

                Log.i("sss","userInfo : "+userInfo)

                ApiWrapper.postToken(userInfo){
                }

                Log.i("SSS","로그인성공")
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }

        //kakao Login button
        binding.kakaoLoginBtn.setOnClickListener {
            UserApiClient.instance.run {
                if (isKakaoTalkLoginAvailable(this@LoginActivity)) {
                    //카카오톡이 있을 경우
                    loginWithKakaoTalk(this@LoginActivity, callback = callback)
                } else {
                    //카카오ㅗㄱ이 없을 경우 카카오 계정으로 로그인하기
                    loginWithKakaoAccount(this@LoginActivity, callback = callback)
                }
            }
        }

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.server_client_id))
            .requestEmail()
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        val mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        binding.googleLoginBtn.setOnClickListener {
            val intent = mGoogleSignInClient.signInIntent

            MySharedPreferences.setMethod(this,"google")
            startActivityForResult(intent, 1)
        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == 1) {
            try {
                // The Task returned from this call is always completed, no need to attach
                // a listener.
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account = task.getResult(ApiException::class.java)
                Log.i("SSS","account"+account.toString())
                MySharedPreferences.setToken(this, account.idToken!!)
                MySharedPreferences.setMethod(this, "google")
                val userInfo = LoginModel(
                    token= MySharedPreferences.getToken(this)
                )

                Log.i("sss","userInfo : "+userInfo)

                ApiWrapper.postToken(userInfo){
                }
                Log.i("SSS","구글 로그인 성공")
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }catch(e: ApiException){
                Log.i("SSS","구글 로그인 실패:"+e)
            }

        }
    }



//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
//        if (requestCode == 1) {
//            // The Task returned from this call is always completed, no need to attach
//            // a listener.
//            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
//            handleSignInResult(task)
//        }
//    }
//    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
//        try {
//            val account = completedTask.getResult(ApiException::class.java)
//            val name= account.givenName
//            val email = account.email
//            Log.d("sss", name.toString())
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
//
//        } catch (e: ApiException) {
//            // The ApiException status code indicates the detailed failure reason.
//            // Please refer to the GoogleSignInStatusCodes class reference for more information.
//            Log.i("SSS", "signInResult:failed code=" + e.statusCode)
//        }
//    }
//
//    override fun onStart() {
//        // Check for existing Google Sign In account, if the user is already signed in
//        // the GoogleSignInAccount will be non-null.
//        super.onStart()
//        val account = GoogleSignIn.getLastSignedInAccount(this)
//
//        if(account!=null) {
//            Log.d("sss", "already signed in")
//        }else{
//            Log.d("sss", "no account")
//        }
//    }

}



