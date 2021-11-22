package com.sunlotocenter.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.zawadz88.materialpopupmenu.popupMenu
import com.sunlotocenter.activity.ChangePasswordActivity
import com.sunlotocenter.activity.R
import com.sunlotocenter.activity.admin.AdminPersonalInfoActivity
import com.sunlotocenter.dao.*
import com.sunlotocenter.extensions.gameTypes
import com.sunlotocenter.utils.USER_EXTRA
import kotlinx.android.synthetic.main.employee_layout.view.*
import kotlinx.android.synthetic.main.game_schedule_layout.view.*

class GameScheduleAdapter(var schedules: ArrayList<GameSchedule>) : RecyclerView.Adapter<GameScheduleAdapter.CustomViewHolder>() {

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


        holder.txtGameNameMorning.text= spinnerItem?.name
        holder.txtEndTimeMorning.text= schedule.morningTime
        holder.txtGameNameNight.text= spinnerItem?.name
        holder.txtEndTimeNight.text= schedule.nightTime

        //Click to open menu
//        holder.imgMenu.setOnClickListener {
//            showMenu(it, employee)
//        }

    }

    private fun showMenu(view: View?, employee: User) {
        val popupMenu = popupMenu {
            section {
                item {
                    label = context.getString(R.string.addOrChangePassword)
                    icon = R.drawable.pencil_outline_black_18
                    callback = {
                        context.startActivity(Intent(context, ChangePasswordActivity::class.java).putExtra(
                            USER_EXTRA, employee))
                    }
                }
                item {
                    labelRes = R.string.block_user
                    icon = R.drawable.account_cancel_outline_black_18
                    callback = {

                    }
                }
                item {
                    labelRes = R.string.reprimand
                    icon = R.drawable.account_alert_outline_black_18
                    callback = {

                    }
                }
                item {
                    labelRes = R.string.balance
                    icon = R.drawable.wallet_outline_black_18
                    callback = {

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
//        var imgMenu= item.imgMenu
    }
}