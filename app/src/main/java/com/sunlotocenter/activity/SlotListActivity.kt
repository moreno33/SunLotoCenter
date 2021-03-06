package com.sunlotocenter.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.application.isradeleon.thermalprinter.ConnectBluetoothActivity
import com.sunlotocenter.MyApplication
import com.sunlotocenter.R
import com.sunlotocenter.activity.admin.AdminReportActivity
import com.sunlotocenter.adapter.SlotListAdapter
import com.sunlotocenter.dao.*
import com.sunlotocenter.extensions.enableHome
import com.sunlotocenter.listener.LoadMoreListener
import com.sunlotocenter.model.GameViewModel
import com.sunlotocenter.utils.*
import com.sunlotocenter.validator.Form
import kotlinx.android.synthetic.main.activity_slot_list.*
import kotlinx.android.synthetic.main.activity_slot_list.progressBar
//import kotlinx.android.synthetic.main.activity_slot_list.swpLayout
import kotlinx.android.synthetic.main.activity_slot_list.toolbar
import kotlinx.android.synthetic.main.activity_slot_list.txtInfo
import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormat

class SlotListActivity : ProtectedActivity(),
    LoadMoreListener.OnLoadMoreListener, SlotListAdapter.OnUpdateSlot{

    private var loadMoreListener: LoadMoreListener?= null
    private lateinit var slotListAdapter: SlotListAdapter
    private lateinit var gameViewModel: GameViewModel
    private var isSaveState= false
    private var gameSession: GameSession?= null
    private var report: Report?= null
    private var form= Form()
    private lateinit var slotListExtra:ArrayList<Slot>
    private var user: User?= null
    private lateinit var  type:GameType


    override fun getLayoutId(): Int {
        return R.layout.activity_slot_list
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableHome(toolbar)

        user= intent.getSerializableExtra(USER_EXTRA) as User?
        type= intent.getSerializableExtra(GAME_TYPE_EXTRA) as GameType

        gameViewModel= ViewModelProvider(this, SavedStateViewModelFactory(application, this))
            .get(GameViewModel::class.java)

        if(savedInstanceState!=  null){
            isSaveState= true
            gameViewModel.loadSaveState()
        }
        gameSession= intent.getSerializableExtra(GAME_SESSION_EXTRA) as GameSession
        report= intent.getSerializableExtra(REPORT_EXTRA) as Report

        supportActionBar?.title= getString(R.string.receipt, "${getDateString(report!!.reportDate!!)} (${gameSession!!.id})")

        setUpAdapter()

        if(user== null)
            gameViewModel.loadSlots(MyApplication.getInstance().company.sequence!!.id!!, true,
                getDateString(report!!.reportDate, DateTimeFormat.forPattern("MM-dd-yyyy"),
                    DateTimeZone.forID("America/New_York"))!!, gameSession!!, type, "")
        else
            gameViewModel.loadIndSlots(MyApplication.getInstance().company.sequence!!.id!!, true, getDateString(report!!.reportDate, DateTimeFormat.forPattern("MM-dd-yyyy"),
                DateTimeZone.forID("America/New_York"))!!, user!!.sequence!!.id!!, gameSession!!, type, "")

//        swpLayout.isRefreshing= true
//        swpLayout.setOnRefreshListener {
//            gameViewModel.loadSlots(true, report!!.sequence.id!!, gameSession!!)
//        }

        imgSearch.setOnClickListener {
            dialog.show()
            search(edxCode.text.toString().trim())
        }
    }

    private fun search(code: String) {
        if(user == null)
            gameViewModel.loadSlots(MyApplication.getInstance().company.sequence!!.id!!, true,
                getDateString(report!!.reportDate, DateTimeFormat.forPattern("MM-dd-yyyy"),
                    DateTimeZone.forID("America/New_York"))!!, gameSession!!, type, code)
        else
            gameViewModel.loadIndSlots(MyApplication.getInstance().company.sequence!!.id!!, true,
                getDateString(report!!.reportDate, DateTimeFormat.forPattern("MM-dd-yyyy"),
                    DateTimeZone.forID("America/New_York"))!!,
                user!!.sequence!!.id!!, gameSession!!, type, code)

    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
//        gameViewModel.saveState()
        super.onSaveInstanceState(outState, outPersistentState)
    }

    private fun setUpAdapter() {
        slotListAdapter= SlotListAdapter(
            if(isSaveState) gameViewModel.slots else arrayListOf(), this)
        gameViewModel.slots.clear()

        rclSlot.apply {
            setHasFixedSize(true)
            layoutManager= LinearLayoutManager(this@SlotListActivity, LinearLayoutManager.VERTICAL, false)
            adapter= slotListAdapter
        }
        observe()
        setLoadMoreListener()
    }

    private fun observe() {
        gameViewModel.lastAddedSlotsData.observe(this,
            { slots ->
                dialog.dismiss()
                addResults(slots)
                progressBar.progressiveStop()
//                swpLayout.isRefreshing= false
            })

        gameViewModel.slotResponseData.observe(this,
            {
                dialog.dismiss()
                if(it== null){
                    showDialog(this@SlotListActivity,
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
                    showDialog(this@SlotListActivity,
                        getString(
                            if(it.success)R.string.success_title else R.string.internet_error_title
                        ),
                        if (it.success) getString(R.string.game_update_success_message) else it.message!!
                        ,
                        getString( R.string.ok),
                        object : ClickListener {
                            override fun onClick(): Boolean {
                                setResult(Activity.RESULT_OK)
                                finish()
                                return false
                            }

                        },
                        false,
                        if(it.success) DialogType.SUCCESS else DialogType.ERROR)
                }
            })


    }

    private fun addResults(slots: ArrayList<Slot>) {
        loadMoreListener?.setFinished(false)
        if(slots.isEmpty()){
            if(gameViewModel.page== 0){
                slotListAdapter.slots.clear()
                slotListAdapter.notifyDataSetChanged()
                txtInfo.visibility= View.VISIBLE
            }else{
                txtInfo.visibility= View.GONE
            }
            loadMoreListener?.setFinished(true)
            loadMoreListener?.setLoaded()
            return
        }else{
            txtInfo.visibility= View.GONE
        }

        val isFirstPage= gameViewModel.page== 0
        if(slots.size< LoadMoreListener.SIZE_PER_PAGE)
            loadMoreListener?.setFinished(true)
        val lastPosition= slotListAdapter.slots.size
        if(isFirstPage)
            slotListAdapter.slots.clear()

        slotListAdapter.slots.addAll(slots)

        if(isFirstPage){
            slotListAdapter.notifyDataSetChanged()
        } else{
            slotListAdapter.notifyItemRangeInserted(lastPosition, slots.size)
        }
        loadMoreListener?.setLoaded()

    }

    private fun setLoadMoreListener() {
        loadMoreListener?.let { rclSlot.removeOnScrollListener(loadMoreListener!!) }
        loadMoreListener=  LoadMoreListener(rclSlot.layoutManager as LinearLayoutManager)
        loadMoreListener?.setOnLoadMoreListener(this)
        rclSlot.addOnScrollListener(loadMoreListener!!)
    }

    override fun onLoadMore() {
        if(user== null)
            gameViewModel.loadSlots(MyApplication.getInstance().company.sequence!!.id!!, false,
                getDateString(report!!.reportDate, DateTimeFormat.forPattern("MM-dd-yyyy"),
                    DateTimeZone.forID("America/New_York"))!!, gameSession!!, type, edxCode.text.toString())
        else
            gameViewModel.loadIndSlots(MyApplication.getInstance().company.sequence!!.id!!, false,
                getDateString(report!!.reportDate, DateTimeFormat.forPattern("MM-dd-yyyy"),
                    DateTimeZone.forID("America/New_York"))!!,
                user!!.sequence!!.id!!, gameSession!!, type, edxCode.text.toString())
        progressBar.progressiveStart()
    }

    override fun onUpdate(slot: Slot) {

        showDialog(this@SlotListActivity,
            getString(R.string.attention),
            if (slot.status== SlotStatus.CANCELLED) getString(R.string.sure_about_cancel_slot) else getString(R.string.reinstaure_game)
            ,
            getString(R.string.yes),
            getString(R.string.no),
            object : ClickListener {
                override fun onClick(): Boolean {
                    dialog.show()
                    gameViewModel.play(slot)
                    return false } },
            object : ClickListener {
                override fun onClick(): Boolean {
                    return false } },
            false, DialogType.WARNING)
    }
}