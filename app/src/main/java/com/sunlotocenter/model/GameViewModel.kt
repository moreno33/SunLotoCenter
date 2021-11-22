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
    var schedules= arrayListOf<GameSchedule>()
    var page= -1
    var ACTION= ACTION_SAVE

    var slotResponseData= MutableLiveData<Response<Slot>>()
    var gameScheduleData= MutableLiveData<Response<GameSchedule>>()

    fun getAllGameSchedules(){
        viewModelScope.launch(Dispatchers.IO) {
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
    }

    fun loadSchedules(){
        loadSchedules(true)
    }

    private fun loadSchedules(isLoadMore: Boolean) {
        if(isLoadMore)
            page++
        else
            page= 0

        viewModelScope.launch(Dispatchers.IO) {
            val schedulesResponse= gameApi.getSchedules(page).await()
            lastAddedSchedulesData.postValue(schedulesResponse.data)
        }
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


    fun login(phone: String, password: String) {
        ACTION= ACTION_LOGIN
        val call: Call<Response<User>> = userApi.login(phone, password)

        call.enqueue(object : Callback<Response<User>> {
            override fun onResponse(call: Call<Response<User>>,
                                    response: retrofit2.Response<Response<User>>) {
//                userResponseData.value= response.body()
            }

            override fun onFailure(call: Call<Response<User>>, t: Throwable) {
//                userResponseData.value= null
            }
        })

    }

}