package com.sunlotocenter.activity.admin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sunlotocenter.activity.ProtectedActivity
import com.sunlotocenter.activity.R
import com.sunlotocenter.adapter.EmployeeListAdapter
import com.sunlotocenter.adapter.ReportAdapter
import com.sunlotocenter.dao.Response
import com.sunlotocenter.dao.User
import com.sunlotocenter.dao.UserStatus
import com.sunlotocenter.dto.Report
import com.sunlotocenter.extensions.enableHome
import com.sunlotocenter.listener.LoadMoreListener
import com.sunlotocenter.model.GameViewModel
import com.sunlotocenter.utils.ClickListener
import com.sunlotocenter.utils.DialogType
import com.sunlotocenter.utils.REFRESH_REQUEST_CODE
import kotlinx.android.synthetic.main.activity_admin_report.*

class AdminReportActivity : ProtectedActivity(),
    LoadMoreListener.OnLoadMoreListener{

    private var loadMoreListener: LoadMoreListener?= null
    private lateinit var reportAdapter: ReportAdapter
    private lateinit var gameViewModel: GameViewModel
    private var isSaveState= false
    private var total= 0L

    override fun getLayoutId(): Int {
        return R.layout.activity_admin_report
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

        gameViewModel.loadReports(true)
        gameViewModel.getTotalReport(null, null)
        swpLayout.isRefreshing= true
        swpLayout.setOnRefreshListener {
            gameViewModel.loadReports(true)
        }
    }

    private fun setUpAdapter() {
        rclReport.setHasFixedSize(true)
        rclReport.layoutManager= LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        reportAdapter= ReportAdapter(
            if(isSaveState) gameViewModel.reports else arrayListOf()
        )
        gameViewModel.reports.clear()

        rclReport.adapter= reportAdapter
        observe()
        setLoadMoreListener()
    }

    private fun observe() {
        gameViewModel.lastAddedReportsData.observe(this,
            { reports ->
                if(reports.isEmpty() && gameViewModel.page== 0){
                    txtInfo.visibility= View.VISIBLE
                }else{
                    txtInfo.visibility= View.GONE
                }
                addReports(reports)
                progressBar.progressiveStop()
                swpLayout.isRefreshing= false
            })

    }

    private fun addReports(reports: ArrayList<Report>) {
        loadMoreListener?.setFinished(false)
        if(reports.isEmpty()){
            loadMoreListener?.setFinished(true)
            loadMoreListener?.setLoaded()
            return
        }
        val isFirstPage= gameViewModel.page== 0
        if(reports.size< LoadMoreListener.SIZE_PER_PAGE)
            loadMoreListener?.setFinished(true)
        val lastPosition= reportAdapter.reports.size
        if(isFirstPage)
            reportAdapter.reports.clear()

        reportAdapter.reports.addAll(reports)

        if(isFirstPage){
            reportAdapter.notifyDataSetChanged()
        } else{
            reportAdapter.notifyItemRangeInserted(lastPosition, reports.size)
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
        gameViewModel.loadReports(false)
        progressBar.progressiveStart()
    }
}