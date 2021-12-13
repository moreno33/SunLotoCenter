package com.sunlotocenter.api

import com.sunlotocenter.dao.*
import com.sunlotocenter.dao.Report
import com.sunlotocenter.dto.GametDto
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

    @GET("/schedules/{company}/{page}")
    fun getSchedules(@Path("company") company: Long, @Path("page") page:Int): Call<Response<ArrayList<GameSchedule>>>

    @GET("/schedules/{company}")
    fun getAllSchedules(@Path("company") company: Long): Call<Response<ArrayList<GameSchedule>>>

    @GET("/schedules/active/{company}")
    fun getAllActiveSchedules(@Path("company") company: Long): Call<Response<ArrayList<GameSchedule>>>

    @GET("/prices/{company}")
    fun getGamePrice(@Path("company") company: Long): Call<Response<GamePrice>>

    @GET("/alerts/blocks/{company}")
    fun getGameArletAndBlock(@Path("company") company: Long): Call<Response<GameAlertAndBlock>>

    @POST("/alerts/blocks")
    fun saveGameAlertAndBlock(@Body gameAlertAndBlock: GameAlertAndBlock): Call<Response<GameAlertAndBlock>>

    @GET("/blocks/{company}/{page}")
    fun getBlockedGames(@Path("company") company: Long,
                        @Path("page") page: Int):
            Call<Response<ArrayList<BlockedGame>>>

    @GET("/alerts/{company}/{page}")
    fun getGamesUnderAlert(@Path("company") company: Long,
                        @Path("page") page: Int):
            Call<Response<ArrayList<GametDto>>>

    @POST("/blocks")
    fun saveBlockedGame(@Body blockedGame: BlockedGame, @HeaderMap headers: HashMap<String, String>): Call<Response<BlockedGame>>

    @GET("/reports/{company}/{page}/{type}/{start}/{end}")
    fun getReports(@Path("company") company: Long,
                   @Path("page") page: Int,  @Path("type") type: GameType,
                   @Path("start") start: String?,
                   @Path("end") end: String?): Call<Response<ArrayList<Report>>>

    @GET("/reports/ind/{ind}/{company}/{page}/{type}/{start}/{end}")
    fun getIndReports(@Path("ind") ind: Long,
                      @Path("company") company: Long,
                      @Path("page") page: Int,  @Path("type") type: GameType,
                      @Path("start") start: String?,
                      @Path("end") end: String?): Call<Response<ArrayList<Report>>>

    @GET("/reports/total/{company}/{type}/{start}/{end}")
    fun getTotalReport(@Path("company") company: Long,
                       @Path("type") type: GameType,
                       @Path("start") start: String?= null,
                       @Path("end") end: String?= null): Call<Response<TotalReport>>

    @GET("/reports/total/ind/{ind}/{company}/{type}/{start}/{end}")
    fun getIndTotalReport(@Path("ind") ind: Long,
                          @Path("company") company: Long,
                          @Path("type") type: GameType,
                          @Path("start") start: String?= null,
                          @Path("end") end: String?= null): Call<Response<TotalReport>>

    @GET("/results/{company}/{page}/{type}/{start}/{end}")
    fun getResults(@Path("company") company: Long,
                   @Path("page") page: Int,
                   @Path("type") type: GameType,
                   @Path("start") start: String?,
                   @Path("end") end: String?):
            Call<Response<ArrayList<Result>>>


    @GET("/slots/{company}/{page}/{sequenceId}/{session}")
    fun getSlots(@Path("company") company: Long,
                 @Path("page") page: Int,
                 @Path("sequenceId") sequenceId: Long,
                 @Path("session") session: GameSession):
            Call<Response<ArrayList<Slot>>>


    @POST("/results")
    fun saveGameResult(@Body gameResult: GameResult, @HeaderMap headers: HashMap<String, String>): Call<Response<GameResult>>

    @GET("/results/for/{company}/{type}/{session}")
    fun getResultFor(@Path("company") company: Long,
                     @Path("type") type: GameType,
                     @Path("session") session: GameSession):
            Call<Response<GameResult>>


}