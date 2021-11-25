package com.sunlotocenter.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.github.zawadz88.materialpopupmenu.popupMenu
import com.sunlotocenter.activity.ChangePasswordActivity
import com.sunlotocenter.activity.R
import com.sunlotocenter.dao.*
import com.sunlotocenter.extensions.gameTypes
import com.sunlotocenter.utils.USER_EXTRA
import com.sunlotocenter.utils.getDateTimeString
import kotlinx.android.synthetic.main.block_list_layout.view.*
import kotlinx.android.synthetic.main.employee_layout.view.*
import kotlinx.android.synthetic.main.game_schedule_layout.view.*

class BlockedGameAdapter(var blockedGames: ArrayList<BlockedGame>, var onChangeBlockedGameListener: OnChangeBlockedGameListener) : RecyclerView.Adapter<BlockedGameAdapter.CustomViewHolder>() {

    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        context= parent.context
        return CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.block_list_layout, null))
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        var blockedGame= blockedGames[position]

        var spinnerItem:SpinnerItem?= null
        (context as AppCompatActivity).gameTypes().forEach {
            if(it.id== blockedGame.type?.id)
                spinnerItem= it
        }
        holder.txtCode.text= blockedGame.number
        holder.txtGame.text= getGameName(blockedGame.number, context)
        holder.txtType.text= spinnerItem?.name
        holder.txtCreationDate.text= getDateTimeString(context, blockedGame.createdDateTime!!)

        holder.imgDelete.setOnClickListener {
            onChangeBlockedGameListener.onChange(blockedGame)
        }

    }

    private fun getGameName(number: String, context: Context): String? {
        return if(number.length== 2) return context.getString(R.string.borlet)
        else if(number.length== 5) return context.getString(R.string.marriage)
        else if(number.length== 3) return context.getString(R.string.loto3)
        else if(number.length== 4) return context.getString(R.string.loto4)
        else context.getString(R.string.loto5)
    }



    override fun getItemCount(): Int {
        return blockedGames.size
    }

    class CustomViewHolder(var item: View) : RecyclerView.ViewHolder(item){
        val txtCode by lazy { item.txtCode }
        val txtGame by lazy { item.txtGame }
        val txtType by lazy { item.txtType }
        val txtCreationDate by lazy { item.txtCreationDate }
        val imgDelete by lazy { item.imgDelete }
    }

    interface OnChangeBlockedGameListener{
        fun onChange(blockedGame: BlockedGame)
    }
}