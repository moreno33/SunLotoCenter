package com.sunlotocenter.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.sunlotocenter.activity.R
import com.sunlotocenter.activity.SlotListActivity
import com.sunlotocenter.dao.GameSession
import com.sunlotocenter.dao.Report
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
        holder.txtDateMorning.text= getDateString(report.reportDate!!)
        holder.txtDateNight.text= getDateString(report.reportDate!!)

        holder.txtEntryMorning.text= context.getString(R.string.enter_value, report.totalMorning)
        holder.txtEntryNight.text= context.getString(R.string.enter_value, report.totalNight)

        holder.txtOutMorning.text= if(report.winMorning!= null)
            context.getString(R.string.out_value, report.winMorning) else
            context.getString(R.string.out_value, 0f)
        holder.txtOutNight.text= if(report.winNight!= null)
            context.getString(R.string.out_value, report.winNight!!) else
            context.getString(R.string.out_value, 0f)

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
            if(resultNight>=0){
                holder.txtAmountNight.setTextColor(ContextCompat.getColor(context, R.color.main_green))
            }else{
                holder.txtAmountNight.setTextColor(ContextCompat.getColor(context, R.color.main_red))
            }
        }else{
            holder.txtAmountNight.text= "-"
            holder.txtAmountNight.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
        }
        holder.imgOpenMorning.setOnClickListener {
            val intent= Intent(context, SlotListActivity::class.java)
            intent.putExtra(GAME_SESSION_EXTRA, GameSession.MORNING)
            intent.putExtra(REPORT_EXTRA, report)
            context.startActivity(intent)
        }
        holder.imgOpenNight.setOnClickListener {
            val intent= Intent(context, SlotListActivity::class.java)
            intent.putExtra(GAME_SESSION_EXTRA, GameSession.NIGHT)
            intent.putExtra(REPORT_EXTRA, report)
            context.startActivity(intent)
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
        val imgOpenMorning by lazy { item.imgOpenMorning }
        val imgOpenNight by lazy { item.imgOpenNight }

    }
}