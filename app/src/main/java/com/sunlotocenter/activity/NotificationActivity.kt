package com.sunlotocenter.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.sunlotocenter.adapter.AdminNotificationAdapter
import com.sunlotocenter.dao.Notification
import com.sunlotocenter.extensions.enableHome
import kotlinx.android.synthetic.main.activity_notification.*

class NotificationActivity : ProtectedActivity() {

    override fun getLayoutId(): Int {
        return R.layout.activity_notification
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableHome(toolbar)

        //To be moved after
        val notifications= listOf(Notification("Nou mande tout moun pou yo rete jodia a 10h", "2mn ago"),
            Notification("Nap anonse tout moun ke gen yon reyinyon jodia se enpoòtan", "10mn ago"))

        val adapter= AdminNotificationAdapter(notifications)
        rclNotification.layoutManager= LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rclNotification.adapter= adapter

    }
}