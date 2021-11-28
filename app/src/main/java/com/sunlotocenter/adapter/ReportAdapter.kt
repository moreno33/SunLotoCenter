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
import com.sunlotocenter.dao.Sex
import com.sunlotocenter.dao.User
import com.sunlotocenter.dao.UserStatus
import com.sunlotocenter.dto.Report
import com.sunlotocenter.listener.SaveUserListener
import com.sunlotocenter.utils.*
import kotlinx.android.synthetic.main.activity_admin_dashboard.*
import kotlinx.android.synthetic.main.employee_layout.view.*
import kotlinx.android.synthetic.main.report_layout.view.*

class ReportAdapter(var reports: ArrayList<Report>) : RecyclerView.Adapter<ReportAdapter.CustomViewHolder>() {

    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        context= parent.context
        return CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.report_layout, null))
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        var report= reports[position]
        holder.txtDateMorning.text= getDateString(report.`when`!!)
        holder.txtDateNight.text= getDateString(report.`when`!!)

        holder.txtEntryMorning.text= context.getString(R.string.enter_value, report.totalMorning?.toInt())
        holder.txtEntryNight.text= context.getString(R.string.enter_value, report.totalNight?.toInt())

        holder.txtOutMorning.text= if(report.winMorning!= null)
            context.getString(R.string.out_value, report.winMorning!!.toInt().toString()+"HTG") else
            context.getString(R.string.out_value, "-")
        holder.txtOutNight.text= if(report.winNight!= null)
            context.getString(R.string.out_value, report.winNight!!.toInt().toString()+"HTG") else
            context.getString(R.string.out_value, "-")

        val resultMorning= if(report.winMorning!= null) report.totalMorning!! - report.winMorning!! else null
        val resultNight= if(report.winNight!= null) report.totalNight!! - report.winNight!! else null

        if(resultMorning!= null){
            holder.txtAmountMorning.text= resultMorning.toInt().toString() +"HTG"
            if(resultMorning>0){
                holder.txtAmountMorning.setTextColor(ContextCompat.getColor(context, R.color.main_green))
            }else{
                holder.txtAmountMorning.setTextColor(ContextCompat.getColor(context, R.color.main_red))
            }
        }
        else{
            holder.txtAmountMorning.text= "-"
            holder.txtAmountMorning.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
        }

        if(resultNight!= null){
            holder.txtAmountNight.text= resultNight.toInt().toString() +"HTG"
            if(resultNight>0){
                holder.txtAmountNight.setTextColor(ContextCompat.getColor(context, R.color.main_green))
            }else{
                holder.txtAmountNight.setTextColor(ContextCompat.getColor(context, R.color.main_red))
            }
        }else{
            holder.txtAmountNight.text= "-"
            holder.txtAmountNight.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
        }
    }

    override fun getItemCount(): Int {
        return reports.size
    }

    class CustomViewHolder(var item: View) : RecyclerView.ViewHolder(item){
        val txtEntryMorning by lazy { item.txtEntryMorning }
        val txtOutMorning by lazy { item.txtOutMorning }
        val txtAmountMorning by lazy { item.txtAmountMorning }
        val txtDateMorning by lazy { item.txtDateMorning }
        val txtEntryNight by lazy { item.txtEntryNight }
        val txtOutNight by lazy { item.txtOutNight }
        val txtAmountNight by lazy { item.txtAmountNight }
        val txtDateNight by lazy { item.txtDateNight }

    }
}