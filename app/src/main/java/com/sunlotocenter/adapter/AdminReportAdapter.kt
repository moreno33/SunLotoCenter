package com.sunlotocenter.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.sunlotocenter.R
import com.sunlotocenter.activity.SlotListActivity
import com.sunlotocenter.dao.GameSession
import com.sunlotocenter.dao.Report
import com.sunlotocenter.utils.*
import kotlinx.android.synthetic.main.report_header_layout.view.txtAmount
import kotlinx.android.synthetic.main.report_header_layout.view.txtBalance
import kotlinx.android.synthetic.main.report_header_layout.view.txtEnter
import kotlinx.android.synthetic.main.report_header_layout.view.txtOut
import kotlinx.android.synthetic.main.report_layout.view.*
import org.joda.time.DateTimeZone

class AdminReportAdapter(
    var reports: ArrayList<Report>,
    var onOpenReportListener: OnOpenReportListener
) : RecyclerView.Adapter<AdminReportAdapter.CustomViewHolder>() {

    private val ITEM: Int= 0;
    private val HEADER:Int= 1;
    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        context= parent.context
        return CustomViewHolder(LayoutInflater.from(context).inflate(if(viewType== ITEM) R.layout.report_layout else R.layout.report_header_layout, null))
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        var report= reports[position]
        if(getItemViewType(position)== ITEM){
            holder.txtDateMorning.text= getDateString(report.reportDate!!, DateTimeZone.forID("America/New_York"))
            holder.txtDateNight.text= getDateString(report.reportDate!!, DateTimeZone.forID("America/New_York"))

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
                holder.txtAmountMorning.text= context.getString(R.string.price_currency, resultMorning)
                if(resultMorning>=0){
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
                holder.txtAmountNight.text= context.getString(R.string.price_currency, resultNight)
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
                onOpenReportListener.onOpen(GameSession.MORNING, report)
            }
            holder.imgOpenNight.setOnClickListener {
                onOpenReportListener.onOpen(GameSession.NIGHT, report)
            }
        }else{
            val totalReport= report.totalReport
            holder.txtBalance.text= context.getString(R.string.balance_value,
                totalReport?.balance?.toFloat() ?: 0f
            )
            holder.txtEnter.text= context.getString(R.string.enter_value, if(totalReport!= null) totalReport.entry.toFloat() else 0f)
            holder.txtOut.text= context.getString(R.string.out_value,
                totalReport?.out?.toFloat() ?: 0f
            )
            holder.txtAmount.text= if(totalReport!= null) String.format("%.0f", (totalReport.entry-totalReport.out)) else "0"
        }

    }

    override fun getItemViewType(position: Int): Int {
        if (reports.isNotEmpty() && reports[position].totalReport!= null)
            return HEADER
        else return ITEM
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

        //header
        val txtBalance by lazy { item.txtBalance }
        val txtEnter by lazy { item.txtEnter }
        val txtOut by lazy { item.txtOut }
        val txtAmount by lazy { item.txtAmount }
    }

    interface OnOpenReportListener{
        fun onOpen(gameSession: GameSession, report: Report)
    }
}