package com.sunlotocenter.activity.admin

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sunlotocenter.MyApplication
import com.sunlotocenter.activity.ProtectedActivity
import com.sunlotocenter.R
import com.sunlotocenter.adapter.NotificationAdapter
import com.sunlotocenter.dao.Blame
import com.sunlotocenter.dao.Notification
import com.sunlotocenter.dao.Response
import com.sunlotocenter.extensions.enableHome
import com.sunlotocenter.listener.LoadMoreListener
import com.sunlotocenter.model.NotificationViewModel
import com.sunlotocenter.utils.*
import kotlinx.android.synthetic.main.activity_notification.*

class NotificationActivity :
    ProtectedActivity(),
    LoadMoreListener.OnLoadMoreListener {

    private var loadMoreListener: LoadMoreListener?= null
    private lateinit var notificationAdapter: NotificationAdapter
    private lateinit var notificationViewModel: NotificationViewModel
    private var isSaveState= false

    override fun getLayoutId(): Int {
        return R.layout.activity_notification
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableHome(toolbar)
        notificationViewModel= ViewModelProvider(this, SavedStateViewModelFactory(application, this))
            .get(NotificationViewModel::class.java)

        if(savedInstanceState!=  null){
            isSaveState= true
            notificationViewModel.loadSaveState()
        }
        setUpAdapter()
        notificationViewModel.loadNotifications(MyApplication.getInstance().connectedUser.sequence!!.id!!,
            MyApplication.getInstance().company.sequence!!.id!!, true)

        swpLayout.isRefreshing= true
        swpLayout.setOnRefreshListener {
            notificationViewModel.loadNotifications(MyApplication.getInstance().connectedUser.sequence!!.id!!,
                MyApplication.getInstance().company.sequence!!.id!!,true)
        }

    }

    private fun setUpAdapter() {
        rclNotification.setHasFixedSize(true)
        rclNotification.layoutManager= LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        notificationAdapter= NotificationAdapter(
            if(isSaveState) notificationViewModel.notifications else arrayListOf()
        )
        notificationViewModel.notifications.clear()

        rclNotification.adapter= notificationAdapter

        observe()
        setLoadMoreListener()
    }

    private fun observe() {
        notificationViewModel.lastAddedNotificationsData.observe(this,
            { notifications ->
                if(notifications.isEmpty() && notificationViewModel.page== 0){
                    txtInfo.visibility= View.VISIBLE
                }else{
                    txtInfo.visibility= View.GONE
                }
                addNotifications(notifications)
                progressBar.progressiveStop()
                swpLayout.isRefreshing= false
            })
    }

    private fun addNotifications(notifications: ArrayList<Notification>) {
        loadMoreListener?.setFinished(false)
        if(notifications.isEmpty()){
            loadMoreListener?.setFinished(true)
            loadMoreListener?.setLoaded()
            return
        }
        val isFirstPage= notificationViewModel.page== 0
        if(notifications.size< LoadMoreListener.SIZE_PER_PAGE)
            loadMoreListener?.setFinished(true)
        val lastPosition= notificationAdapter.notifications.size
        if(isFirstPage)
            notificationAdapter.notifications.clear()

        notificationAdapter.notifications.addAll(notifications)

        if(isFirstPage){
            notificationAdapter.notifyDataSetChanged()
        } else{
            notificationAdapter.notifyItemRangeInserted(lastPosition, notifications.size)
        }
        loadMoreListener?.setLoaded()

    }

    private fun setLoadMoreListener() {
        loadMoreListener?.let { rclNotification.removeOnScrollListener(loadMoreListener!!) }
        loadMoreListener=  LoadMoreListener(rclNotification.layoutManager as LinearLayoutManager)
        loadMoreListener?.setOnLoadMoreListener(this)
        rclNotification.addOnScrollListener(loadMoreListener!!)
    }

    override fun onLoadMore() {
        notificationViewModel.loadNotifications(MyApplication.getInstance().connectedUser.sequence!!.id!!,
            MyApplication.getInstance().company.sequence!!.id!!,false)
        progressBar.progressiveStart()
    }

}