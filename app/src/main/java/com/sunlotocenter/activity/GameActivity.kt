package com.sunlotocenter.activity


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sunlotocenter.adapter.GameAdapter
import com.sunlotocenter.adapter.TabsPagerAdapter
import com.sunlotocenter.listener.AddGameListener
import kotlinx.android.synthetic.main.activity_game.*
import java.util.*
import androidx.recyclerview.widget.DividerItemDecoration
import com.github.zawadz88.materialpopupmenu.popupMenu
import com.sunlotocenter.MyApplication
import com.sunlotocenter.activity.admin.AdminPersonalInfoActivity
import com.sunlotocenter.adapter.GameScheduleSessionAdapter
import com.sunlotocenter.dao.*
import com.sunlotocenter.extensions.enableHome
import com.sunlotocenter.extensions.gameTypes
import com.sunlotocenter.model.GameViewModel
import com.sunlotocenter.model.UserViewModel
import com.sunlotocenter.utils.ClickListener
import com.sunlotocenter.utils.DialogType
import com.sunlotocenter.utils.DividerItemDecorator
import com.sunlotocenter.utils.randomWithNDigits
import kotlinx.android.synthetic.main.bottom_game_schedule_layout.view.*
import org.michaelbel.bottomsheet.BottomSheet
import kotlin.collections.ArrayList
import kotlin.math.ceil


class GameActivity : ProtectedActivity(),
    AddGameListener,
    GameScheduleSessionAdapter.GameScheduleSessionListener,
    GameAdapter.OnGameRemoveListener{

    private lateinit var tabsAdapter:TabsPagerAdapter
    private var gameSet= TreeSet<Game>()
    private lateinit var gameAdapter:GameAdapter
    private var marriedSet:TreeSet<Game>?= null
    private var lot4Set:TreeSet<Game>?= null
    private lateinit var gameViewModel: GameViewModel
    private var selectedGameScheduleSession: GameScheduleSessionAdapter.GameScheduleSession?= null

    override fun getLayoutId(): Int {
        return R.layout.activity_game
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableHome(toolbar)
        manageTabs()

        gameViewModel= ViewModelProvider(this, SavedStateViewModelFactory(application, this))
            .get(GameViewModel::class.java)

        tabsAdapter = TabsPagerAdapter(this, 5, this)
        tabsViewPager.adapter= tabsAdapter

        //Game list adapter
        gameAdapter= GameAdapter(gameSet, this)

        rclGame.apply {
            setHasFixedSize(true)
            adapter= gameAdapter
            layoutManager= LinearLayoutManager(this.context)

            addItemDecoration(DividerItemDecorator(ContextCompat.getDrawable(context, R.drawable.game_row_divider)))
        }

        //Initialize total preview text
        btnTotalPreview.text= getString(R.string.total_preview, 0, 0f)

        //Trigger auto Marriage
        btnAutoMarriage.setOnClickListener { autoMarry(gameSet) }
        //Trigger auto Loto 4
        btnAutoLoto4.setOnClickListener {
            autLoto4(gameSet)
        }

        //Submit slot
        imgSend.setOnClickListener {
            submitSlot(gameSet)
        }

        observe()
        btnTotalPreview.setOnClickListener {
            dialog.show()
            gameViewModel.getAllGameSchedules()
        }
    }
    private fun showMenu(schedules: List<GameSchedule>) {
        val bottomLayout= LayoutInflater.from(this).inflate(R.layout.bottom_game_schedule_layout, null)

        val gameScheduleSessions= ArrayList<GameScheduleSessionAdapter.GameScheduleSession>()
        schedules.forEach {
            gameScheduleSessions.add(GameScheduleSessionAdapter.GameScheduleSession(it, GameSession.MORNING))
            gameScheduleSessions.add(GameScheduleSessionAdapter.GameScheduleSession(it, GameSession.NIGHT))
        }

        bottomLayout.rclGameScheduleSession.apply {
            setHasFixedSize(true)
            layoutManager= LinearLayoutManager(this@GameActivity, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(DividerItemDecorator(ContextCompat.getDrawable(context, R.drawable.game_row_divider)))
            adapter= GameScheduleSessionAdapter(gameScheduleSessions, this@GameActivity)
        }

        BottomSheet.Builder(this)
            .setView(bottomLayout)
            .setBackgroundColor(ContextCompat.getColor(applicationContext, android.R.color.white))
            .show()

    }

    private fun submitSlot(gameSet: TreeSet<Game>) {
        if (gameSet.isEmpty() || selectedGameScheduleSession== null)return
        dialog.show()
        val slot= Slot(filteredGame(gameSet), Sequence(), MyApplication.getInstance().connectedUser, selectedGameScheduleSession!!.gameSchedule.type!!, selectedGameScheduleSession!!.gameSession, randomWithNDigits(10))
        gameViewModel.play(slot)
    }

    private fun observe() {
        gameViewModel.slotResponseData.observe(this,
            {
                dialog.dismiss()
                if(it== null){
                    com.sunlotocenter.utils.showDialog(this@GameActivity,
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
                    com.sunlotocenter.utils.showDialog(this@GameActivity,
                        getString(
                            if(it.success)R.string.success_title else R.string.internet_error_title
                        ),
                        if (it.success) getString(R.string.game_success_message) else it.message!!
                        ,
                        getString(
                            if(it.success)R.string.print_receipt else R.string.ok
                        ),
                        object : ClickListener {
                            override fun onClick(): Boolean {
                                return false
                            }

                        }, false, if(it.success) DialogType.SUCCESS else DialogType.ERROR)
                }
            })

        gameViewModel.lastAddedSchedulesData.observe(this, { schedules ->
            dialog.dismiss()
            runOnUiThread {
                showMenu(schedules)
            }
        })
    }

    private fun filteredGame(gameSet: TreeSet<Game>): List<Game> {
        val filteredGame= ArrayList<Game>()
        gameSet.forEach {
            if(it.type==1 && it.amount > 0){
                filteredGame.add(it)
            }
        }
        return filteredGame
    }

    private fun autLoto4(gameSet: TreeSet<Game>) {
        lot4Set= TreeSet<Game>()
        lot4Set?.clear()
        var i= 0
        var j= 1

        //Normal order
        for (outer in i until gameSet.size){
            var outerGame= gameSet.elementAt(outer)
            for (inner in j until gameSet.size ){
                var innerGame= gameSet.elementAt(inner)
                if(outerGame is Borlet && outerGame.type== 1 && innerGame is Borlet && innerGame.type==1){
                    for(opt in 1 until 4){
                        val loto4= Loto4("${outerGame.number}${innerGame.number}", ceil((outerGame.amount + innerGame.amount)/2), "${opt}", 1)
                        lot4Set?.add(loto4)
                    }
                }
                j++
            }
            i++
            j= i+1
        }
        //Reverse order
        i= gameSet.size-1
        j= gameSet.size-2
        for (outer in i downTo 0){
            var outerGame= gameSet.elementAt(outer)
            for (inner in j downTo 1 ){
                var innerGame= gameSet.elementAt(inner)
                if(outerGame is Borlet && outerGame.type== 1 && innerGame is Borlet && innerGame.type==1){
                    for(opt in 1 until 4){
                        val loto4= Loto4("${outerGame.number}${innerGame.number}", ceil((outerGame.amount + innerGame.amount)/2), "${opt}", 1)
                        lot4Set?.add(loto4)
                    }
                }
                j--
            }
            i--
            j= i-1
        }


        startAutoLoto4Activity(lot4Set!!)
    }

    private fun startAutoLoto4Activity(lot4Set: TreeSet<Game>) {
        var intent= Intent(this, AutoComposeActivity::class.java)
        intent.putExtra(AutoComposeActivity.GAMES_EXTRA, lot4Set)
        intent.putExtra(AutoComposeActivity.TITLE_EXTRA, getString(R.string.auto_loto_4))
        startActivityForResult(intent, AutoComposeActivity.REQUEST_CODE)
    }

    private fun autoMarry(gameSet: TreeSet<Game>) {
        marriedSet= TreeSet<Game>()
        marriedSet?.clear()
        var i= 0
        var j= 1

        for (outer in i until gameSet.size){
            var outerGame= gameSet.elementAt(outer)
            for (inner in j until gameSet.size ){
                var innerGame= gameSet.elementAt(inner)
                if(outerGame is Borlet && outerGame.type== 1 && innerGame is Borlet && innerGame.type==1){
                    val marriage= Marriage("${outerGame.number}X${innerGame.number}", ceil((outerGame.amount + innerGame.amount)/2), "", 1)
                    marriedSet?.add(marriage)
                }
                j++
            }
            i++
            j= i+1
        }
        startAutoMarriageActivity(marriedSet!!)
    }

    private fun startAutoMarriageActivity(marriedSet: TreeSet<Game>) {
        var intent= Intent(this, AutoComposeActivity::class.java)
        intent.putExtra(AutoComposeActivity.GAMES_EXTRA, marriedSet)
        intent.putExtra(AutoComposeActivity.TITLE_EXTRA, getString(R.string.marry_all))
        startActivityForResult(intent, AutoComposeActivity.REQUEST_CODE)
    }

    private fun manageTabs() {
        (tabs.getChildAt(0) as AppCompatButton).apply {
            isActivated= true
            setTextColor(resources.getColor(R.color.colorPrimary))
        }
        tabs.children.forEachIndexed { index, child->
            child.setOnClickListener { self ->
                tabs.children.forEach {
                    it.isActivated= false
                    (it as AppCompatButton).setTextColor(resources.getColor(R.color.white))
                }
                tabsViewPager.currentItem= index
                self.isActivated= true
                (self as AppCompatButton).setTextColor(resources.getColor(R.color.colorPrimary))
            }
        }

    }

    override fun addGame(game: Game) {
        txtInfo.visibility= View.GONE
        //Let check if we already have header for this specific game and add one
        if(gameSet.isEmpty()){
            addHeaderAndGame(game, gameSet)
        }else{
            if(isHeaderExistForGame(game, gameSet)){
                if(!gameSet.add(game)){
                    gameSet.remove(game)
                    gameSet.add(game)
                }
            }else{
                addHeaderAndGame(game, gameSet)
            }
        }

        gameAdapter.notifyDataSetChanged()
        updateTotalPreview(gameSet)
        updateAutoLotAndAutoMarriage(gameSet)
    }

    private fun updateAutoLotAndAutoMarriage(gameSet: TreeSet<Game>) {
        var countBorlet: Int= 0
        gameSet.forEach {
            if(it is Borlet && it.type== 1)
                countBorlet++
        }
        if (countBorlet> 1){
            btnAutoLoto4.isEnabled= true
            btnAutoMarriage.isEnabled= true
        }else{
            btnAutoLoto4.isEnabled= false
            btnAutoMarriage.isEnabled= false
        }
    }

    private fun addHeaderAndGame(game: Game, gameSet: TreeSet<Game>) {
        var gameCopy= game.clone() as Game
        gameCopy.number= ""
        //Header
        gameCopy.type= 0
        gameSet.add(gameCopy)
        if(!gameSet.add(game)){
            gameSet.remove(game)
            gameSet.add(game)
        }
    }

    private fun isHeaderExistForGame(game: Game, gameSet: TreeSet<Game>): Boolean{
        gameSet.forEach {
            if (it.position== game.position) return true
        }
        return false
    }


    private fun updateTotalPreview(gameSet: Set<Game>) {
        var total= 0.0
        var size= 0
        gameSet.forEach {
            if(it.type!= 0){
                total+= it.amount
                size++
            }
        }

        if(selectedGameScheduleSession== null){
            btnTotalPreview.text= getString(R.string.total_preview, size, total)
        }else{
            btnTotalPreview.text= "${getString(R.string.total_preview, size, total)} (${selectedGameScheduleSession!!.gameSchedule.type!!.id}-${selectedGameScheduleSession!!.gameSession.id})"
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AutoComposeActivity.REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK){
                (data?.getSerializableExtra(AutoComposeActivity.GAMES_EXTRA) as TreeSet<Game>).forEach {
                    if(it.amount> 0)
                        addGame(it)
                }
                gameAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onClick(gameScheduleSession: GameScheduleSessionAdapter.GameScheduleSession) {
        this.selectedGameScheduleSession= gameScheduleSession
        val text= btnTotalPreview.text.trim()
        if(text.contains("(")){
            btnTotalPreview.text= "${text.split("(")[0].trim()} (${gameScheduleSession.gameSchedule.type!!.id}-${gameScheduleSession.gameSession.id})"
        }else{
            btnTotalPreview.text= "${text} (${gameScheduleSession.gameSchedule.type!!.id}-${gameScheduleSession.gameSession.id})"
        }
    }

    override fun onRemove(games: Set<Game>, game: Game) {
        if(gameSet.isEmpty()){
            txtInfo.visibility= View.VISIBLE
            btnAutoLoto4.isEnabled= false
            btnAutoMarriage.isEnabled= false
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() === 0) {
            if(gameAdapter.gameSet.isNotEmpty()){
                com.sunlotocenter.utils.showDialog(this,
                    getString(R.string.attention),
                    getString(R.string.unsubmitted_game_warning_message),
                    getString(R.string.no),
                    getString(R.string.yes),
                    object :ClickListener{
                        override fun onClick(): Boolean {
                            return false
                        }

                    },
                    object :ClickListener{
                        override fun onClick(): Boolean {
                            finish()
                            return false
                        }

                    },
                    true
                )
                return true
            }
            return super.onKeyDown(keyCode, event)
        } else super.onKeyDown(keyCode, event)
    }
}