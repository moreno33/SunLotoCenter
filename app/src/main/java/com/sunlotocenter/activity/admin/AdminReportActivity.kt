package com.sunlotocenter.activity.admin
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sunlotocenter.MyApplication
import com.sunlotocenter.activity.ProtectedActivity
import com.sunlotocenter.R
import com.sunlotocenter.activity.SlotListActivity
import com.sunlotocenter.adapter.AdminReportAdapter
import com.sunlotocenter.dao.GameSession
import com.sunlotocenter.dao.GameType
import com.sunlotocenter.dao.Report
import com.sunlotocenter.dao.SpinnerItem
import com.sunlotocenter.extensions.enableHome
import com.sunlotocenter.extensions.gameTypes
import com.sunlotocenter.listener.LoadMoreListener
import com.sunlotocenter.model.GameViewModel
import com.sunlotocenter.utils.GAME_SESSION_EXTRA
import com.sunlotocenter.utils.GAME_TYPE_EXTRA
import com.sunlotocenter.utils.REPORT_EXTRA
import com.sunlotocenter.validator.DateValidator
import com.sunlotocenter.validator.Form
import kotlinx.android.synthetic.main.activity_admin_report.*
import kotlinx.android.synthetic.main.activity_admin_report.edxFrom
import kotlinx.android.synthetic.main.activity_admin_report.edxTo
import kotlinx.android.synthetic.main.activity_admin_report.progressBar
import kotlinx.android.synthetic.main.activity_admin_report.spnType
import kotlinx.android.synthetic.main.activity_admin_report.swpLayout
import kotlinx.android.synthetic.main.activity_admin_report.toolbar
import kotlinx.android.synthetic.main.activity_admin_report.txtInfo

class AdminReportActivity : ProtectedActivity(),
    LoadMoreListener.OnLoadMoreListener,
AdminReportAdapter.OnOpenReportListener{

    private var loadMoreListener: LoadMoreListener?= null
    private lateinit var adminReportAdapter: AdminReportAdapter
    private lateinit var gameViewModel: GameViewModel
    private var isSaveState= false
    private var gameType: GameType?= null
    private var form= Form()

    override fun getLayoutId(): Int {
        return R.layout.activity_admin_report
    }

    lateinit var activityResult:
            ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableHome(toolbar)
        activityResult= registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode== Activity.RESULT_OK){
                gameType?.let { gameViewModel.loadReports(MyApplication.getInstance().company.sequence!!.id!!, true, it, edxFrom.text, edxTo.text)  }
            }
        }
        gameViewModel= ViewModelProvider(this, SavedStateViewModelFactory(application, this))[GameViewModel::class.java]

        if(savedInstanceState!=  null){
            isSaveState= true
            gameViewModel.loadSaveState()
        }

        setUpAdapter()
        prepareControl()
        gameType?.let { gameViewModel.loadReports(MyApplication.getInstance().company.sequence!!.id!!, true, it, "", "") }
        swpLayout.isRefreshing= true
        swpLayout.setOnRefreshListener {
            gameType?.let { gameViewModel.loadReports(MyApplication.getInstance().company.sequence!!.id!!, true, it, edxFrom.text, edxTo.text)  }
        }
    }

    private fun prepareControl() {

        edxFrom.addValidator(DateValidator(getString(R.string.date_validator_error)))
        edxTo.addValidator(DateValidator(getString(R.string.date_validator_error)))
        form.addInput(edxFrom, edxTo)

        edxFrom.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s== null) return
                if(s.length>= 2 && before== 0 && !s.contains("-")){
                    edxFrom.text= "${s.substring(0,2)}-${s.substring(2)}"
                    edxFrom.setSelection(edxFrom.text.length)
                }else if(s.length>= 5 && before== 0 && !s.substring(3).contains("-")){
                    edxFrom.text= "${s.substring(0,5)}-${s.substring(5)}"
                    edxFrom.setSelection(edxFrom.text.length)
                }else if(edxFrom.text.isEmpty() && edxTo.text.isEmpty()){
                    dialog.show()
                    gameType?.let { gameViewModel.loadReports(MyApplication.getInstance().company.sequence!!.id!!, true, it, "", "") }
                }
                if(s.length== 10){
                    if(edxTo.text.length== 10 && form.isValid()){
                        dialog.show()
                        gameType?.let { gameViewModel.loadReports(MyApplication.getInstance().company.sequence!!.id!!, true, it, edxFrom.text, edxTo.text) }
                    }else if(edxTo.text.length<10){
                        edxTo.focus()
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        edxTo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s== null) return
                if(s.length>= 2 && before== 0 && !s.contains("-")){
                    edxTo.text= "${s.substring(0,2)}-${s.substring(2)}"
                    edxTo.setSelection(edxTo.text.length)
                }else if(s.length>= 5 && before== 0 && !s.substring(3).contains("-")){
                    edxTo.text= "${s.substring(0,5)}-${s.substring(5)}"
                    edxTo.setSelection(edxTo.text.length)
                }else if(edxFrom.text.isEmpty() && edxTo.text.isEmpty()){
                    dialog.show()
                    gameType?.let { gameViewModel.loadReports(MyApplication.getInstance().company.sequence!!.id!!,
                        true, it, "", "") }
                }
                if(s.length== 10){
                    if(edxFrom.text.length== 10 && form.isValid()){
                        dialog.show()
                        gameType?.let { gameViewModel.loadReports(MyApplication.getInstance().company.sequence!!.id!!,
                            true, it, edxFrom.text, edxTo.text) }
                    }else if (edxFrom.text.length<10){
                        edxFrom.focus()
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })


        //Fill game spinner
        val dataAdapter= ArrayAdapter(this, android.R.layout.simple_spinner_item, gameTypes())
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnType.adapter = dataAdapter
        spnType.setTitle(getString(R.string.game))
        spnType.setPositiveButton(getString(R.string.ok))
        gameType= GameType.ALL
        spnType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                GameType.values().forEach {
                    if(it.id == dataAdapter.getItem(position)!!.id){
                        gameType= it
                        if (form.isValid()){
                            dialog.show()
                            gameViewModel.loadReports(MyApplication.getInstance().company.sequence!!.id!!,
                                true, gameType!!, edxFrom.text, edxTo.text)
                        }
                    }
                }
            }
            override fun onNothingSelected(parentView: AdapterView<*>?) {
            }
        }
    }

    private fun setUpAdapter() {
        rclReport.setHasFixedSize(true)
        rclReport.layoutManager= LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        adminReportAdapter= AdminReportAdapter(if(isSaveState) gameViewModel.reports else arrayListOf(), this)
        gameViewModel.reports.clear()

        rclReport.adapter= adminReportAdapter
        observe()
        setLoadMoreListener()
    }

    private fun observe() {
        gameViewModel.lastAddedReportsData.observe(this,
            { reports ->
                dialog.dismiss()
                progressBar.progressiveStop()
                addReports(reports)
                swpLayout.isRefreshing= false
            })
    }

    private fun addReports(reports: ArrayList<Report>) {
        loadMoreListener?.setFinished(false)
        if(reports.isEmpty()){
            if(gameViewModel.page== 0){
                adminReportAdapter.reports.clear()
                adminReportAdapter.notifyDataSetChanged()
                txtInfo.visibility= VISIBLE
            }else{
                txtInfo.visibility= GONE
            }
            loadMoreListener?.setFinished(true)
            loadMoreListener?.setLoaded()
            return
        }else{
            txtInfo.visibility= GONE
        }
        val isFirstPage= gameViewModel.page== 0
        if(reports.size< LoadMoreListener.SIZE_PER_PAGE)
            loadMoreListener?.setFinished(true)
        val lastPosition= adminReportAdapter.reports.size
        if(isFirstPage)
            adminReportAdapter.reports.clear()

        adminReportAdapter.reports.addAll(reports)

        if(isFirstPage){
            adminReportAdapter.notifyDataSetChanged()
        } else{
            adminReportAdapter.notifyItemRangeInserted(lastPosition, reports.size)
        }
        loadMoreListener?.setLoaded()

    }

    private fun setLoadMoreListener() {
        loadMoreListener?.let { rclReport.removeOnScrollListener(loadMoreListener!!) }
        loadMoreListener=  LoadMoreListener(rclReport.layoutManager as LinearLayoutManager)
        loadMoreListener?.setOnLoadMoreListener(this)
        rclReport.addOnScrollListener(loadMoreListener!!)
    }

    override fun onLoadMore() {
        gameType?.let { gameViewModel.loadReports(MyApplication.getInstance().company.sequence!!.id!!,
            false, it, edxFrom.text, edxTo.text) }
        progressBar.progressiveStart()
    }

    override fun onOpen(gameSession: GameSession, report: Report) {
        val intent= Intent(this, SlotListActivity::class.java)
        intent.putExtra(GAME_SESSION_EXTRA, gameSession)
        intent.putExtra(REPORT_EXTRA, report)
        intent.putExtra(GAME_TYPE_EXTRA, gameType)
        activityResult.launch(intent)
    }
}