package com.sunlotocenter.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.sunlotocenter.adapter.GameScheduleSessionAdapter
import com.sunlotocenter.dao.*
import com.sunlotocenter.dao.Report
import com.sunlotocenter.dto.GametDto
import com.sunlotocenter.dto.Result
import com.sunlotocenter.dto.TotalReport
import com.sunlotocenter.utils.*
import org.joda.time.DateTime
import retrofit2.Call
import retrofit2.Callback
import java.util.*
import kotlin.collections.ArrayList

class GameViewModel (private val savedStateHandle: SavedStateHandle) : ViewModel() {


    private val SCHEDULES: String= "schedules"
    private val REPORTS: String= "reports"
    private val BLOCKED_GAMES: String= "blocked_games"
    private val RESULTS= "results"
    private val SLOTS= "slots"
    var lastAddedSchedulesData= MutableLiveData<ArrayList<GameSchedule>>()
    var lastAddedBlockedGamesData= MutableLiveData<ArrayList<BlockedGame>>()
    var lastAddedReportsData= MutableLiveData<ArrayList<Report>>()
    var lastAddedResultsData= MutableLiveData<ArrayList<Result>>()
    var lastAddedSlotsData= MutableLiveData<ArrayList<Slot>>()
    var lastAddedGameUnderAlertData= MutableLiveData<ArrayList<GametDto>>()
    var schedules= arrayListOf<GameSchedule>()
    var reports= arrayListOf<Report>()
    var blockedGames= arrayListOf<BlockedGame>()
    var alertedGames= arrayListOf<GametDto>()
    var results= arrayListOf<Result>()
    var slots= arrayListOf<Slot>()
    var page= -1
    var ACTION= ACTION_SAVE

    var slotResponseData= MutableLiveData<Response<Slot>>()
    var gameScheduleData= MutableLiveData<Response<GameSchedule>>()
    var gamePriceData= MutableLiveData<Response<GamePrice>>()
    var gameAlertAndBlockData= MutableLiveData<Response<GameAlertAndBlock>>()
    var blockedGameData= MutableLiveData<Response<BlockedGame>>()
    var totalReportData= MutableLiveData<Response<TotalReport>>()
    var gameResultData= MutableLiveData<Response<GameResult>>()


    fun getGamePrice(company: Long){

        gameApi.getGamePrice(company).enqueue(object : Callback<Response<GamePrice>>{
            override fun onResponse(
                call: Call<Response<GamePrice>>,
                response: retrofit2.Response<Response<GamePrice>>
            ) {
                gamePriceData.postValue(response.body())
            }

            override fun onFailure(call: Call<Response<GamePrice>>, t: Throwable) {
                gamePriceData.postValue(null)
            }

        })
    }

    fun getGameAlertAndBlock(company: Long){

        gameApi.getGameArletAndBlock(company).enqueue(object : Callback<Response<GameAlertAndBlock>>{
            override fun onResponse(
                call: Call<Response<GameAlertAndBlock>>,
                response: retrofit2.Response<Response<GameAlertAndBlock>>
            ) {
                gameAlertAndBlockData.postValue(response.body())
            }

            override fun onFailure(call: Call<Response<GameAlertAndBlock>>, t: Throwable) {
                gameAlertAndBlockData.postValue(null)
            }

        })
    }

    fun getAllGameSchedules(company: Long){

        gameApi.getAllSchedules(company).enqueue(object : Callback<Response<ArrayList<GameSchedule>>>{
            override fun onResponse(
                call: Call<Response<ArrayList<GameSchedule>>>,
                response: retrofit2.Response<Response<ArrayList<GameSchedule>>>
            ) {
                if(response.body()== null){
                    lastAddedSchedulesData.postValue(ArrayList())
                }else{
                    lastAddedSchedulesData.postValue(response.body()!!.data)
                }
            }

            override fun onFailure(call: Call<Response<ArrayList<GameSchedule>>>, t: Throwable) {
                lastAddedSchedulesData.postValue(ArrayList())
            }

        })
    }

    fun getAllActiveGameSchedules(company: Long){

        gameApi.getAllActiveSchedules(company).enqueue(object : Callback<Response<ArrayList<GameSchedule>>>{
            override fun onResponse(
                call: Call<Response<ArrayList<GameSchedule>>>,
                response: retrofit2.Response<Response<ArrayList<GameSchedule>>>
            ) {
                if(response.body()== null){
                    lastAddedSchedulesData.postValue(ArrayList())
                }else{
                    lastAddedSchedulesData.postValue(response.body()!!.data)
                }
            }

            override fun onFailure(call: Call<Response<ArrayList<GameSchedule>>>, t: Throwable) {
                lastAddedSchedulesData.postValue(ArrayList())
            }

        })
    }

    fun loadSchedules(company: Long, isFirstPage: Boolean) {
        if(!isFirstPage)
            page++
        else
            page= 0

        gameApi.getSchedules(company, page).enqueue(object :Callback<Response<ArrayList<GameSchedule>>>{
            override fun onResponse(
                call: Call<Response<ArrayList<GameSchedule>>>,
                response: retrofit2.Response<Response<ArrayList<GameSchedule>>>
            ) {
                if(response.body()== null){
                    lastAddedSchedulesData.postValue(ArrayList())
                }else{
                    lastAddedSchedulesData.postValue(response.body()!!.data)
                }
            }

            override fun onFailure(call: Call<Response<ArrayList<GameSchedule>>>, t: Throwable) {
                lastAddedSchedulesData.postValue(ArrayList())
            }

        })
    }

    fun loadBlockedGame(company: Long, isFirstPage: Boolean) {
        if(!isFirstPage)
            page++
        else
            page= 0

        gameApi.getBlockedGames(company, page).enqueue(object: Callback<Response<ArrayList<BlockedGame>>>{
            override fun onResponse(
                call: Call<Response<ArrayList<BlockedGame>>>,
                response: retrofit2.Response<Response<ArrayList<BlockedGame>>>
            ) {
                if(response.body()== null){
                    lastAddedBlockedGamesData.postValue(ArrayList())
                }else{
                    lastAddedBlockedGamesData.postValue(response.body()!!.data)
                }


            }

            override fun onFailure(call: Call<Response<ArrayList<BlockedGame>>>, t: Throwable) {
                lastAddedBlockedGamesData.postValue(ArrayList())
            }

        })
    }

    fun loadReports(company: Long, isFirstPage: Boolean, gameType: GameType, start: String, end: String) {
        if(!isFirstPage)
            page++
        else
            page= 0

        gameApi.getReports(company, page, gameType, start, end).enqueue(object: Callback<Response<ArrayList<Report>>>{
            override fun onResponse(
                call: Call<Response<ArrayList<Report>>>,
                response: retrofit2.Response<Response<ArrayList<Report>>>
            ) {
                if(response.body()== null){
                    lastAddedReportsData.postValue(ArrayList())
                }else{
                    lastAddedReportsData.postValue(response.body()!!.data)
                }


            }

            override fun onFailure(call: Call<Response<ArrayList<Report>>>, t: Throwable) {
                lastAddedReportsData.postValue(ArrayList())
            }

        })
    }


    fun loadIndReports(ind: Long, company: Long, isFirstPage: Boolean, gameType: GameType, start: String, end: String) {
        if(!isFirstPage)
            page++
        else
            page= 0

        gameApi.getIndReports(ind, company, page, gameType, start, end).enqueue(object: Callback<Response<ArrayList<Report>>>{
            override fun onResponse(
                call: Call<Response<ArrayList<Report>>>,
                response: retrofit2.Response<Response<ArrayList<Report>>>
            ) {
                if(response.body()== null){
                    lastAddedReportsData.postValue(ArrayList())
                }else{
                    lastAddedReportsData.postValue(response.body()!!.data)
                }


            }

            override fun onFailure(call: Call<Response<ArrayList<Report>>>, t: Throwable) {
                lastAddedReportsData.postValue(ArrayList())
            }

        })
    }

    fun loadSaveState() {
        schedules= savedStateHandle.get(SCHEDULES)?: arrayListOf()
        reports= savedStateHandle.get(REPORTS)?: arrayListOf()
        blockedGames= savedStateHandle.get(BLOCKED_GAMES)?: arrayListOf()
        results= savedStateHandle.get(RESULTS)?: arrayListOf()
        slots= savedStateHandle.get(SLOTS)?: arrayListOf()

    }


    fun play(slot: Slot) {

        ACTION= ACTION_SAVE

        val call: Call<Response<Slot>> = gameApi.play(slot)
        call.enqueue(object : Callback<Response<Slot>> {
            override fun onResponse(call: Call<Response<Slot>>,
                                    response: retrofit2.Response<Response<Slot>>) {
                if(response.code()== 200){
                    slotResponseData.value= response.body()
                }else{
                    slotResponseData.value= null
                }
            }

            override fun onFailure(call: Call<Response<Slot>>, t: Throwable) {
                slotResponseData.value= null
            }
        })

    }

    fun saveGameSchedule(gameSchedule: GameSchedule) {

        val call: Call<Response<GameSchedule>> = gameApi.saveGameSchedule(gameSchedule)
        call.enqueue(object : Callback<Response<GameSchedule>> {
            override fun onResponse(call: Call<Response<GameSchedule>>,
                                    response: retrofit2.Response<Response<GameSchedule>>) {
                if(response.code()== 200){
                    gameScheduleData.value= response.body()
                }else{
                    gameScheduleData.value= null
                }
            }

            override fun onFailure(call: Call<Response<GameSchedule>>, t: Throwable) {
                gameScheduleData.value= null
            }
        })
    }

    fun saveGameResult(gameResult: GameResult) {

        val headers= HashMap<String, String>()
        headers["accept-language"] = Locale.getDefault().toString()
        val call: Call<Response<GameResult>> = gameApi.saveGameResult(gameResult, headers)
        call.enqueue(object : Callback<Response<GameResult>> {
            override fun onResponse(call: Call<Response<GameResult>>,
                                    response: retrofit2.Response<Response<GameResult>>) {
                if(response.code()== 200){
                    gameResultData.value= response.body()
                }else{
                    gameResultData.value= null
                }
            }

            override fun onFailure(call: Call<Response<GameResult>>, t: Throwable) {
                gameResultData.value= null
            }
        })
    }

    fun saveGamePrice(gamePrice: GamePrice) {
        val call: Call<Response<GamePrice>> = gameApi.saveGamePrice(gamePrice)
        call.enqueue(object : Callback<Response<GamePrice>> {
            override fun onResponse(call: Call<Response<GamePrice>>,
                                    response: retrofit2.Response<Response<GamePrice>>) {
                if(response.code()== 200){
                    gamePriceData.value= response.body()
                }else{
                    gamePriceData.value= null
                }
            }

            override fun onFailure(call: Call<Response<GamePrice>>, t: Throwable) {
                gamePriceData.value= null
            }
        })
    }

    fun saveGameAlertAndBlock(gameAlertAndBlock: GameAlertAndBlock) {
        val call: Call<Response<GameAlertAndBlock>> = gameApi.saveGameAlertAndBlock(gameAlertAndBlock)
        call.enqueue(object : Callback<Response<GameAlertAndBlock>> {
            override fun onResponse(call: Call<Response<GameAlertAndBlock>>,
                                    response: retrofit2.Response<Response<GameAlertAndBlock>>) {
                if(response.code()== 200){
                    gameAlertAndBlockData.value= response.body()
                }else{
                    gameAlertAndBlockData.value= null
                }
            }

            override fun onFailure(call: Call<Response<GameAlertAndBlock>>, t: Throwable) {
                gameAlertAndBlockData.value= null
            }
        })
    }

    fun saveBlockedGame(blockedGame: BlockedGame) {

        /*
        Here we send the language of the device to the user.
        */
        val headers= HashMap<String, String>()
        headers["accept-language"] = Locale.getDefault().toString()

        val call: Call<Response<BlockedGame>> = gameApi.saveBlockedGame(blockedGame, headers)
        call.enqueue(object : Callback<Response<BlockedGame>> {
            override fun onResponse(call: Call<Response<BlockedGame>>,
                                    response: retrofit2.Response<Response<BlockedGame>>) {
                if(response.code()== 200){
                    blockedGameData.value= response.body()
                }else{
                    blockedGameData.value= null
                }
            }

            override fun onFailure(call: Call<Response<BlockedGame>>, t: Throwable) {
                blockedGameData.value= null
            }
        })
    }

    fun getTotalReport(company: Long, gameType: GameType, start: String?= null, end:String?= null) {
        gameApi.getTotalReport(company, gameType, start, end).enqueue(object: Callback<Response<TotalReport>>{
            override fun onResponse(
                call: Call<Response<TotalReport>>,
                response: retrofit2.Response<Response<TotalReport>>
            ) {
                if(response.body()== null){
                    totalReportData.postValue(null)
                }else{
                    totalReportData.postValue(response.body())
                }
            }

            override fun onFailure(call: Call<Response<TotalReport>>, t: Throwable) {
                totalReportData.postValue(null)
            }

        })
    }

    fun getIndTotalReport(ind: Long, company: Long, gameType: GameType, start: String?= null, end:String?= null) {
        gameApi.getIndTotalReport(ind, company, gameType, start, end).enqueue(object: Callback<Response<TotalReport>>{
            override fun onResponse(
                call: Call<Response<TotalReport>>,
                response: retrofit2.Response<Response<TotalReport>>
            ) {
                if(response.body()== null){
                    totalReportData.postValue(null)
                }else{
                    totalReportData.postValue(response.body())
                }
            }

            override fun onFailure(call: Call<Response<TotalReport>>, t: Throwable) {
                totalReportData.postValue(null)
            }

        })
    }

    fun loadResults(company: Long, isFirstPage: Boolean, gameType: GameType, start: String, end: String) {
        if(!isFirstPage)
            page++
        else
            page= 0

        gameApi.getResults(company, page, gameType, start, end).enqueue(object: Callback<Response<ArrayList<Result>>>{
            override fun onResponse(
                call: Call<Response<ArrayList<Result>>>,
                response: retrofit2.Response<Response<ArrayList<Result>>>
            ) {
                if(response.body()== null){
                    lastAddedResultsData.postValue(ArrayList())
                }else{
                    lastAddedResultsData.postValue(response.body()!!.data)
                }
            }

            override fun onFailure(call: Call<Response<ArrayList<Result>>>, t: Throwable) {
                lastAddedResultsData.postValue(ArrayList())
            }

        })
    }

    fun loadSlots(company: Long, isFirstPage: Boolean, sequenceId: Long, gameSession: GameSession) {
        if(!isFirstPage)
            page++
        else
            page= 0

        gameApi.getSlots(company, page, sequenceId, gameSession).enqueue(object: Callback<Response<ArrayList<Slot>>>{
            override fun onResponse(
                call: Call<Response<ArrayList<Slot>>>,
                response: retrofit2.Response<Response<ArrayList<Slot>>>
            ) {
                if(response.body()== null){
                    lastAddedSlotsData.postValue(ArrayList())
                }else{
                    lastAddedSlotsData.postValue(response.body()!!.data)
                }
            }

            override fun onFailure(call: Call<Response<ArrayList<Slot>>>, t: Throwable) {
                lastAddedSlotsData.postValue(ArrayList())
            }

        })
    }

    fun getResultFor(company: Long, gameScheduleSession: GameScheduleSessionAdapter.GameScheduleSession) {
        val call: Call<Response<GameResult>> = gameApi.getResultFor(company, gameScheduleSession.gameSchedule.type!!, gameScheduleSession.gameSession)
        call.enqueue(object : Callback<Response<GameResult>> {
            override fun onResponse(call: Call<Response<GameResult>>,
                                    response: retrofit2.Response<Response<GameResult>>) {
                if(response.code()== 200){
                    gameResultData.value= response.body()
                }else{
                    gameResultData.value= null
                }
            }

            override fun onFailure(call: Call<Response<GameResult>>, t: Throwable) {
                gameResultData.value= null
            }
        })
    }

    fun getGamesUnderAlert(company: Long, isFirstPage: Boolean) {
        if(!isFirstPage)
            page++
        else
            page= 0

        gameApi.getGamesUnderAlert(company, page).enqueue(object: Callback<Response<ArrayList<GametDto>>>{
            override fun onResponse(
                call: Call<Response<ArrayList<GametDto>>>,
                response: retrofit2.Response<Response<ArrayList<GametDto>>>
            ) {
                if(response.body()== null){
                    lastAddedGameUnderAlertData.postValue(ArrayList())
                }else{
                    lastAddedGameUnderAlertData.postValue(response.body()!!.data)
                }
            }

            override fun onFailure(call: Call<Response<ArrayList<GametDto>>>, t: Throwable) {
                lastAddedGameUnderAlertData.postValue(ArrayList())
            }

        })
    }

//    fun saveState() {
//        schedules= savedStateHandle.set(SCHEDULES)
//        reports= savedStateHandle.get(REPORTS)?
//        blockedGames= savedStateHandle.get(BLOCKED_GAMES)
//        results= savedStateHandle.get(RESULTS)?
//    }
}