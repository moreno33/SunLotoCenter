package com.sunlotocenter.activity.admin

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sunlotocenter.MyApplication
import com.sunlotocenter.R
import com.sunlotocenter.activity.*
import com.sunlotocenter.adapter.AlertedGameAdapter
import com.sunlotocenter.adapter.BlockedGameAdapter
import com.sunlotocenter.adapter.GameScheduleSessionAdapter
import com.sunlotocenter.dao.*
import com.sunlotocenter.dto.GametDto
import com.sunlotocenter.listener.LoadMoreListener
import com.sunlotocenter.model.GameViewModel
import com.sunlotocenter.utils.DividerItemDecorator
import com.sunlotocenter.utils.glide
import kotlinx.android.synthetic.main.activity_admin_dashboard.*
import kotlinx.android.synthetic.main.activity_admin_dashboard.toolbar
import kotlinx.android.synthetic.main.bottom_game_schedule_layout.view.*
import org.michaelbel.bottomsheet.BottomSheet

class AdminDashboardActivity : ProtectedActivity(),
    GameScheduleSessionAdapter.GameScheduleSessionListener,
    LoadMoreListener.OnLoadMoreListener{

    private lateinit var gameViewModel: GameViewModel
    private var selectedGameScheduleSession: GameScheduleSessionAdapter.GameScheduleSession?= null
    private var loadMoreListener: LoadMoreListener?= null
    private var isSaveState= false
    private lateinit var alertedGameAdapter: AlertedGameAdapter
    private var gameSessionHolder: GameScheduleSessionAdapter.GameScheduleSession?= null

    override fun getLayoutId(): Int {
        return R.layout.activity_admin_dashboard
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)
        gameViewModel= ViewModelProvider(this, SavedStateViewModelFactory(application, this))
            .get(GameViewModel::class.java)

        manageClick(mapOf(Pair(clEmployees, EmployeeListActivity::class.java),
            Pair(clReports, AdminReportActivity::class.java),
            Pair(clResults, ResultListActivity::class.java),
            Pair(clBanks, BankListActivity::class.java)
            ))

        btnPlay.setOnClickListener {
            startActivity(Intent(this, GameActivity::class.java))
        }
        btnGame.setOnClickListener {
            dialog.show()
            gameViewModel.getAllGameSchedules(MyApplication.getInstance().company.sequence!!.id!!)
        }
        observe()

        setUpAdapter()
    }

    override fun onResume() {
        super.onResume()
        loadScreen()
        loadAlert()
    }

    private fun setUpAdapter() {
        alertedGameAdapter= AlertedGameAdapter(if(isSaveState) gameViewModel.alertedGames else arrayListOf())

        rclAlert.apply {
            setHasFixedSize(true)
            layoutManager= LinearLayoutManager(this@AdminDashboardActivity, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(DividerItemDecorator(ContextCompat.getDrawable(this@AdminDashboardActivity, R.drawable.game_row_divider)))
            adapter= alertedGameAdapter
        }
        gameViewModel.blockedGames.clear()

        setLoadMoreListener()
    }

    private fun loadAlert() {
        gameViewModel.getGamesUnderAlert(MyApplication.getInstance().connectedUser.company!!.sequence!!.id!!, true)
    }

    private fun setLoadMoreListener() {
        loadMoreListener?.let { rclAlert.removeOnScrollListener(loadMoreListener!!) }
        loadMoreListener=  LoadMoreListener(rclAlert.layoutManager as LinearLayoutManager)
        loadMoreListener?.setOnLoadMoreListener(this)
        rclAlert.addOnScrollListener(loadMoreListener!!)
    }

    private fun observe() {
        gameViewModel.lastAddedSchedulesData.observe(this, { schedules ->
            dialog.dismiss()
            runOnUiThread {
                showMenu(schedules)
            }
        })

        gameViewModel.lastAddedGameUnderAlertData.observe(this, { games ->
            addAlertedGames(games)
        })

        gameViewModel.gameResultData.observe(this, {resultData ->
            dialog.dismiss()
            runOnUiThread {
                if(resultData== null)loadLot(null)
                else loadLot(resultData.data)
            }
        })
    }

    private fun addAlertedGames(alertedGames: List<GametDto>) {
        loadMoreListener?.setFinished(false)
        pgbAlert.visibility= View.GONE
        if(alertedGames.isEmpty()){
            loadMoreListener?.setFinished(true)
            loadMoreListener?.setLoaded()
            if(gameViewModel.page== 0) {
                txtInfo.visibility= View.VISIBLE
                alertedGameAdapter.alertedGames.clear()
                alertedGameAdapter.notifyDataSetChanged()
            }else{
                txtInfo.visibility= View.GONE
            }
            return
        }else{
            txtInfo.visibility= View.GONE
        }
        val isFirstPage= gameViewModel.page== 0
        if(alertedGames.size< LoadMoreListener.SIZE_PER_PAGE)
            loadMoreListener?.setFinished(true)
        val lastPosition= alertedGameAdapter.alertedGames.size
        if(isFirstPage)
            alertedGameAdapter.alertedGames.clear()

        alertedGameAdapter.alertedGames.addAll(alertedGames)

        if(isFirstPage){
            alertedGameAdapter.notifyDataSetChanged()
        } else{
            alertedGameAdapter.notifyItemRangeInserted(lastPosition, alertedGames.size)
        }
        loadMoreListener?.setLoaded()
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
            layoutManager= LinearLayoutManager(this@AdminDashboardActivity, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(DividerItemDecorator(ContextCompat.getDrawable(context, R.drawable.game_row_divider)))
            adapter= GameScheduleSessionAdapter(gameScheduleSessions, this@AdminDashboardActivity, false)
        }

        BottomSheet.Builder(this)
            .setView(bottomLayout)
            .setBackgroundColor(ContextCompat.getColor(applicationContext, android.R.color.white))
            .show()

    }

    private fun loadScreen() {
        glide(this, MyApplication.getInstance().connectedUser.profilePath, imgAdmin, R.drawable.background_gray, getProfileImage(MyApplication.getInstance().connectedUser))

        txtName.text= "${MyApplication.getInstance().connectedUser.firstName} ${MyApplication.getInstance().connectedUser.lastName}"
        txtPosition.text= UserType.ADMIN.id

        val gameResult= MyApplication.getInstance().gameResult
        loadLot(gameResult)
    }

    private fun loadLot(gameResult: GameResult?) {
        if(gameResult!= null){

            txtFirst.text= gameResult.lo1
            txtSecond.text= gameResult.lo2
            txtThird.text= gameResult.lo3
            displayGameSessionImage(gameResult.session!!)
            txtGame.text= gameResult.type!!.id
        }else{
            txtFirst.text= "-"
            txtSecond.text= "-"
            txtThird.text= "-"
            displayGameSessionImage(gameSessionHolder?.gameSession)
            txtGame.text= gameSessionHolder?.gameSchedule?.type?.id?:"-"
        }
    }

    private fun getProfileImage(user: User?): Int {
        if(user== null){
            return R.drawable.admin_male_icon
        }else{
            if (user.sex== Sex.MALE){
                return R.drawable.admin_male_icon
            }else{
                return R.drawable.woman_icon
            }
        }

    }

    private fun manageClick(actions: Map<ConstraintLayout, Class<out AppCompatActivity>>) {
        actions.forEach { action->
            action.key.setOnClickListener {
                startActivity(Intent(this, action.value))
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        val menuInflater= menuInflater
        menuInflater.inflate(R.menu.mn_admin_dashboard, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        when(item.itemId){
            R.id.mnSettings-> {
                val intent= Intent(this, SettingActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.mnNotification->{
                startActivity(Intent(this, NotificationActivity::class.java))
                return  true
            }
            R.id.mnBroadcast->{
                startActivity(Intent(this, AdminBroadcastActivity::class.java))
                return  true
            }
        }
        return false
    }

    override fun onClick(gameScheduleSession: GameScheduleSessionAdapter.GameScheduleSession) {
        dialog.show()
        gameSessionHolder= gameScheduleSession
        this.selectedGameScheduleSession= gameScheduleSession
        txtGame.text= gameScheduleSession.gameSchedule.type?.id
        displayGameSessionImage(gameScheduleSession.gameSession)
        gameViewModel.getResultFor(MyApplication.getInstance().company.sequence!!.id!!, this.selectedGameScheduleSession!!)
    }

    private fun displayGameSessionImage(gameSession: GameSession?) {
        if(gameSession== GameSession.MORNING || gameSession== null){
            imgTime.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.white_balance_sunny))
        }else{
            imgTime.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.moon_waning_crescent))
        }
    }

    override fun onLoadMore() {
        gameViewModel.getGamesUnderAlert(MyApplication.getInstance().company.sequence!!.id!!,false)
    }

}