package com.sunlotocenter.model

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.sunlotocenter.dao.Bank
import com.sunlotocenter.dao.Blame
import com.sunlotocenter.dao.Response
import com.sunlotocenter.dao.User
import com.sunlotocenter.utils.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class BankViewModel (private val savedStateHandle: SavedStateHandle) : ViewModel() {

    private val BANKS: String= "banks"
    var lastAddedBanksData= MutableLiveData<ArrayList<Bank>>()
    var bankTotalData= MutableLiveData<Long>()
    var banks= arrayListOf<Bank>()
    var page= -1
    var ACTION= ACTION_SAVE

    var  bankResponseData= MutableLiveData<Response<Bank>>()

    fun loadBanks(isFirstPage: Boolean) {
        if(!isFirstPage)
            page++
        else
            page= 0

        bankApi.getBanks(page).enqueue(object: Callback<Response<ArrayList<Bank>>>{
            override fun onResponse(
                call: Call<Response<ArrayList<Bank>>>,
                response: retrofit2.Response<Response<ArrayList<Bank>>>
            ) {
                if(response.body()== null){
                    lastAddedBanksData.postValue(ArrayList())
                    bankTotalData.postValue(0)
                }else{
                    lastAddedBanksData.postValue(response.body()!!.data)
                    bankTotalData.postValue(response.body()!!.total)
                }


            }

            override fun onFailure(call: Call<Response<ArrayList<Bank>>>, t: Throwable) {
                lastAddedBanksData.postValue(ArrayList())
                bankTotalData.postValue(0)
            }

        })
    }


    fun loadSaveState() {
        banks= savedStateHandle.get(BANKS)?: arrayListOf()
    }


    fun save(bank: Bank) {

        ACTION= ACTION_SAVE
        var profilePart: MultipartBody.Part?= null

        if (isNotEmpty(bank.profilePath)) {
            if(!bank.profilePath!!.contains("http")){
                var profileFile= File(bank.profilePath)
                val reqBody= RequestBody.create(MediaType.parse(getMimeType(Uri.fromFile(profileFile).toString())), profileFile)
                profilePart= MultipartBody.Part.createFormData("profile", profileFile.name, reqBody)
            }
        }

        /*
        Here we send the language of the device to the user.
        */
        val headers= HashMap<String, String>()
        headers["accept-language"] = Locale.getDefault().toString()

        val bankBody= RequestBody.create(MediaType.parse("application/json"), getGson().toJson(bank))

        val call: Call<Response<Bank>> = bankApi.saveBank(profilePart, bankBody, headers)
        call.enqueue(object : Callback<Response<Bank>> {
            override fun onResponse(call: Call<Response<Bank>>,
                                    response: retrofit2.Response<Response<Bank>>) {
                if(response.code()== 200){
                    bankResponseData.value= response.body()
                }else{
                    bankResponseData.value= null
                }
            }

            override fun onFailure(call: Call<Response<Bank>>, t: Throwable) {
                bankResponseData.value= null
            }
        })

    }
}