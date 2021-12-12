package com.sunlotocenter.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.sunlotocenter.dao.*
import com.sunlotocenter.dao.Report
import com.sunlotocenter.dto.Result
import com.sunlotocenter.dto.TotalReport
import com.sunlotocenter.utils.*
import retrofit2.Call
import retrofit2.Callback
import java.util.*
import kotlin.collections.ArrayList

class NotificationViewModel (private val savedStateHandle: SavedStateHandle) : ViewModel() {


    private val SCHEDULES: String= "schedules"
    private val REPORTS: String= "reports"
    private val BLOCKED_GAMES: String= "blocked_games"
    private val RESULTS= "results"
    private val SLOTS= "slots"
    private val NOTIFICATIONS: String= "notifications"
    var lastAddedNotificationsData= MutableLiveData<ArrayList<Notification>>()
    var notifications= arrayListOf<Notification>()
    var page= -1
    var ACTION= ACTION_SAVE

    var slotResponseData= MutableLiveData<Response<Slot>>()
    var gameScheduleData= MutableLiveData<Response<GameSchedule>>()
    var gamePriceData= MutableLiveData<Response<GamePrice>>()
    var gameAlertAndBlockData= MutableLiveData<Response<GameAlertAndBlock>>()
    var blockedGameData= MutableLiveData<Response<BlockedGame>>()
    var totalReportData= MutableLiveData<Response<TotalReport>>()
    var gameResultData= MutableLiveData<Response<GameResult>>()
    var notificationData= MutableLiveData<Response<Notification>>()


    fun getNotificationById(notificationId: Long){
        com.sunlotocenter.utils.notificationApi.getNotificationById(notificationId).enqueue(object : Callback<Response<Notification>>{
            override fun onResponse(call: Call<Response<Notification>>,
                response: retrofit2.Response<Response<Notification>>) { notificationData.postValue(response.body()) }

            override fun onFailure(call: Call<Response<Notification>>, t: Throwable) {
                notificationData.postValue(null)
            }

        })



    }

    fun loadSaveState() {
        notifications= savedStateHandle.get(NOTIFICATIONS)?: arrayListOf()
    }



    fun sendBroadCast(notification: Notification) {

        val headers= HashMap<String, String>()
        headers["accept-language"] = Locale.getDefault().toString()

        val call: Call<Response<Notification>> = notificationApi.sendBroadcast(notification, headers)
        call.enqueue(object : Callback<Response<Notification>> {
            override fun onResponse(call: Call<Response<Notification>>,
                                    response: retrofit2.Response<Response<Notification>>) {
                if(response.code()== 200){
                    notificationData.value= response.body()
                }else{
                    notificationData.value= null
                }
            }

            override fun onFailure(call: Call<Response<Notification>>, t: Throwable) {
                notificationData.value= null
            }
        })
    }

    fun loadNotifications(company: Long, isFirstPage: Boolean) {
        if(!isFirstPage)
            page++
        else
            page= 0

        notificationApi.loadNotifications(company, page).enqueue(object: Callback<Response<ArrayList<Notification>>>{
            override fun onResponse(
                call: Call<Response<ArrayList<Notification>>>,
                response: retrofit2.Response<Response<ArrayList<Notification>>>
            ) {
                if(response.body()== null){
                    lastAddedNotificationsData.postValue(ArrayList())
                }else{
                    lastAddedNotificationsData.postValue(response.body()!!.data)
                }


            }

            override fun onFailure(call: Call<Response<ArrayList<Notification>>>, t: Throwable) {
                lastAddedNotificationsData.postValue(ArrayList())
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