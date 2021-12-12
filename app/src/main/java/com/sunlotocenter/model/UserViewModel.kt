package com.sunlotocenter.model

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.sunlotocenter.MyApplication
import com.sunlotocenter.dao.*
import com.sunlotocenter.extensions.redirectToDashboard
import com.sunlotocenter.utils.*
import kotlinx.coroutines.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.await
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class UserViewModel (private val savedStateHandle: SavedStateHandle) : ViewModel() {

    private val EMPLOYEES: String= "employees"
    private val BLAMES: String= "blames"
    var lastAddedEmployeesData= MutableLiveData<ArrayList<out User>>()
    var lastAddedBlamesData= MutableLiveData<ArrayList<Blame>>()
    var employeeTotalData= MutableLiveData<Long>()
    var blameData= MutableLiveData<Response<Blame>>()
    var employees= arrayListOf<User>()
    var blames= arrayListOf<Blame>()
    var page= -1
    var ACTION= ACTION_SAVE

    var  userResponseData= MutableLiveData<Response<User>>()
    var  companyResponseData= MutableLiveData<Response<Company>>()

    fun loadEmployees(isFirstPage: Boolean) {
        if(!isFirstPage)
            page++
        else
            page= 0

        userApi.getUsers( MyApplication.getInstance().company.sequence!!.id!!, page).enqueue(object: Callback<Response<ArrayList<User>>>{
            override fun onResponse(
                call: Call<Response<ArrayList<User>>>,
                response: retrofit2.Response<Response<ArrayList<User>>>
            ) {
                if(response.body()== null){
                    lastAddedEmployeesData.postValue(ArrayList())
                    employeeTotalData.postValue(0)
                }else{
                    lastAddedEmployeesData.postValue(response.body()!!.data)
                    employeeTotalData.postValue(response.body()!!.total)
                }


            }

            override fun onFailure(call: Call<Response<ArrayList<User>>>, t: Throwable) {
                lastAddedEmployeesData.postValue(ArrayList())
                employeeTotalData.postValue(0)
            }

        })
    }

    fun loadBlamesForUser(isFirstPage: Boolean, userSequenceId: Long) {
        if(!isFirstPage)
            page++
        else
            page= 0

        userApi.getBlames(page, userSequenceId).enqueue(object: Callback<Response<ArrayList<Blame>>>{
            override fun onResponse(
                call: Call<Response<ArrayList<Blame>>>,
                response: retrofit2.Response<Response<ArrayList<Blame>>>
            ) {
                if(response.body()== null){
                    lastAddedBlamesData.postValue(ArrayList())
                }else{
                    lastAddedBlamesData.postValue(response.body()!!.data)
                }


            }

            override fun onFailure(call: Call<Response<ArrayList<Blame>>>, t: Throwable) {
                lastAddedBlamesData.postValue(ArrayList())
            }

        })
    }

    fun loadSellers(company: Long) {

        userApi.getSellers(company).enqueue(object: Callback<Response<ArrayList<Seller>>>{
            override fun onResponse(
                call: Call<Response<ArrayList<Seller>>>,
                response: retrofit2.Response<Response<ArrayList<Seller>>>
            ) {
                if(response.body()== null){
                    lastAddedEmployeesData.postValue(ArrayList())
                }else{
                    lastAddedEmployeesData.postValue(response.body()!!.data)
                }


            }

            override fun onFailure(call: Call<Response<ArrayList<Seller>>>, t: Throwable) {
                lastAddedEmployeesData.postValue(ArrayList())
            }

        })
    }


    fun loadSaveState() {
        employees= savedStateHandle.get(EMPLOYEES)?: arrayListOf()
        blames= savedStateHandle.get(BLAMES)?: arrayListOf()
    }


    fun save(user: User) {

        ACTION= ACTION_SAVE
        var profilePart: MultipartBody.Part?= null

        if (!user.profilePath.isNullOrEmpty()) {
            if(!user.profilePath!!.contains("http")){
                var profileFile= File(user.profilePath)
                val reqBody= RequestBody.create(MediaType.parse(getMimeType(Uri.fromFile(profileFile).toString())), profileFile)
                profilePart= MultipartBody.Part.createFormData("profile", profileFile.name, reqBody)
            }
        }

        /*
        Here we send the language of the device to the user.
        */
        val headers= HashMap<String, String>()
        headers["accept-language"] = Locale.getDefault().toString()

        val accountBody= RequestBody.create(MediaType.parse("application/json"), getGson().toJson(user))

        val call: Call<Response<User>> = userApi.saveUser(profilePart, accountBody, headers)
        call.enqueue(object : Callback<Response<User>> {
            override fun onResponse(call: Call<Response<User>>,
                                    response: retrofit2.Response<Response<User>>) {
                if(response.code()== 200){
                    userResponseData.value= response.body()
                }else{
                    userResponseData.value= null
                }
            }

            override fun onFailure(call: Call<Response<User>>, t: Throwable) {
                userResponseData.value= null
            }
        })

    }


    fun saveCompany(company: Company) {

        ACTION= ACTION_SAVE
        var profilePart: MultipartBody.Part?= null

        if (company.profilePath!!.isNotEmpty()) {
            if(!company.profilePath!!.contains("http")){
                var profileFile= File(company.profilePath)
                val reqBody= RequestBody.create(MediaType.parse(getMimeType(Uri.fromFile(profileFile).toString())), profileFile)
                profilePart= MultipartBody.Part.createFormData("profile", profileFile.name, reqBody)
            }
        }

        /*
        Here we send the language of the device to the user.
        */
        val headers= HashMap<String, String>()
        headers["accept-language"] = Locale.getDefault().toString()

        val accountBody= RequestBody.create(MediaType.parse("application/json"), getGson().toJson(company))

        val call: Call<Response<Company>> = userApi.saveCompany(profilePart, accountBody, headers)
        call.enqueue(object : Callback<Response<Company>> {
            override fun onResponse(call: Call<Response<Company>>,
                                    response: retrofit2.Response<Response<Company>>) {
                if(response.code()== 200){
                    companyResponseData.value= response.body()
                }else{
                    companyResponseData.value= null
                }
            }

            override fun onFailure(call: Call<Response<Company>>, t: Throwable) {
                companyResponseData.value= null
            }
        })

    }


    fun login(phone: String, password: String) {
        ACTION= ACTION_LOGIN
        val call: Call<Response<User>> = userApi.login(phone, password)

        call.enqueue(object : Callback<Response<User>> {
            override fun onResponse(call: Call<Response<User>>,
                                    response: retrofit2.Response<Response<User>>) {
                userResponseData.value= response.body()
            }

            override fun onFailure(call: Call<Response<User>>, t: Throwable) {
                userResponseData.value= null
            }
        })

    }

    fun addBlame(blame: Blame) {

        val call: Call<Response<Blame>> = userApi.addBlame(blame)
        call.enqueue(object : Callback<Response<Blame>> {
            override fun onResponse(call: Call<Response<Blame>>,
                                    response: retrofit2.Response<Response<Blame>>) {
                if(response.code()== 200){
                    blameData.value= response.body()
                }else{
                    blameData.value= null
                }
            }

            override fun onFailure(call: Call<Response<Blame>>, t: Throwable) {
                blameData.value= null
            }
        })

    }

}