package com.sunlotocenter.service

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.text.Html
import android.webkit.MimeTypeMap
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.sunlotocenter.MyApplication
import com.sunlotocenter.R
import com.sunlotocenter.activity.admin.NotificationActivity
import com.sunlotocenter.dao.Admin
import com.sunlotocenter.dao.Response
import com.sunlotocenter.dao.User
import com.sunlotocenter.utils.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import java.io.File
import java.lang.Exception
import java.util.*

class NotificationService: FirebaseMessagingService(){
    private val TAG = "com.woolib.MS"

    val ACTION_CHAT = "com.woolib.ACTION_CHAT_SERVICE"
    val CHAT_EXTRA = "com.woolib.CHAT_EXTRA"

    val NOTIFICATION_CHANNEL_CHAT = "com.woolib.NOTIFICATION_CHANNEL_CHAT"

    val GENERAL_NOTIFICATION_CHANNEL_CHAT = "com.woolib.GENERAL_NOTIFICATION_CHANNEL_CHAT"

    val NOTIFICATION_CHANNEL_NAME = "SunLotoCenter"

    val ACTION_SEE_CHAT = "com.woolib.ACTION_SEE_CHAT"

    val ACTION_TYPING = "com.woolib.ACTION_TYPING"

    val ACCOUNT_EXTRA = "com.woolib.ACCOUNT_EXTRA"

    val ACTION_NOTIF = "com.woolib.ACTION_NOTIF"

    val ACTION_MESSAGE = "com.woolib.MESSAGE_EXTRA"

    val ADMIN_ACTION_TYPING = "com.woolib.ADMIN_ACTION_TYPING"

    val ACTION_SEE_MESSAGE = "com.woolib.ACTION_SEE_MESSAGE"

    /**
     * This is the notification manager.
     */
    private var notificationManager: NotificationManager? = null

    /**
     * This is a helper class.
     */
    private var notificationHelper: NotificationHelper? = null

    /**
     * For building notification
     */
    var builder: NotificationCompat.Builder? = null

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            val data = remoteMessage.data
            while (data.keys.iterator().hasNext()) {
                val key = data.keys.iterator().next()
                when (key) {
                    "notif" -> {
                        val notif = data["notif"]
                        //This is notification helper for chat
                        notificationHelper =
                            NotificationHelper(this, GENERAL_NOTIFICATION_CHANNEL_CHAT)

                        //We need to split the notif, so we can get the notification id
                        val datas = notif!!.split("#").toTypedArray()
                        var notificationId = 0L
                        try {
                            notificationId = java.lang.Long.valueOf(datas[0])
                        } catch (e: Exception) {
                        }

                        //No need to continue
                        if (notificationId == 0L) return
                        if (MyApplication.getInstance().isLoggedIn) {
                            notificationApi.getNotificationById(notificationId).enqueue(object :Callback<Response<com.sunlotocenter.dao.Notification>> {
                                override fun onResponse(
                                    call: Call<Response<com.sunlotocenter.dao.Notification>>,
                                    response: retrofit2.Response<Response<com.sunlotocenter.dao.Notification>>
                                ) {
                                    if (response.body() != null) {
                                        val notification = response.body()!!.data
                                        if (notification != null) {
                                            if(MyApplication.getInstance().isLoggedIn){

                                                if (notification.author== null){

                                                    if(MyApplication.getInstance().connectedUser is Admin){
                                                        val bigImage: String = MyApplication.BASE_URL + "/users/profile/picture/0"
                                                        val intent: Intent = Intent(
                                                            this@NotificationService,
                                                            NotificationActivity::class.java
                                                        ).putExtra(BROADCAST_NOTIF_EXTRA, notification)
                                                        sendNotification(getString(R.string.app_name),
                                                                        Html.fromHtml(datas[1]).toString(),
                                                                        bigImage,
                                                                        intent,
                                                                        notification.id!!.toInt(),
                                                                        "notif")
                                                    }

                                                }else{
                                                    if(MyApplication.getInstance().connectedUser.sequence.id!! != notification.author!!.sequence.id!!){
                                                        val bigImage: String = MyApplication.BASE_URL + "/users/profile/picture/0"
                                                        val intent: Intent = Intent(
                                                            this@NotificationService,
                                                            NotificationActivity::class.java
                                                        ).putExtra(BROADCAST_NOTIF_EXTRA, notification)
                                                        sendNotification(getString(R.string.app_name),
                                                                        Html.fromHtml(
                                                                            datas[1]).toString(),
                                                                        bigImage,
                                                                        intent,
                                                                        notification.id!!.toInt(),
                                                                        "notif")
                                                    }
                                                }
//                                                sendBroadcast(Intent(ACTION_NOTIF).setPackage(packageName))
                                            }
                                        }
                                    }
                                }

                                override fun onFailure(
                                    call: Call<Response<com.sunlotocenter.dao.Notification>>,
                                    t: Throwable
                                ) {}

                            })
                        }
                    }
                }
                break
            }
        }
    }

    private fun sendNotification(title: String,
                                message: String,
                                bigImage: String,
                                intent: Intent,
                                notificationId: Int,
                                group: String) {

        //This is for notification in higher version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager!!.createNotificationChannel(
                notificationHelper!!.createNotificationChannel(
                    NOTIFICATION_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH, false, 0
                )
            )
        }
        Glide.with(this)
            .asBitmap()
            .load(bigImage)
            .into(object : CustomTarget<Bitmap?>(100, 100) {

                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap?>?
                ) { builder = notificationHelper!!.createNotification(
                        title,
                        message,
                        R.drawable.sun,
                        resource,
                        NotificationCompat.PRIORITY_HIGH,
                        NotificationCompat.DEFAULT_ALL,
                        true
                    )

                    //Here is the grouping notification
                    val builderSummary = notificationHelper!!.createNotification(
                        title,
                        message,
                        R.mipmap.flag_soviet_union,
                        resource,
                        NotificationCompat.PRIORITY_HIGH,
                        NotificationCompat.DEFAULT_ALL,
                        true
                    )
                        .setGroup(group)
                        .setGroupSummary(true)
                        .setContentIntent(getPendingIntent(intent))
                        .build()
                    prepareNotification(builder, builderSummary, intent, notificationId, group)
                }
                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    builder = notificationHelper!!.createNotification(
                        title,
                        message,
                        R.drawable.sun,
                        null,
                        NotificationCompat.PRIORITY_HIGH,
                        NotificationCompat.DEFAULT_ALL,
                        true
                    )

                    //Here is the grouping notification
                    val builderSummary = notificationHelper!!.createNotification(
                        title,
                        message,
                        R.drawable.sun,
                        null,
                        NotificationCompat.PRIORITY_HIGH,
                        NotificationCompat.DEFAULT_ALL,
                        true
                    )
                        .setGroup(group)
                        .setGroupSummary(true)
                        .setContentIntent(getPendingIntent(intent))
                        .build()
                    prepareNotification(builder, builderSummary, intent, notificationId, group)
                }

                override fun onLoadCleared(placeholder: Drawable?) {}

            })
    }

    fun prepareNotification(builder: NotificationCompat.Builder?,
                            summary: Notification?,
                            intent: Intent?,
                            notificationId: Int,
                            group: String?
                        ) {
        builder!!.setGroup(group)
        builder.setOnlyAlertOnce(true)
        builder.setContentIntent(getPendingIntent(intent))
        notificationManager!!.notify(notificationId, builder.build())
        notificationManager!!.notify(0, summary)
    }

    private fun getPendingIntent(intent: Intent?): PendingIntent? {
        val stackBuilder = TaskStackBuilder.create(this)
        stackBuilder.addNextIntentWithParentStack((intent)!!)
        return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT)
    }


    override fun onNewToken(token: String) {
        //When there is a connected user we need to send the token on the server anytime that here is an update.
        if (MyApplication.getInstance().isLoggedIn()) {
            sendTokenToServer(token)
        }
    }

    private fun sendTokenToServer(token: String) {
        val user= MyApplication.getInstance().connectedUser
        user.fcmToken= token

        var profilePart: MultipartBody.Part? = null
        if (isNotEmpty(user.profilePath)) {
            if (!user.profilePath!!.contains("http")) {
                val profileFile: File = File(user.profilePath)
                val reqBody = RequestBody.create(
                    MediaType.parse(
                        getMimeType(
                            Uri.fromFile(profileFile).toString()
                        )
                    ), profileFile
                )
                profilePart =
                    MultipartBody.Part.createFormData("profile", profileFile.name, reqBody)
            }
        }
        /*
        Here we send the language of the device to the user.
        */
        /*
        Here we send the language of the device to the user.
        */
        val headers = HashMap<String, String>()
        headers["accept-language"] = Locale.getDefault().toString()

        val accountBody =
            RequestBody.create(MediaType.parse("application/json"), getGson().toJson(user))

        val call = userApi.saveUser(profilePart, accountBody, headers)

        call.enqueue(object : Callback<Response<User>> {
            override fun onResponse(call: Call<Response<User>>,
                                    response: retrofit2.Response<Response<User>>) {
            }
            override fun onFailure(call: Call<Response<User>>, t: Throwable) {
            }
        })
    }
}