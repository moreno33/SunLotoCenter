package com.sunlotocenter.api

import com.sunlotocenter.dao.Bank
import com.sunlotocenter.dao.Blame
import com.sunlotocenter.dao.Response
import com.sunlotocenter.dao.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*
import java.util.*
import kotlin.collections.ArrayList

interface BankApi {

    @Multipart
    @POST("/banks")
    fun saveBank(
        @Part profile: MultipartBody.Part?,
        @Part("bank") accountBody: RequestBody?,
        @HeaderMap headers: HashMap<String, String>
    ): Call<Response<Bank>>

    @GET("/banks/{page}")
    fun getBanks(@Path("page") page:Int): Call<Response<ArrayList<Bank>>>
}