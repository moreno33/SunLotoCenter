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
import com.sunlotocenter.utils.*
import kotlinx.android.synthetic.main.blame_layout.view.*

class BlameAdapter(var blames: ArrayList<Blame>) : RecyclerView.Adapter<BlameAdapter.CustomViewHolder>() {

    lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        context= parent.context
        return CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.blame_layout, null))
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        var blame= blames[position]
        holder.txtAuthor.text= "${blame.author.firstName} ${blame.author.lastName}"
        if(blame.mesage.length>70){
            clickableHtmlString(holder.txtMessage, blame.mesage.substring(0, 70) + " <b>#li plis#</b>", object :ClickListener{
                override fun onClick(): Boolean {
                    holder.txtMessage.text= blame.mesage
                    return false
                }

            })
        }
        else holder.txtMessage.text= blame.mesage


        holder.txtWhen.text= getDateString(blame.createdDateTime!!)

    }


    override fun getItemCount(): Int {
        return blames.size
    }

    class CustomViewHolder(var item: View) : RecyclerView.ViewHolder(item){
        val txtAuthor by lazy { item.txtAuthor }
        val txtMessage by lazy { item.txtMessage }
        val txtWhen by lazy { item.txtTime }
    }
}