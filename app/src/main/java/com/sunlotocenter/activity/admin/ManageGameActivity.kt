package com.sunlotocenter.activity.admin

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sunlotocenter.activity.BasicActivity
import com.sunlotocenter.activity.ProtectedActivity
import com.sunlotocenter.activity.R
import com.sunlotocenter.adapter.GameScheduleAdapter
import com.sunlotocenter.dao.GameSchedule
import com.sunlotocenter.extensions.enableHome
import com.sunlotocenter.listener.LoadMoreListener
import com.sunlotocenter.model.GameViewModel
import kotlinx.android.synthetic.main.activity_manage_game.*
import kotlinx.android.synthetic.main.activity_manage_game.toolbar

class ManageGameActivity :
    ProtectedActivity(),
    LoadMoreListener.OnLoadMoreListener {

    private var loadMoreListener: LoadMoreListener?= null
    private lateinit var gameScheduleAdapter: GameScheduleAdapter
    private lateinit var gameViewModel: GameViewModel
    private var isSaveState= false

    override fun getLayoutId(): Int {
        return R.layout.activity_manage_game
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableHome(toolbar)
        gameViewModel= ViewModelProvider(this, SavedStateViewModelFactory(application, this))
            .get(GameViewModel::class.java)

        if(savedInstanceState!=  null){
            isSaveState= true
            gameViewModel.loadSaveState()
        }

        setUpAdapter()

        btnAdd.setOnClickListener {
            startActivityForResult(Intent(this, GameScheduleActivity::class.java), GameScheduleActivity.GAME_SCHEDULE_REQUEST_CODE)
        }
    }

    override fun onResume() {
        super.onResume()
        gameViewModel.loadSchedules()
    }

    private fun setUpAdapter() {
        rclGameSchedule.setHasFixedSize(true)
        rclGameSchedule.layoutManager= LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        gameScheduleAdapter= GameScheduleAdapter(
            if(isSaveState) gameViewModel.schedules else arrayListOf()
        )
        gameViewModel.schedules.clear()

        rclGameSchedule.adapter= gameScheduleAdapter

//        registerForContextMenu(rclEmployees)

        observe()
        setLoadMoreListener()
    }

    private fun observe() {
        gameViewModel.lastAddedSchedulesData.observe(this,
            { schedules -> addSchedules(schedules) })
    }

    private fun addSchedules(schedules: ArrayList<GameSchedule>) {
        loadMoreListener?.setFinished(false)
        if(schedules.isEmpty()){
            loadMoreListener?.setFinished(true)
            loadMoreListener?.setLoaded()
            return
        }
        val isFirstPage= gameViewModel.page== 0
        if(schedules.size< LoadMoreListener.SIZE_PER_PAGE)
            loadMoreListener?.setFinished(true)
        val lastPosition= gameScheduleAdapter.schedules.size
        if(isFirstPage)
            gameScheduleAdapter.schedules.clear()

        gameScheduleAdapter.schedules.addAll(schedules)

        if(isFirstPage){
            gameScheduleAdapter.notifyDataSetChanged()
        } else{
            gameScheduleAdapter.notifyItemRangeInserted(lastPosition, schedules.size)
        }
        loadMoreListener?.setLoaded()

    }

    private fun setLoadMoreListener() {
        loadMoreListener?.let { rclGameSchedule.removeOnScrollListener(loadMoreListener!!) }
        loadMoreListener=  LoadMoreListener(rclGameSchedule.layoutManager as LinearLayoutManager)
        loadMoreListener?.setOnLoadMoreListener(this)
        rclGameSchedule.addOnScrollListener(loadMoreListener!!)
    }

    override fun onLoadMore() {
        gameViewModel.loadSchedules()
    }
}