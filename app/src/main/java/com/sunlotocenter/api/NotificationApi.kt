package com.sunlotocenter.api

import com.sunlotocenter.dao.*
import retrofit2.Call
import retrofit2.http.*
import java.util.HashMap

interface NotificationApi {
    @POST("/broadcasts")
    fun sendBroadcast(@Body notification: Notification, @HeaderMap headers: HashMap<String, String>): Call<Response<Notification>>

    @GET("/broadcasts/{id}")
    fun getNotificationById(@Path("id") notificationId: Long): Call<Response<Notification>>

    @GET("/broadcasts/pages/{company}/{page}")
    fun loadNotifications(@Path("company") company: Long, @Path("page") page: Int): Call<Response<ArrayList<Notification>>>
}