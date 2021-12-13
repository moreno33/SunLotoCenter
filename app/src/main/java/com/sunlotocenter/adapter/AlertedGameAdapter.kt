package com.sunlotocenter.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.sunlotocenter.R
import com.sunlotocenter.dao.*
import com.sunlotocenter.dto.GametDto
import com.sunlotocenter.extensions.gameTypes
import kotlinx.android.synthetic.main.alert_list_layout.view.*

class AlertedGameAdapter(var alertedGames: ArrayList<GametDto>) :
    RecyclerView.Adapter<AlertedGameAdapter.CustomViewHolder>() {

    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        context= parent.context
        return CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.alert_list_layout, null))
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        var alertedGame= alertedGames[position]

        var spinnerItem:SpinnerItem?= null
        (context as AppCompatActivity).gameTypes().forEach {
            if(it.id== alertedGame.type?.id)
                spinnerItem= it
        }
        holder.txtCode.text= alertedGame.number;
        holder.txtPrice.text= context.getString(R.string.price_currency, alertedGame.amount)
        holder.txtType.text= spinnerItem?.name
        holder.txtSession.text= alertedGame.session!!.id

    }

    override fun getItemCount(): Int {
        return alertedGames.size
    }

    class CustomViewHolder(var item: View) : RecyclerView.ViewHolder(item){
        val txtCode by lazy { item.txtCode }
        val txtPrice by lazy { item.txtPrice }
        val txtType by lazy { item.txtType }
        val txtSession by lazy { item.txtSession }
    }
}