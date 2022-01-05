package com.sunlotocenter.extensions

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.sunlotocenter.R
import com.sunlotocenter.activity.admin.AdminDashboardActivity
import com.sunlotocenter.activity.seller.SellerDashboardActivity
import com.sunlotocenter.dao.Admin
import com.sunlotocenter.dao.Seller
import com.sunlotocenter.dao.SpinnerItem
import com.sunlotocenter.dao.User
import com.sunlotocenter.utils.ClickListener
import com.sunlotocenter.utils.DialogType
import com.sunlotocenter.utils.showDialog
import com.valdesekamdem.library.mdtoast.MDToast


fun AppCompatActivity.gameTypes(): Array<SpinnerItem>{
    return arrayOf(
            SpinnerItem("ALL", getString(R.string.all_type)),
            SpinnerItem("NY", getString(R.string.new_york)),
            SpinnerItem("FL", getString(R.string.florida)),
            SpinnerItem("GA", getString(R.string.georgia))
    )
}

fun AppCompatActivity.gameSessions(): Array<SpinnerItem>{
    return arrayOf(
        SpinnerItem("AM", getString(R.string.morning_uppercase)),
        SpinnerItem("PM", getString(R.string.night_uppercase)),
    )
}




fun AppCompatActivity.gameCategories(): Array<SpinnerItem>{
    return arrayOf(
        SpinnerItem("Borlet", getString(R.string.borlet)),
        SpinnerItem("Marriage", getString(R.string.marriage)),
        SpinnerItem("Loto3", getString(R.string.loto3)),
        SpinnerItem("Loto4", getString(R.string.loto4)),
        SpinnerItem("Loto5", getString(R.string.loto5))
    )
}

/**
 * This method allows to configure a toolbar.
 */
fun AppCompatActivity.enableHome(toolBar:Toolbar){
    setSupportActionBar(toolBar)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    supportActionBar?.setDisplayShowHomeEnabled(true)
}

/**
 * This method allows us to check if we are connected to the internet.
 */
fun AppCompatActivity.isConnected(): Boolean {
    var result = false
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        result = when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    } else {
        connectivityManager.run {
            connectivityManager.activeNetworkInfo?.run {
                result = when (type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }

            }
        }
    }

    return result
}


/**
 * This method allows us to show a custom toast.
 */
fun AppCompatActivity.toast(message: String, type: Int) {
    val mdToast = MDToast.makeText(this, message, MDToast.LENGTH_SHORT, type)
    mdToast.show()
}

/**
 * this method allows us to show a dialog with only one ok button.
 */
fun AppCompatActivity.neutralDialog(title: String= getString(R.string.internet_error_title),
                  message: String= getString(R.string.internet_error_message),
                  isPositiveLabelBold:Boolean= true, positiveFunction: () -> Boolean={false},
                                    isCancelable: Boolean=true,
                                    dialogType: DialogType = DialogType.NEUTRAL) {
    showDialog(this, title, message, getString(R.string.ok),
            object: ClickListener {
                override fun onClick(): Boolean {
                    return positiveFunction()
                }

            }
            ,isCancelable, dialogType)
}

/**
 * This mehtod allows us to show a more customizable button.
 */
fun AppCompatActivity.dialog(title: String,
                             message: String,
                             isPositiveLabelBold:Boolean= true,
                             isCancelable: Boolean=true,
                             positiveLabel: String,
                             negativeLabel: String,
                             positiveFunction:()->Boolean,
                             negativeFunction:()->Boolean= {false},
                             dialogType: DialogType= DialogType.NEUTRAL) {

    showDialog(this, title, message,
            positiveLabel, negativeLabel,
            object:ClickListener{
                override fun onClick(): Boolean {
                    return positiveFunction()
                }

            },
            object:ClickListener{
                override fun onClick(): Boolean {
                    return negativeFunction()
                }

            },
            isCancelable,
            dialogType)
}

fun AppCompatActivity.dialog(title: String,
                             txtMessage: TextView,
                             isPositiveLabelBold:Boolean= true,
                             isCancelable: Boolean=true,
                             positiveLabel: String,
                             negativeLabel: String,
                             positiveFunction:()->Boolean,
                             negativeFunction:()->Boolean= {false},
                             dialogType: DialogType = DialogType.NEUTRAL) {
    showDialog(this, title, txtMessage,
            positiveLabel, negativeLabel,
            object:ClickListener{
                override fun onClick(): Boolean {
                    return positiveFunction()
                }

            },
            object: ClickListener {
                override fun onClick(): Boolean {
                    return negativeFunction()
                }

            },
            isCancelable,
            dialogType)
}

fun AppCompatActivity.redirectToDashboard(user: User){
    if(user is Admin){
        startActivity(Intent(this, AdminDashboardActivity::class.java))
        finish()
    }else if (user is Seller){
        startActivity(Intent(this, SellerDashboardActivity::class.java))
        finish()
    }
}