package com.sunlotocenter.api

import com.sunlotocenter.dao.*
import com.sunlotocenter.dto.Configuration
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

    @Multipart
    @POST("/companies")
    fun saveCompany(
        @Part profile: MultipartBody.Part?,
        @Part("company") accountBody: RequestBody?,
        @HeaderMap headers: HashMap<String, String>
    ): Call<Response<Company>>

    @GET("/login/{phone}/{password}")
    fun login(@Path("phone") phone: String,
              @Path("password") password: String): Call<Response<User>>


    @GET("/users/{company}/{page}")
    fun getUsers(@Path("company") company:Long,
                 @Path("page") page:Int):
            Call<Response<ArrayList<User>>>

    @POST("/blames")
    fun addBlame(@Body blame: Blame): Call<Response<Blame>>

    @GET("/blames/{page}/{userSequenceId}")
    fun getBlames(@Path("page") page: Int, @Path("userSequenceId") userSequenceId: Long): Call<Response<ArrayList<Blame>>>

    @GET("/sellers/{company}")
    fun getSellers(@Path("company") company: Long): Call<Response<ArrayList<Seller>>>

    @GET("/config/data/{sequenceId}")
    fun getConfigurationData(@Path("sequenceId") userSequenceId: Long?): Call<Response<Configuration>>
}