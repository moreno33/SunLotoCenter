package com.sunlotocenter.service

import android.app.IntentService
import android.content.Intent
import android.util.Log
import com.sunlotocenter.MyApplication
import com.sunlotocenter.dao.*
import com.sunlotocenter.dto.Configuration
import com.sunlotocenter.utils.userApi
import retrofit2.Call
import retrofit2.Callback
import java.util.*

/**
 * This is a service class that initialize some configuration
 * data that needs to be retrieve from the database.
 * Every time we launch the application, this service will get
 * activated to find out if there is nothing new from the server.
 */
class ConfigurationService : IntentService(TAG) {

    override fun onHandleIntent(intent: Intent?) {
        val action = intent!!.action
        when (action) {
            ACTION_CONFIG_DATA -> cacheConfigurationData()
            ACTION_VERSION->cacheVersionData()
        }
    }

    private fun cacheVersionData() {
        val headers: MutableMap<String, String> = HashMap()
        headers["accept-language"] = Locale.getDefault().toString()
            val versionCall: Call<Response<Version>> = userApi
            .getCurrentVersion()

        versionCall.enqueue(object : Callback<Response<Version>> {
            /*
            In case we get a positive answer we need to update our list accordingly
             */
            override fun onResponse(call: Call<Response<Version>>,
                                    response: retrofit2.Response<Response<Version>>) {

                if (response.body() != null) {
                    if (response.body()!!.success) {
                        //If the response comes successfully and the account is null, there is a problem, log the user out.
                        val version = response.body()!!.data
                        updateVersion(version)
                    }
                }
            }

            /*
             * In case there is a failure no need to advise the user because this is a service
             * running on background
             * @param call
             * @param t
             */
            override fun onFailure(call: Call<Response<Version>>, t: Throwable) {
                Log.e(TAG, "onFailure: Sorry we fail fetching version")
            }
        })
    }

    private fun cacheConfigurationData() {

        if (!MyApplication.getInstance().isLoggedIn) return
        val headers: MutableMap<String, String> = HashMap()
        headers["accept-language"] = Locale.getDefault().toString()
        val getAccountCall: Call<Response<Configuration>> = userApi
            .getConfigurationData(MyApplication.getInstance().connectedUser.sequence!!.id)

        getAccountCall.enqueue(object : Callback<Response<Configuration>> {
            /*
            In case we get a positive answer we need to update our list accordingly
             */
            override fun onResponse(call: Call<Response<Configuration>>,
                                    response: retrofit2.Response<Response<Configuration>>) {

                if (response.body() != null) {
                    if (response.body()!!.success) {
                        //If the response comes successfully and the account is null, there is a problem, log the user out.
                        val configuration = response.body()!!.data

                        if (configuration == null)
                            MyApplication.getInstance().logout()
                        else {
                            updateUserInfo(configuration.connectedUser)
                            updateGameSchedules(configuration.gameSchedules)
                            updateLatestResult(configuration.latestGameResult)
                            updateCompany(configuration.company)
                            updateVersion(configuration.version)
                        }
                    }
                }
            }

            /*
             * In case there is a failure no need to advise the user because this is a service
             * running on background
             * @param call
             * @param t
             */
            override fun onFailure(call: Call<Response<Configuration>>, t: Throwable) {
                Log.e(TAG, "onFailure: Sorry we fail fetching your configuration data")
            }
        })
    }

    private fun updateVersion(version: Version?) {
        MyApplication.getInstance().version= version
    }

    private fun updateCompany(company: Company?) {
        MyApplication.getInstance().company= company
    }

    private fun updateLatestResult(gameResult: GameResult?) {
        MyApplication.getInstance().gameResult= gameResult
    }

    private fun updateGameSchedules(gameSchedules: ArrayList<GameSchedule>?) {
        MyApplication.getInstance().gameSchedules= gameSchedules
    }

    private fun updateUserInfo(connectedUser: User?) {
        if(connectedUser== null) MyApplication.getInstance().logout()
        if (!MyApplication.getInstance().connectedUser.phoneNumber?.number
                .equals(connectedUser?.phoneNumber?.number) ||
            connectedUser?.status != UserStatus.ACTIVE
        ) {
            MyApplication.getInstance().logout()
        } else {
            MyApplication.getInstance().connectedUser= connectedUser
        }
    }

    companion object {
        const val ACTION_VERSION= "com.sunlotocenter.ACTION_VERSION"
        const val ACTION_CONFIG_DATA = "com.sunlotocenter.ACTION_CONFIG_DATA"
        const val TAG = "ConfigurationService"
    }
}