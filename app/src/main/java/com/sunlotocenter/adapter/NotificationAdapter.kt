package com.sunlotocenter.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sunlotocenter.activity.R
import com.sunlotocenter.dao.Notification
import kotlinx.android.synthetic.main.notification_layout.view.*
import java.util.*

class AdminNotificationAdapter (var notifications: List<Notification>) : RecyclerView.Adapter<AdminNotificationAdapter.CustomVH>() {

    private var ctx: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomVH {
        this.ctx = parent.context
        var view= LayoutInflater.from(ctx).inflate(R.layout.notification_layout, null)

        return CustomVH(view!!)
    }

    override fun onBindViewHolder(holder: CustomVH, position: Int) {
        val notification = notifications[holder.adapterPosition]
        if (notification != null) {
            holder.txtMessage.text= notification.message
            holder.txtTime.text= notification.time
        }
    }

    override fun getItemCount(): Int {
        return notifications.size
    }


    /**
     * This is the viewholder for every single row that I will
     * have on my recyclerview.
     */
    inner class CustomVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtName: TextView by lazy { itemView.txtName }
        val txtMessage: TextView by lazy { itemView.txtMessage }
        val txtTime by lazy { itemView.txtTime }
    }
}
