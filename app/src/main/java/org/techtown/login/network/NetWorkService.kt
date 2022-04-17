package org.techtown.login.network

import retrofit2.http.*
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetWorkService{

    //private const val BASE_URL ="http://172.30.1.17:8080"
    private const val BASE_URL ="http://172.30.1.48:8080"
    //private const val BASE_URL ="http://just-lore-344905.du.r.appspot.com"

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: ApiInterface = retrofit.create(ApiInterface::class.java)
}


interface ApiInterface{
    //@Headers("Content-Type: application/json")
    //login/kakao
    @POST("login")
    fun requestLogin(
        @Body userData: LoginModel
    ): Call<LoginModel>

}