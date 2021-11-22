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
import com.sunlotocenter.dao.Response
import com.sunlotocenter.dao.User
import com.sunlotocenter.dao.UserStatus
import com.sunlotocenter.extensions.enableHome
import com.sunlotocenter.listener.LoadMoreListener
import com.sunlotocenter.listener.SaveUserListener
import com.sunlotocenter.model.UserViewModel
import com.sunlotocenter.utils.ClickListener
import com.sunlotocenter.utils.REFRESH_REQUEST_CODE
import kotlinx.android.synthetic.main.activity_employee_list.*

class EmployeeListActivity : ProtectedActivity(),
    LoadMoreListener.OnLoadMoreListener,
SaveUserListener{

    private var loadMoreListener: LoadMoreListener?= null
    private lateinit var employeeListAdapter: EmployeeListAdapter
    private lateinit var userViewModel: UserViewModel
    private var isSaveState= false
    private var total= 0L

    override fun getLayoutId(): Int {
        return R.layout.activity_employee_list
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableHome(toolbar)
        userViewModel= ViewModelProvider(this, SavedStateViewModelFactory(application, this))
            .get(UserViewModel::class.java)

        if(savedInstanceState!=  null){
            isSaveState= true
            userViewModel.loadSaveState()
        }

        setUpAdapter()

        userViewModel.loadEmployees(true)
        swpLayout.isRefreshing= true
        swpLayout.setOnRefreshListener {
            userViewModel.loadEmployees(true)
        }

        //preview total employee
        txtTotal.text= getString(R.string.loading)

        imgFilter.setOnClickListener {
//            showFilter()
        }
    }

    private fun setUpAdapter() {
        rclEmployees.setHasFixedSize(true)
        rclEmployees.layoutManager= LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        employeeListAdapter= EmployeeListAdapter(
            if(isSaveState) userViewModel.employees else arrayListOf(),
            this
        )
        userViewModel.employees.clear()

        rclEmployees.adapter= employeeListAdapter

        registerForContextMenu(rclEmployees)


        observe()
        setLoadMoreListener()

        fltAdd.setOnClickListener {
            startActivityForResult(Intent(this, AdminPersonalInfoActivity::class.java), REFRESH_REQUEST_CODE)
        }
    }

    private fun observe() {
        userViewModel.lastAddedEmployeesData.observe(this,
             { employees ->
                 if(employees.isEmpty() && userViewModel.page== 0){
                     txtInfo.visibility= View.VISIBLE
                 }else{
                     txtInfo.visibility= View.GONE
                 }
                 addEmployess(employees)
                 progressBar.progressiveStop()
                 swpLayout.isRefreshing= false
             })
        userViewModel.employeeTotalData.observe(this, {
            t->
            if(total!= t || t.toInt()== 0){
                total= t
                txtTotal.text= getString(R.string.employee_total, total)
            }
        })

        userViewModel.userResponseData.observe(this,
            {
                saveListener(it)
            })
    }

    private fun saveListener(it: Response<User>?) {
        dialog.dismiss()
        if(it== null){
            com.sunlotocenter.utils.showDialog(this,
                getString(R.string.internet_error_title),
                getString(
                    R.string.internet_error_message
                ),
                getString(R.string.ok),
                object : ClickListener {
                    override fun onClick(): Boolean {
                        return false
                    }
                }, true
            )
        }else{
            if(it.success){
                com.sunlotocenter.utils.showDialog(this,
                    getString(R.string.success_title),
                    getString(
                        if(it.data!!.status== UserStatus.BLOCKED)
                            R.string.account_block_success_message
                        else
                            R.string.account_unblocked_sucess_message
                    ),
                    getString(R.string.ok),
                    object : ClickListener {
                        override fun onClick(): Boolean {
                            return false
                        }
                    }, false
                )
                userViewModel.loadEmployees(true)
            }else{
                com.sunlotocenter.utils.showDialog(this,
                    getString(R.string.internet_error_title),
                    it.message!!,
                    getString(R.string.ok),
                    object : ClickListener {
                        override fun onClick(): Boolean {
                            return false
                        }
                    }, false
                )
            }

        }
    }

    private fun addEmployess(employees: ArrayList<out User>) {
        loadMoreListener?.setFinished(false)
        if(employees.isEmpty()){
            loadMoreListener?.setFinished(true)
            loadMoreListener?.setLoaded()
            return
        }
        val isFirstPage= userViewModel.page== 0
        if(employees.size< 10)
            loadMoreListener?.setFinished(true)
        val lastPosition= employeeListAdapter.employees.size
        if(isFirstPage)
            employeeListAdapter.employees.clear()

        employeeListAdapter.employees.addAll(employees)

        if(isFirstPage){
            employeeListAdapter.notifyDataSetChanged()
        } else{
            employeeListAdapter.notifyItemRangeInserted(lastPosition, employees.size)
        }
        loadMoreListener?.setLoaded()

    }

    private fun setLoadMoreListener() {
        loadMoreListener?.let { rclEmployees.removeOnScrollListener(loadMoreListener!!) }
        loadMoreListener=  LoadMoreListener(rclEmployees.layoutManager as LinearLayoutManager)
        loadMoreListener?.setOnLoadMoreListener(this)
        rclEmployees.addOnScrollListener(loadMoreListener!!)
    }

    override fun onLoadMore() {
        userViewModel.loadEmployees(false)
        progressBar.progressiveStart()
    }

    override fun save(user: User) {
        dialog.show()
        userViewModel.save(user)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode== REFRESH_REQUEST_CODE && resultCode== Activity.RESULT_OK){
            userViewModel.loadEmployees(true)
        }
    }
}