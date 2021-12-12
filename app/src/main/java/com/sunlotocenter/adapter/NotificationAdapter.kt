package com.sunlotocenter.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sunlotocenter.MyApplication
import com.sunlotocenter.R
import com.sunlotocenter.dao.*
import com.sunlotocenter.utils.*
import kotlinx.android.synthetic.main.notification_layout.view.*

class NotificationAdapter(var notifications: ArrayList<Notification>) : RecyclerView.Adapter<NotificationAdapter.CustomViewHolder>() {

    lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        context= parent.context
        return CustomViewHolder(LayoutInflater.from(context).inflate(R.layout.notification_layout, null))
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {

        var notification= notifications[position]
        var profilePicture:String?= null

        val connetedUser= MyApplication.getInstance().connectedUser
        if (connetedUser is Admin){
            if(notification.author== null){
                holder.txtName.text= context.getString(R.string.author_system)
                profilePicture= ""
            }else{
                if(connetedUser.sequence.id== notification.author!!.sequence.id) {
                    holder.txtName.text= context.getString(R.string.you)
                    profilePicture= notification.author!!.profilePath
                }
                else {
                    holder.txtName.text= "${notification.author!!.firstName} ${notification.author!!.lastName}"
                    profilePicture= notification.author!!.profilePath
                }
            }
        }else{
            if(notification.author== null){
                holder.txtName.text= context.getString(R.string.admin)
                profilePicture= ""
            }else{
                if(connetedUser.sequence.id== notification.author!!.sequence.id) {
                    holder.txtName.text= context.getString(R.string.you)
                    profilePicture= notification.author!!.profilePath
                } else {
                    holder.txtName.text= context.getString(R.string.admin)
                    profilePicture= ""
                }
            }
        }

        glide(context,
            profilePicture,
            holder.imgAuthor,
            R.drawable.background_gray,
            R.drawable.sun)

        if(notification.message!!.length>70){
            clickableHtmlString(holder.txtMessage, notification.message!!.substring(0, 70) + " <b>#li plis#</b>", object :ClickListener{
                override fun onClick(): Boolean {
                    holder.txtMessage.text= notification.message
                    return false
                }

            })
        }
        else holder.txtMessage.text= notification.message
        holder.txtTime.text= getTimeString(context, notification.updatedDateTime!!)

        if (position== itemCount-1) holder.view.visibility= View.GONE
        else holder.view.visibility= View.VISIBLE
    }


    override fun getItemCount(): Int {
        return notifications.size
    }

    class CustomViewHolder(var item: View) : RecyclerView.ViewHolder(item){
        val txtName: TextView by lazy { itemView.txtName }
        val txtMessage: TextView by lazy { itemView.txtMessage }
        val txtTime by lazy { itemView.txtTime }
        val imgAuthor by lazy { item.imgAuthor}
        val view by lazy { item.view }
    }
}