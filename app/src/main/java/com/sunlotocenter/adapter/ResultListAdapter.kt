package com.sunlotocenter.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.github.zawadz88.materialpopupmenu.popupMenu
import com.sunlotocenter.MyApplication
import com.sunlotocenter.activity.ChangePasswordActivity
import com.sunlotocenter.activity.R
import com.sunlotocenter.activity.admin.AdminPersonalInfoActivity
import com.sunlotocenter.activity.admin.BlameListActivity
import com.sunlotocenter.activity.admin.ResultActivity
import com.sunlotocenter.dao.GameResult
import com.sunlotocenter.dao.Sex
import com.sunlotocenter.dao.User
import com.sunlotocenter.dao.UserStatus
import com.sunlotocenter.dto.Result
import com.sunlotocenter.listener.SaveUserListener
import com.sunlotocenter.utils.*
import kotlinx.android.synthetic.main.activity_admin_dashboard.*
import kotlinx.android.synthetic.main.employee_layout.view.*
import kotlinx.android.synthetic.main.result_header_layout.view.*
import kotlinx.android.synthetic.main.result_layout.view.*

class ResultListAdapter(var results: ArrayList<Result>) :
    RecyclerView.Adapter<ResultListAdapter.CustomViewHolder>() {

    private val HEADER= 0
    private val ITEM= 1

    lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        context= parent.context

        if(viewType== ITEM) return CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.result_layout, null))
        else return CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.result_header_layout, null))
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        var result= results[position]
        if(getItemViewType(position)== HEADER){
            var resultHeader= if(result.night== null) result.morning else result.night
            holder.txtFirst.text= resultHeader?.lo1
            holder.txtSecond.text= resultHeader?.lo2
            holder.txtThird.text= resultHeader?.lo3
            holder.imgEdit.setOnClickListener {
                (context as AppCompatActivity).startActivityForResult(
                    Intent(context, ResultActivity::class.java).putExtra(RESULT_EXTRA, resultHeader),
                    REFRESH_REQUEST_CODE)
            }
        }
        else{
            holder.txtLo1Morning.text= result.morning?.lo1?:"-"
            holder.txtLo2Morning.text= result.morning?.lo2?:"-"
            holder.txtLo3Morning.text= result.morning?.lo3?:"-"
            holder.txtDateMorning.text= result.morning?.let{ getDateString(result.morning!!.resultDate!!)}?:"-"

            holder.txtLo1Night.text= result.night?.lo1?:"-"
            holder.txtLo2Night.text= result.night?.lo2?:"-"
            holder.txtLo3Night.text= result.night?.lo3?:"-"
            holder.txtDateNight.text= result.morning?.let{ getDateString(result.morning!!.resultDate!!)}?:"-"
        }
    }


    override fun getItemViewType(position: Int): Int {
        if (position== 0) return HEADER
        else return ITEM
    }

    override fun getItemCount(): Int {
        return results.size
    }

    class CustomViewHolder(var item: View) : RecyclerView.ViewHolder(item){
        val txtLo1Morning by lazy { item.txtLo1Morning }
        val txtLo2Morning by lazy { item.txtLo2Morning }
        val txtLo3Morning by lazy { item.txtLo3Morning }
        val txtDateMorning by lazy { item.txtDateMorning }
        val txtLo1Night by lazy { item.txtLo1Night }
        val txtLo2Night by lazy { item.txtLo2Night }
        val txtLo3Night by lazy { item.txtLo3Night }
        val txtDateNight by lazy { item.txtDateNight }
        val txtFirst by lazy { item.txtFirst }
        val txtSecond by lazy { item.txtSecond }
        val txtThird by lazy { item.txtThird }
        val imgEdit by lazy { item.imgEdit }
    }
}