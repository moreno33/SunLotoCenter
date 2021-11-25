package com.sunlotocenter.model

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sunlotocenter.dao.*
import com.sunlotocenter.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.await
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class GameViewModel (private val savedStateHandle: SavedStateHandle) : ViewModel() {

    private val SCHEDULES: String= "schedules"
    var lastAddedSchedulesData= MutableLiveData<ArrayList<GameSchedule>>()
    var lastAddedBlockedGamesData= MutableLiveData<ArrayList<BlockedGame>>()
    var schedules= arrayListOf<GameSchedule>()
    var blockedGames= arrayListOf<BlockedGame>()
    var page= -1
    var ACTION= ACTION_SAVE

    var slotResponseData= MutableLiveData<Response<Slot>>()
    var gameScheduleData= MutableLiveData<Response<GameSchedule>>()
    var gamePriceData= MutableLiveData<Response<GamePrice>>()
    var gameAlertAndBlockData= MutableLiveData<Response<GameAlertAndBlock>>()
    var blockedGameData= MutableLiveData<Response<BlockedGame>>()


    fun getGamePrice(){
        gameApi.getGamePrice().enqueue(object : Callback<Response<GamePrice>>{
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

    fun getGameAlertAndBlock(){
        gameApi.getGameArletAndBlock().enqueue(object : Callback<Response<GameAlertAndBlock>>{
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

    fun getAllGameSchedules(){
        gameApi.getAllSchedules().enqueue(object : Callback<Response<ArrayList<GameSchedule>>>{
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

    fun loadSchedules(){
        loadSchedules(true)
    }

    private fun loadSchedules(isLoadMore: Boolean) {
        if(isLoadMore)
            page++
        else
            page= 0

        gameApi.getSchedules(page).enqueue(object :Callback<Response<ArrayList<GameSchedule>>>{
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

    fun loadBlockedGame(isFirstPage: Boolean) {
        if(!isFirstPage)
            page++
        else
            page= 0

        gameApi.getBlockedGames(page).enqueue(object: Callback<Response<ArrayList<BlockedGame>>>{
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

    fun loadSaveState() {
        schedules= savedStateHandle.get(SCHEDULES)?: arrayListOf()
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
}