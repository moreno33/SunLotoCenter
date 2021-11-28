package com.sunlotocenter.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.github.zawadz88.materialpopupmenu.popupMenu
import com.sunlotocenter.activity.ChangePasswordActivity
import com.sunlotocenter.activity.R
import com.sunlotocenter.activity.admin.GameScheduleActivity
import com.sunlotocenter.dao.*
import com.sunlotocenter.extensions.gameTypes
import com.sunlotocenter.utils.GAME_SCHEDULE_EXTRA
import com.sunlotocenter.utils.REFRESH_REQUEST_CODE
import com.sunlotocenter.utils.USER_EXTRA
import kotlinx.android.synthetic.main.game_schedule_layout.view.*

class GameScheduleAdapter(var schedules: ArrayList<GameSchedule>, var onGameScheduleChangeListener: OnGameScheduleChangeListener) : RecyclerView.Adapter<GameScheduleAdapter.CustomViewHolder>() {

    lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        context= parent.context
        return CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.game_schedule_layout, null))
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        var schedule= schedules[position]

        var spinnerItem:SpinnerItem?= null
        (context as AppCompatActivity).gameTypes().forEach {
            if(it.id== schedule.type!!.id)
                spinnerItem= it
        }

        if(schedule.status== GameScheduleStatus.ACTIVE){
            holder.vwStatus.background= ContextCompat.getDrawable(context, R.drawable.green_circle_background)
        }else{
            holder.vwStatus.background= ContextCompat.getDrawable(context, R.drawable.red_circle_background)
        }
        holder.txtGameNameMorning.text= spinnerItem?.name
        holder.txtEndTimeMorning.text= schedule.morningTime
        holder.txtGameNameNight.text= spinnerItem?.name
        holder.txtEndTimeNight.text= schedule.nightTime

//        Click to open menu
        holder.imgMenu.setOnClickListener {
            showMenu(it, schedule)
        }

    }

    private fun showMenu(view: View?, schedule: GameSchedule) {
        val popupMenu = popupMenu {
            section {
                item {
                    label = context.getString(R.string.change)
                    icon = R.drawable.pencil_outline_black_18
                    callback = {
                        (context as AppCompatActivity).startActivityForResult(Intent(context, GameScheduleActivity::class.java).putExtra(GAME_SCHEDULE_EXTRA, schedule), REFRESH_REQUEST_CODE)
                    }
                }
                item {
                    labelRes = if(schedule.status== GameScheduleStatus.BLOCK)R.string.to_unblock else R.string.to_block
                    icon = R.drawable.block_outline_18_black
                    callback = {
                        val scheduleCopy= schedule.clone() as GameSchedule
                        if (schedule.status== GameScheduleStatus.ACTIVE)
                            scheduleCopy.status= GameScheduleStatus.BLOCK
                        else
                            scheduleCopy.status= GameScheduleStatus.ACTIVE

                        onGameScheduleChangeListener.onChange(scheduleCopy)
                    }
                }
            }
        }

        popupMenu.show(context, view!!)
    }

    override fun getItemCount(): Int {
        return schedules.size
    }

    class CustomViewHolder(var item: View) : RecyclerView.ViewHolder(item){
        val txtGameNameMorning by lazy { item.txtGameNameMorning }
        val txtEndTimeMorning by lazy { item.txtEndTimeMorning }
        val txtGameNameNight by lazy { item.txtGameNameNight }
        val txtEndTimeNight by lazy { item.txtEndTimeNight }
        val imgMenu by lazy { item.imgMenu }
        val vwStatus by lazy { item.vwStatus }
    }

    interface OnGameScheduleChangeListener{
        fun  onChange(gameSchedule:GameSchedule)
    }
}