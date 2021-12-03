package com.sunlotocenter.api

import com.sunlotocenter.dao.*
import com.sunlotocenter.dao.Report
import com.sunlotocenter.dto.Result
import com.sunlotocenter.dto.TotalReport
import org.joda.time.DateTime
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

    @GET("/schedules/active")
    fun getAllActiveSchedules(): Call<Response<ArrayList<GameSchedule>>>

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

    @GET("/reports/{page}/{type}/{start}/{end}")
    fun getReports(@Path("page") page: Int,  @Path("type") type: GameType,
                   @Path("start") start: String?,
                   @Path("end") end: String?): Call<Response<ArrayList<Report>>>

    @GET("/reports/total/{type}/{start}/{end}")
    fun getTotalReport(@Path("type") type: GameType,
                       @Path("start") start: String?= null,
                       @Path("end") end: String?= null): Call<Response<TotalReport>>

    @GET("/results/{page}/{type}/{start}/{end}")
    fun getResults(@Path("page") page: Int,
                   @Path("type") type: GameType,
                   @Path("start") start: String?,
                   @Path("end") end: String?):
            Call<Response<ArrayList<Result>>>


    @GET("/slots/{page}/{sequenceId}/{session}")
    fun getSlots(@Path("page") page: Int,
                   @Path("sequenceId") sequenceId: Long,
                   @Path("session") session: GameSession):
            Call<Response<ArrayList<Slot>>>



    @POST("/results")
    fun saveGameResult(@Body gameResult: GameResult, @HeaderMap headers: HashMap<String, String>): Call<Response<GameResult>>
}