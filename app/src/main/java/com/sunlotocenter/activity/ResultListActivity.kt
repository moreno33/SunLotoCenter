package com.sunlotocenter.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sunlotocenter.activity.admin.ResultActivity
import com.sunlotocenter.adapter.ResultListAdapter
import com.sunlotocenter.dao.GameType
import com.sunlotocenter.dto.Result
import com.sunlotocenter.extensions.enableHome
import com.sunlotocenter.extensions.gameTypes
import com.sunlotocenter.listener.LoadMoreListener
import com.sunlotocenter.model.GameViewModel
import com.sunlotocenter.utils.REFRESH_REQUEST_CODE
import kotlinx.android.synthetic.main.activity_result_list.*
import kotlinx.android.synthetic.main.activity_result_list.toolbar

class ResultListActivity : ProtectedActivity(),
    LoadMoreListener.OnLoadMoreListener{

    private var loadMoreListener: LoadMoreListener?= null
    private lateinit var resultListAdapter: ResultListAdapter
    private lateinit var gameViewModel: GameViewModel
    private var isSaveState= false
    private var gameType: GameType?= null

    override fun getLayoutId(): Int {
        return R.layout.activity_result_list
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
        prepareControl()

        gameType?.let { gameViewModel.loadResults(true, it) }

        swpLayout.isRefreshing= true
        swpLayout.setOnRefreshListener {
            gameType?.let { gameViewModel.loadResults(true, it) }
        }
    }

    private fun prepareControl() {
        //Fill game spinner
        val dataAdapter= ArrayAdapter(this, android.R.layout.simple_spinner_item, gameTypes())
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnType.adapter = dataAdapter
        gameType= GameType.NY
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
                        gameViewModel.loadResults(true, gameType!!)
                    }
                }
            }
            override fun onNothingSelected(parentView: AdapterView<*>?) {
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
//        gameViewModel.saveState()
        super.onSaveInstanceState(outState, outPersistentState)
    }

    private fun setUpAdapter() {
        rclResult.setHasFixedSize(true)
        rclResult.layoutManager= LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        resultListAdapter= ResultListAdapter(if(isSaveState) gameViewModel.results else arrayListOf())
        gameViewModel.results.clear()

        rclResult.adapter= resultListAdapter

        observe()
        setLoadMoreListener()

        fltAdd.setOnClickListener {
            startActivityForResult(Intent(this, ResultActivity::class.java), REFRESH_REQUEST_CODE)
        }
    }

    private fun observe() {
        gameViewModel.lastAddedResultsData.observe(this,
            { results ->
                addResults(results)
                progressBar.progressiveStop()
                swpLayout.isRefreshing= false
            })
    }

    private fun addResults(results: ArrayList<Result>) {
        loadMoreListener?.setFinished(false)
        if(results.isEmpty()){
            if(gameViewModel.page== 0){
                resultListAdapter.results.clear()
                resultListAdapter.notifyDataSetChanged()
                txtInfo.visibility= View.VISIBLE
            }else{
                txtInfo.visibility= View.GONE
            }
            loadMoreListener?.setFinished(true)
            loadMoreListener?.setLoaded()
            return
        }else{
            txtInfo.visibility= View.GONE
            if(gameViewModel.page== 0){
                if(results[0].night== null){
                    results.add(0, Result(results[0].morning, null))
                }else{
                    results.add(0, Result(null, results[0].night))
                }
            }
        }
        val isFirstPage= gameViewModel.page== 0
        if(results.size< LoadMoreListener.SIZE_PER_PAGE)
            loadMoreListener?.setFinished(true)
        val lastPosition= resultListAdapter.results.size
        if(isFirstPage)
            resultListAdapter.results.clear()

        resultListAdapter.results.addAll(results)

        if(isFirstPage){
            resultListAdapter.notifyDataSetChanged()
        } else{
            resultListAdapter.notifyItemRangeInserted(lastPosition, results.size)
        }
        loadMoreListener?.setLoaded()

    }

    private fun setLoadMoreListener() {
        loadMoreListener?.let { rclResult.removeOnScrollListener(loadMoreListener!!) }
        loadMoreListener=  LoadMoreListener(rclResult.layoutManager as LinearLayoutManager)
        loadMoreListener?.setOnLoadMoreListener(this)
        rclResult.addOnScrollListener(loadMoreListener!!)
    }

    override fun onLoadMore() {
        gameType?.let { gameViewModel.loadResults(false, it) }
        progressBar.progressiveStart()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode== REFRESH_REQUEST_CODE && resultCode== Activity.RESULT_OK){
            gameType?.let { gameViewModel.loadResults(true, it) }
        }
    }
}