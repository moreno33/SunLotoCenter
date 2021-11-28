package com.sunlotocenter.activity.admin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.BlockedNumberContract.unblock
import androidx.core.content.ContextCompat
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sunlotocenter.MyApplication
import com.sunlotocenter.activity.ProtectedActivity
import com.sunlotocenter.activity.R
import com.sunlotocenter.adapter.BlockedGameAdapter
import com.sunlotocenter.adapter.GameScheduleAdapter
import com.sunlotocenter.dao.BlockedGame
import com.sunlotocenter.dao.GameAlertAndBlock
import com.sunlotocenter.dao.GamePrice
import com.sunlotocenter.dao.GameSchedule
import com.sunlotocenter.extensions.enableHome
import com.sunlotocenter.listener.LoadMoreListener
import com.sunlotocenter.model.GameViewModel
import com.sunlotocenter.utils.*
import com.sunlotocenter.validator.Form
import com.sunlotocenter.validator.GreaterThanZeroValidator
import kotlinx.android.synthetic.main.activity_prevent_trouble.*
import kotlinx.android.synthetic.main.activity_prevent_trouble.toolbar
import org.modelmapper.ModelMapper

class PreventTroubleActivity : ProtectedActivity(),
    LoadMoreListener.OnLoadMoreListener,
    BlockedGameAdapter.OnChangeBlockedGameListener{
    var gameArletAndBlockFromServer: GameAlertAndBlock?= null
    var gameArletAndBlock: GameAlertAndBlock?= null
    private var ACTION= "GET"

    var form= Form()
    private lateinit var gameViewModel: GameViewModel

    private var isSaveState= false
    private var loadMoreListener: LoadMoreListener?= null
    private lateinit var blockedGameAdapter: BlockedGameAdapter

    override fun getLayoutId(): Int {
        return R.layout.activity_prevent_trouble
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

        btnCreateArletBlock.setOnClickListener {
            ACTION= "SAVE"
            addAlertAndBlock()
        }

        setUpAdapter()

        btnAddBlock.setOnClickListener {
            startActivityForResult(Intent(this, BlockedGameActivity::class.java), REFRESH_REQUEST_CODE)
        }

        dialog.show()
        gameViewModel.getGameAlertAndBlock()

        addValidator()
        observe()

        gameViewModel.loadBlockedGame(true)
    }

    private fun prefill(gameAlertAndBlock: GameAlertAndBlock?) {
        if(gameAlertAndBlock!= null){
            edxBorletAlert.text= gameAlertAndBlock.borletAlertPrice.toString()
            edxBorletAlert.setSelection(edxBorletAlert.text.length)
            edxBorletBLock.text= gameAlertAndBlock.borletBlockPrice.toString()
            edxMarriageAlert.text= gameAlertAndBlock.marriageAlertPrice.toString()
            edxMarriageBLock.text= gameAlertAndBlock.marriageBlockPrice.toString()
            edxLoto3Alert.text= gameAlertAndBlock.loto3AlertPrice.toString()
            edxLoto3BLock.text= gameAlertAndBlock.loto3BlockPrice.toString()
            edxLoto4Alert.text= gameAlertAndBlock.loto4AlertPrice.toString()
            edxLoto4BLock.text= gameAlertAndBlock.loto4BlockPrice.toString()
            edxLoto5Alert.text= gameAlertAndBlock.loto5AlertPrice.toString()
            edxLoto5BLock.text= gameAlertAndBlock.loto5BlockPrice.toString()
        }
    }

    private fun addAlertAndBlock() {
        if(form.isValid()){
            gameArletAndBlock= GameAlertAndBlock(sequence = com.sunlotocenter.dao.Sequence(), author = MyApplication.getInstance().connectedUser)
            if(gameArletAndBlockFromServer!= null){
                ModelMapper().map(gameArletAndBlockFromServer, gameArletAndBlock)
            }
            gameArletAndBlock?.apply {
                borletAlertPrice = edxBorletAlert.text.toInt()
                borletBlockPrice = edxBorletBLock.text.toInt()
                marriageAlertPrice = edxMarriageAlert.text.toInt()
                marriageBlockPrice = edxMarriageBLock.text.toInt()
                loto3AlertPrice= edxLoto3Alert.text.toInt()
                loto3BlockPrice= edxLoto3BLock.text.toInt()
                loto4AlertPrice= edxLoto4Alert.text.toInt()
                loto4BlockPrice= edxLoto4BLock.text.toInt()
                loto5AlertPrice= edxLoto5Alert.text.toInt()
                loto5BlockPrice= edxLoto5BLock.text.toInt()
                author = MyApplication.getInstance().connectedUser
            }

            dialog.show()
            gameViewModel.saveGameAlertAndBlock(gameArletAndBlock!!)
        }
    }

    private fun addValidator() {
        edxBorletAlert.addValidator(GreaterThanZeroValidator(getString(R.string.greater_than_zero_validator_error)))
        edxBorletBLock.addValidator(GreaterThanZeroValidator(getString(R.string.greater_than_zero_validator_error)))
        edxMarriageAlert.addValidator(GreaterThanZeroValidator(getString(R.string.greater_than_zero_validator_error)))
        edxMarriageBLock.addValidator(GreaterThanZeroValidator(getString(R.string.greater_than_zero_validator_error)))
        edxLoto3Alert.addValidator(GreaterThanZeroValidator(getString(R.string.greater_than_zero_validator_error)))
        edxLoto3BLock.addValidator(GreaterThanZeroValidator(getString(R.string.greater_than_zero_validator_error)))
        edxLoto4Alert.addValidator(GreaterThanZeroValidator(getString(R.string.greater_than_zero_validator_error)))
        edxLoto4BLock.addValidator(GreaterThanZeroValidator(getString(R.string.greater_than_zero_validator_error)))
        edxLoto5Alert.addValidator(GreaterThanZeroValidator(getString(R.string.greater_than_zero_validator_error)))
        edxLoto5BLock.addValidator(GreaterThanZeroValidator(getString(R.string.greater_than_zero_validator_error)))
        form.addInput(edxBorletAlert,
            edxBorletBLock,
            edxMarriageAlert,
            edxMarriageBLock,
            edxLoto3Alert,
            edxLoto3BLock,
            edxLoto4Alert,
            edxLoto4BLock,
            edxLoto5Alert,
            edxLoto5BLock)
    }

    private fun observe() {
        gameViewModel.gameAlertAndBlockData.observe(this,
            {
                dialog.dismiss()
                if(it== null){
                    if(ACTION== "SAVE")
                        com.sunlotocenter.utils.showDialog(this@PreventTroubleActivity,
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
                    if(ACTION== "SAVE")
                        com.sunlotocenter.utils.showDialog(this@PreventTroubleActivity,
                            getString(R.string.success_title),
                            getString(
                                if (gameArletAndBlockFromServer == null) R.string.create_game_alert_and_block_success_message
                                else R.string.update_game_alert_and_block_success_message

                            ),
                            getString(R.string.ok),
                            object : ClickListener {
                                override fun onClick(): Boolean {
                                    finish()
                                    return false
                                }

                            }, false, DialogType.SUCCESS)
                    else {
                        gameArletAndBlockFromServer= it.data
                        prefill(gameArletAndBlockFromServer)
                    }
                }
            })

        gameViewModel.lastAddedBlockedGamesData.observe(this, { blockedGames ->
                dialog.dismiss()
                addBlockedGames(blockedGames)
        })
        gameViewModel.blockedGameData.observe(this, {
            gameViewModel.loadBlockedGame(true)
        })
    }

    //---------------Block Game-------------------------------------

    private fun setUpAdapter() {
        blockedGameAdapter= BlockedGameAdapter(if(isSaveState) gameViewModel.blockedGames else arrayListOf(), this)

        rclblock.apply {
            setHasFixedSize(true)
            layoutManager= LinearLayoutManager(this@PreventTroubleActivity, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(DividerItemDecorator(ContextCompat.getDrawable(this@PreventTroubleActivity, R.drawable.item_divider)))
            adapter= blockedGameAdapter
        }
        gameViewModel.blockedGames.clear()

        setLoadMoreListener()
    }

    private fun addBlockedGames(blockedBames: ArrayList<BlockedGame>) {
        loadMoreListener?.setFinished(false)
        if(blockedBames.isEmpty()){
            loadMoreListener?.setFinished(true)
            loadMoreListener?.setLoaded()
            if(gameViewModel.page== 0) {
                blockedGameAdapter.blockedGames.clear()
                blockedGameAdapter.notifyDataSetChanged()
            }
            return
        }
        val isFirstPage= gameViewModel.page== 0
        if(blockedBames.size< LoadMoreListener.SIZE_PER_PAGE)
            loadMoreListener?.setFinished(true)
        val lastPosition= blockedGameAdapter.blockedGames.size
        if(isFirstPage)
            blockedGameAdapter.blockedGames.clear()

        blockedGameAdapter.blockedGames.addAll(blockedBames)

        if(isFirstPage){
            blockedGameAdapter.notifyDataSetChanged()
        } else{
            blockedGameAdapter.notifyItemRangeInserted(lastPosition, blockedBames.size)
        }
        loadMoreListener?.setLoaded()

    }

    private fun setLoadMoreListener() {
        loadMoreListener?.let { rclblock.removeOnScrollListener(loadMoreListener!!) }
        loadMoreListener=  LoadMoreListener(rclblock.layoutManager as LinearLayoutManager)
        loadMoreListener?.setOnLoadMoreListener(this)
        rclblock.addOnScrollListener(loadMoreListener!!)
    }

    override fun onLoadMore() {
        gameViewModel.loadBlockedGame(false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode== REFRESH_REQUEST_CODE && resultCode== Activity.RESULT_OK){
            dialog.show()
            gameViewModel.loadBlockedGame(true)
        }
    }

    override fun onChange(blockedGame: BlockedGame) {
        showDialog(this, getString(R.string.unblock_game),
            getString(R.string.want_to_unblock_game, blockedGame.number),
            getString(R.string.yes), getString(R.string.no), object :ClickListener{
                override fun onClick(): Boolean {
                    dialog.show()
                    blockedGame.current= false
                    gameViewModel.saveBlockedGame(blockedGame)
                    return false
                }

            }, object :ClickListener{
                override fun onClick(): Boolean {
                    return false
                }

            },
            false, DialogType.NEUTRAL)
    }
}