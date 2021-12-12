package com.sunlotocenter.activity

import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sunlotocenter.MyApplication
import com.sunlotocenter.R
import com.sunlotocenter.adapter.GameReceiptAdapter
import com.sunlotocenter.adapter.SlotListAdapter
import com.sunlotocenter.dao.GameSession
import com.sunlotocenter.dao.Report
import com.sunlotocenter.dao.Slot
import com.sunlotocenter.dao.SlotStatus
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

class ReceiptReviewActivity : ProtectedActivity() {
    private lateinit var gameReceiptAdapter: GameReceiptAdapter
    private lateinit var gameViewModel: GameViewModel
    private var isSaveState= false
    private lateinit var slotExtra:Slot


    override fun getLayoutId(): Int {
        return R.layout.activity_receipt_review
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

        slotExtra= intent.getSerializableExtra(SLOT_EXTRA) as Slot

        supportActionBar?.title= getString(R.string.receipt, "${slotExtra.type} (${slotExtra.session.id})")

        setUpAdapter()
    }

    private fun setUpAdapter() {
        gameReceiptAdapter= GameReceiptAdapter(slotExtra.games.toSet())
        gameViewModel.slots.clear()

        rclSlot.apply {
            setHasFixedSize(true)
            layoutManager= LinearLayoutManager(this@ReceiptReviewActivity, LinearLayoutManager.VERTICAL, false)
            adapter= gameReceiptAdapter
        }
        observe()
    }

    private fun observe() {
        gameViewModel.lastAddedSlotsData.observe(this,
            { slots ->


            })
    }

    fun submit(slot: Slot){
        dialog.show()
        gameViewModel.play(slot)
    }
}