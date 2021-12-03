package com.sunlotocenter.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sunlotocenter.activity.R
import com.sunlotocenter.dao.*
import com.sunlotocenter.extensions.gameTypes
import com.sunlotocenter.utils.ClickListener
import com.sunlotocenter.utils.DialogType
import com.sunlotocenter.utils.getTimeString
import com.sunlotocenter.utils.showDialog
import kotlinx.android.synthetic.main.game_schedule_session_layout.view.*
import kotlinx.android.synthetic.main.valid_error_layout.view.*
import org.joda.time.DateTimeZone
import org.joda.time.LocalTime
import org.w3c.dom.Text
import kotlin.collections.ArrayList

class GameScheduleSessionAdapter(var schedules: ArrayList<GameScheduleSession>,
                                 var gameScheduleSessionListener: GameScheduleSessionListener, var showOpenStatus:Boolean= true) :
    RecyclerView.Adapter<GameScheduleSessionAdapter.CustomViewHolder>() {

    lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        context= parent.context
        return CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.game_schedule_session_layout, null))
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        var schedule= schedules[position]

        var spinnerItem:SpinnerItem?= null
        (context as AppCompatActivity).gameTypes().forEach {
            if(it.id== schedule.gameSchedule.type!!.id)
                spinnerItem= it
        }
        if(schedule.gameSession== GameSession.MORNING) {
            holder.txtGame.text= "${spinnerItem!!.name} --- ${context.getString(R.string.morning)} ${ if(showOpenStatus)"(${getTimeString(schedule.gameSchedule.morningTime!!, DateTimeZone.getDefault())})" else ""}"
            Glide
                .with(context)
                .load(R.drawable.sun)
                .into(holder.imgSession)
        }
        else {
            holder.txtGame.text= "${spinnerItem!!.name} --- ${context.getString(R.string.night)} ${ if(showOpenStatus)"(${getTimeString(schedule.gameSchedule.nightTime!!, DateTimeZone.getDefault())})" else ""}"
            Glide
                .with(context)
                .load(R.drawable.moon)
                .into(holder.imgSession)
        }

        if (showOpenStatus)holder.txtStatus.visibility= View.VISIBLE
        else holder.txtStatus.visibility= View.GONE

        if(schedule.gameSession== GameSession.MORNING){
            if(LocalTime.now().isAfter(schedule.gameSchedule.morningTime!!
                    .minusMinutes(schedule.gameSchedule.secInterval!!.toInt()))){
                holder.txtStatus.text= context.getString(R.string.game_close)

                holder.txtStatus.setTextColor(ContextCompat.getColor(context, R.color.main_red))
                holder.parent.setOnClickListener {
                    if (showOpenStatus) showClosedGameMessage()
                    else gameScheduleSessionListener.onClick(schedule)
                }
            }else{
                holder.txtStatus.text= context.getString(R.string.game_open)
                holder.txtStatus.setTextColor(ContextCompat.getColor(context, R.color.main_green))
                holder.parent.setOnClickListener {
                    gameScheduleSessionListener.onClick(schedule)
                }
            }
        }else{
            if(LocalTime.now().isAfter(schedule.gameSchedule.nightTime!!
                    .minusMinutes(schedule.gameSchedule.secInterval!!.toInt()))){
                holder.txtStatus.text= context.getString(R.string.game_close)
                holder.txtStatus.setTextColor(ContextCompat.getColor(context, R.color.main_red))
                holder.parent.setOnClickListener {
                    if (showOpenStatus) showClosedGameMessage()
                    else gameScheduleSessionListener.onClick(schedule)
                }
            }else{
                holder.txtStatus.text= context.getString(R.string.game_open)
                holder.txtStatus.setTextColor(ContextCompat.getColor(context, R.color.main_green))
                holder.parent.setOnClickListener {
                    gameScheduleSessionListener.onClick(schedule)
                }
            }
        }
    }

    private fun showClosedGameMessage() {
        showDialog(context, context.getString(R.string.internet_error_title),
            context.getString(R.string.game_close_error_message),
            context.getString(R.string.ok), object :ClickListener{
                override fun onClick(): Boolean {
                    return false
                }

            }, true, DialogType.ERROR)
    }


    override fun getItemCount(): Int {
        return schedules.size
    }

    inner class CustomViewHolder(var item: View) : RecyclerView.ViewHolder(item){
        val imgSession by lazy { item.imgSession }
        val txtGame by lazy { item.txtGame }
        val txtStatus by lazy { item.txtStatus }
        val parent by lazy { item.rltParent }
    }

    class GameScheduleSession(var gameSchedule: GameSchedule, var gameSession: GameSession)

    //To transfer the selected one to the parent activity
    interface GameScheduleSessionListener{
        fun onClick(gameScheduleSession: GameScheduleSession)
    }
}