package com.sunlotocenter.activity.admin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sunlotocenter.MyApplication
import com.sunlotocenter.activity.BasicActivity
import com.sunlotocenter.activity.ProtectedActivity
import com.sunlotocenter.R
import com.sunlotocenter.activity.AutoComposeActivity
import com.sunlotocenter.adapter.GameScheduleAdapter
import com.sunlotocenter.dao.Game
import com.sunlotocenter.dao.GameSchedule
import com.sunlotocenter.extensions.enableHome
import com.sunlotocenter.listener.LoadMoreListener
import com.sunlotocenter.model.GameViewModel
import com.sunlotocenter.utils.ClickListener
import com.sunlotocenter.utils.DialogType
import com.sunlotocenter.utils.REFRESH_REQUEST_CODE
import kotlinx.android.synthetic.main.activity_manage_game.*
import kotlinx.android.synthetic.main.activity_manage_game.progressBar
import kotlinx.android.synthetic.main.activity_manage_game.swpLayout
import kotlinx.android.synthetic.main.activity_manage_game.toolbar
import kotlinx.android.synthetic.main.activity_manage_game.txtInfo
import kotlinx.android.synthetic.main.activity_notification.*
import java.util.*
import kotlin.collections.ArrayList

class ManageGameActivity :
    ProtectedActivity(),
    LoadMoreListener.OnLoadMoreListener, GameScheduleAdapter.OnGameScheduleChangeListener{

    private var loadMoreListener: LoadMoreListener?= null
    private lateinit var gameScheduleAdapter: GameScheduleAdapter
    private lateinit var gameViewModel: GameViewModel
    private var isSaveState= false

    override fun getLayoutId(): Int {
        return R.layout.activity_manage_game
    }

    lateinit var activityResult:
            ActivityResultLauncher<Intent>

    override fun onStart() {
        super.onStart()
        activityResult= registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode== Activity.RESULT_OK){
                gameViewModel.loadSchedules(MyApplication.getInstance().company.sequence!!.id!!, true)
            }
        }
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
            activityResult.launch(Intent(this, GameScheduleActivity::class.java))
        }
        gameViewModel.loadSchedules(MyApplication.getInstance().company.sequence!!.id!!, true)
        swpLayout.isRefreshing= true
        swpLayout.setOnRefreshListener {
            gameViewModel.loadSchedules(MyApplication.getInstance().company.sequence!!.id!!, true)
        }
    }

    private fun setUpAdapter() {
        rclGameSchedule.setHasFixedSize(true)
        rclGameSchedule.layoutManager= LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        gameScheduleAdapter= GameScheduleAdapter(if(isSaveState) gameViewModel.schedules else arrayListOf(), this)
        gameViewModel.schedules.clear()

        rclGameSchedule.adapter= gameScheduleAdapter

//        registerForContextMenu(rclEmployees)

        observe()
        setLoadMoreListener()
    }

    private fun observe() {
        gameViewModel.lastAddedSchedulesData.observe(this,
            { schedules ->
                if(schedules.isEmpty() && gameViewModel.page== 0){
                    txtInfo.visibility= View.VISIBLE
                }else{
                    txtInfo.visibility= View.GONE
                }
                addSchedules(schedules)
                progressBar.progressiveStop()
                swpLayout.isRefreshing= false })
        gameViewModel.gameScheduleData.observe(this,
            {
                dialog.dismiss()
                if(it== null){
                    com.sunlotocenter.utils.showDialog(this@ManageGameActivity,
                        getString(R.string.internet_error_title),
                        getString(
                            R.string.internet_error_message
                        ),
                        getString(R.string.ok),
                        object : ClickListener {
                            override fun onClick(): Boolean {
                                return false
                            }

                        }, true, DialogType.ERROR)
                }else{
                    if(it.success){
                        com.sunlotocenter.utils.showDialog(this@ManageGameActivity,
                            getString(R.string.success_title),
                            getString(R.string.update_game_schedule_success_message ),
                            getString(R.string.ok),
                            object : ClickListener {
                                override fun onClick(): Boolean {
                                    gameViewModel.loadSchedules(MyApplication.getInstance().company.sequence!!.id!!, true)
                                    return false
                                }

                            }, false, DialogType.SUCCESS)
                    }else{
                        com.sunlotocenter.utils.showDialog(this@ManageGameActivity,
                            getString(R.string.internet_error_title), it.message!!,
                            getString(R.string.ok),
                            object : ClickListener {
                                override fun onClick(): Boolean {
                                    return false
                                }

                            }, false, DialogType.ERROR)
                    }
                }
            })
    }

    private fun addSchedules(schedules: ArrayList<GameSchedule>) {
        loadMoreListener?.setFinished(false)
        if(schedules.isEmpty()){
            loadMoreListener?.setFinished(true)
            loadMoreListener?.setLoaded()
            if(gameViewModel.page== 0){
                gameScheduleAdapter.apply {
                    schedules.clear()
                    notifyDataSetChanged()
                }
            }
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
        gameViewModel.loadSchedules(MyApplication.getInstance().company.sequence!!.id!!, false)
        progressBar.progressiveStart()
    }

    override fun onChange(gameSchedule: GameSchedule) {
        com.sunlotocenter.utils.showDialog(this, getString(R.string.to_block),
            getString(R.string.want_to_block_schedule),
            getString(R.string.yes), getString(R.string.no), object : ClickListener {
                override fun onClick(): Boolean {
                    dialog.show()
                    gameViewModel.saveGameSchedule(gameSchedule)
                    return false
                }

            }, object : ClickListener {
                override fun onClick(): Boolean {
                    return false
                }

            },
            false, DialogType.NEUTRAL
        )
    }
}