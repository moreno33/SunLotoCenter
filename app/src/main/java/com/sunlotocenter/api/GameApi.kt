package com.sunlotocenter.api

import com.sunlotocenter.dao.*
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

    @POST("/prices")
    fun saveGamePrice(@Body gamePrice: GamePrice): Call<Response<GamePrice>>

    @GET("/schedules/{page}")
    fun getSchedules(@Path("page") page:Int): Call<Response<ArrayList<GameSchedule>>>

    @GET("/schedules")
    fun getAllSchedules(): Call<Response<ArrayList<GameSchedule>>>

    @GET("/prices")
    fun getGamePrice(): Call<Response<GamePrice>>

    @GET("/alerts/blocks")
    fun getGameArletAndBlock(): Call<Response<GameAlertAndBlock>>

    @POST("/alerts/blocks")
    fun saveGameAlertAndBlock(@Body gameAlertAndBlock: GameAlertAndBlock): Call<Response<GameAlertAndBlock>>

    @GET("/blocks/{page}")
    fun getBlockedGames(@Path("page") page: Int): Call<Response<ArrayList<BlockedGame>>>

    @POST("/blocks")
    fun saveBlockedGame(@Body blockedGame: BlockedGame, @HeaderMap headers: HashMap<String, String>): Call<Response<BlockedGame>>
}