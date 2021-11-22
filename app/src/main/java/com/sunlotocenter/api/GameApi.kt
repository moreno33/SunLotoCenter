package com.sunlotocenter.api

import com.sunlotocenter.dao.GameSchedule
import com.sunlotocenter.dao.Response
import com.sunlotocenter.dao.Slot
import com.sunlotocenter.dao.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*
import java.util.HashMap

interface GameApi {
    @POST("/play")
    fun play(@Body slot: Slot): Call<Response<Slot>>

    @POST("/schedules")
    fun saveGameSchedule(@Body gameSchedule: GameSchedule): Call<Response<GameSchedule>>

    @GET("/schedules/{page}")
    fun getSchedules(@Path("page") page:Int): Call<Response<ArrayList<GameSchedule>>>

    @GET("/schedules")
    fun getAllSchedules(): Call<Response<ArrayList<GameSchedule>>>
}