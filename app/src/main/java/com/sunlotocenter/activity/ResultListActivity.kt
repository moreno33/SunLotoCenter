package com.sunlotocenter.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sunlotocenter.MyApplication
import com.sunlotocenter.R
import com.sunlotocenter.activity.admin.ResultActivity
import com.sunlotocenter.adapter.ResultListAdapter
import com.sunlotocenter.dao.Admin
import com.sunlotocenter.dao.Game
import com.sunlotocenter.dao.GameType
import com.sunlotocenter.dto.Result
import com.sunlotocenter.extensions.enableHome
import com.sunlotocenter.extensions.gameTypes
import com.sunlotocenter.listener.LoadMoreListener
import com.sunlotocenter.model.GameViewModel
import com.sunlotocenter.utils.REFRESH_REQUEST_CODE
import com.sunlotocenter.validator.DateValidator
import com.sunlotocenter.validator.Form
import kotlinx.android.synthetic.main.activity_admin_report.*
import kotlinx.android.synthetic.main.activity_result_list.*
import kotlinx.android.synthetic.main.activity_result_list.edxFrom
import kotlinx.android.synthetic.main.activity_result_list.edxTo
import kotlinx.android.synthetic.main.activity_result_list.progressBar
import kotlinx.android.synthetic.main.activity_result_list.spnType
import kotlinx.android.synthetic.main.activity_result_list.toolbar
import kotlinx.android.synthetic.main.activity_result_list.txtInfo
import java.util.*
import kotlin.collections.ArrayList

class ResultListActivity : ProtectedActivity(),
    LoadMoreListener.OnLoadMoreListener{

    private var loadMoreListener: LoadMoreListener?= null
    private lateinit var resultListAdapter: ResultListAdapter
    private lateinit var gameViewModel: GameViewModel
    private var isSaveState= false
    private var gameType: GameType?= null
    private var form= Form()

    override fun getLayoutId(): Int {
        return R.layout.activity_result_list
    }

    lateinit var activityResult:
            ActivityResultLauncher<Intent>

    override fun onStart() {
        super.onStart()
        activityResult= registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode== Activity.RESULT_OK){
                gameType?.let { gameViewModel.loadResults(MyApplication.getInstance().company.sequence!!.id!!, true, it, edxFrom.text, edxTo.text) }
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
        prepareControl()

        gameType?.let { gameViewModel.loadResults(MyApplication.getInstance().company.sequence!!.id!!, true, it, "", "") }

        swpLayout.isRefreshing= true
        swpLayout.setOnRefreshListener {
            gameType?.let { gameViewModel.loadResults(MyApplication.getInstance().company.sequence!!.id!!, true, it, edxFrom.text, edxTo.text) }
        }
    }

    private fun prepareControl() {

        edxFrom.addValidator(DateValidator(getString(R.string.date_validator_error)))
        edxTo.addValidator(DateValidator(getString(R.string.date_validator_error)))
        form.addInput(edxFrom, edxTo)

        edxFrom.addTextChangedListener(object :TextWatcher{
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
                    gameType?.let { gameViewModel.loadResults(MyApplication.getInstance().company.sequence!!.id!!, true, it, "", "") }
                }
                if(s.length== 10){
                    if(edxTo.text.length== 10 && form.isValid()){
                        gameType?.let { gameViewModel.loadResults(MyApplication.getInstance().company.sequence!!.id!!, true, it, edxFrom.text, edxTo.text) }
                    }else if(edxTo.text.length<10){
                        edxTo.focus()
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        edxTo.addTextChangedListener(object :TextWatcher{
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
                    gameType?.let { gameViewModel.loadResults(MyApplication.getInstance().company.sequence!!.id!!, true, it, "", "") }
                }
                if(s.length== 10){
                    if(edxFrom.text.length== 10 && form.isValid()){
                        gameType?.let { gameViewModel.loadResults(MyApplication.getInstance().company.sequence!!.id!!, true, it, edxFrom.text, edxTo.text) }
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
                        gameViewModel.loadResults(MyApplication.getInstance().company.sequence!!.id!!, true, gameType!!, edxFrom.text, edxTo.text)
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

        fltAdd.isVisible= MyApplication.getInstance().connectedUser is Admin
        fltAdd.setOnClickListener {
            activityResult.launch(Intent(this, ResultActivity::class.java))
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
        gameType?.let { gameViewModel.loadResults(MyApplication.getInstance().company.sequence!!.id!!, false, it, edxFrom.text, edxTo.text) }
        progressBar.progressiveStart()
    }
}