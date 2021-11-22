package com.sunlotocenter.api

import com.sunlotocenter.dao.Blame
import com.sunlotocenter.dao.Response
import com.sunlotocenter.dao.Seller
import com.sunlotocenter.dao.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*
import java.util.*
import kotlin.collections.ArrayList

interface UserApi {

    @Multipart
    @POST("/users")
    fun saveUser(
        @Part profile: MultipartBody.Part?,
        @Part("user") accountBody: RequestBody?,
        @HeaderMap headers: HashMap<String, String>
    ): Call<Response<User>>

    @GET("/login/{phone}/{password}")
    fun login(@Path("phone") phone: String,
              @Path("password") password: String): Call<Response<User>>


    @GET("/users/{page}")
    fun getUsers(@Path("page") page:Int): Call<Response<ArrayList<User>>>

    @POST("/blames")
    fun addBlame(@Body blame: Blame): Call<Response<Blame>>

    @GET("/blames/{page}/{userSequenceId}")
    fun getBlames(@Path("page") page: Int, @Path("userSequenceId") userSequenceId: Long): Call<Response<ArrayList<Blame>>>

    @GET("/sellers/")
    fun getSellers(): Call<Response<ArrayList<Seller>>>
}