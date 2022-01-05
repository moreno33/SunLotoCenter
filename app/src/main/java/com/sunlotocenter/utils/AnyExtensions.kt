package com.sunlotocenter.utils

import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.gson.GsonBuilder
import com.kaopiz.kprogresshud.KProgressHUD
import com.sunlotocenter.MyApplication
import com.sunlotocenter.R
import com.sunlotocenter.adapter.ClassTypeAdapter
import com.sunlotocenter.adapter.ClassTypeAdapterFactory
import com.sunlotocenter.adapter.DateTimeTypeAdapter
import com.sunlotocenter.adapter.UserTypeAdapter
import com.sunlotocenter.api.BankApi
import com.sunlotocenter.api.GameApi
import com.sunlotocenter.api.NotificationApi
import com.sunlotocenter.api.UserApi
import com.sunlotocenter.dao.Entity
import kotlinx.android.synthetic.main.dialog_layout.view.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.LocalTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.security.SecureRandom
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList


val random = SecureRandom()
val ACTION_LOGIN= "login"
val ACTION_SAVE= "save"

val USER_EXTRA= "USER_EXTRA"
val REFRESH_REQUEST_CODE= 3000

val BANK_EXTRA= "SUN_LOTO_CENTER_USER_EXTRA"
val GAME_PRICE_EXTRA= "GAME_PRICE_EXTRA"
val BLOCKED_GAME_EXTRA= "BLOCKED_GAME_EXTRA"
val GAME_SCHEDULE_EXTRA= "GAME_SCHEDULE_EXTRA"
val RESULT_EXTRA= "RESULT_EXTRA"
val SLOT_LIST_EXTRA= "SLOT_LIST_EXTRA"
val GAME_SESSION_EXTRA= "GAME_SESSION_EXTRA"
val REPORT_EXTRA= "REPORT_EXTRA"
val BROADCAST_NOTIF_EXTRA= "BROADCAST_NOTIF_EXTRA"
val COPIED_GAME_LIST= "COPIED_GAME_LIST"
val SLOT_EXTRA= "SLOT_EXTRA"
val COMPANY_EXTRA= "COMPANY_EXTRA"
val GAME_TYPE_EXTRA= "GAME_TYPE_EXTRA"

val userApi =
    MyApplication.getInstance().clientNetworking.create(UserApi::class.java)
val gameApi= MyApplication.getInstance().clientNetworking.create(GameApi::class.java)
val bankApi= MyApplication.getInstance().clientNetworking.create(BankApi::class.java)
val notificationApi= MyApplication.getInstance().clientNetworking.create(NotificationApi::class.java)

fun createPart(url:String, name:String): MultipartBody.Part{
    val file= File(url)
    val reqBody= RequestBody.create(MediaType.parse(getMimeType(url)), file)
    val part= MultipartBody.Part.createFormData(name, file.name, reqBody)
    return part
}


fun getMimeType(url:String):String{
    var type:String?= null
    val extension:String= getFileExtensionFromUrl(url)
    type= MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
    return type!!
}


fun getFileExtensionFromUrl(url:String):String {
    if (!TextUtils.isEmpty(url)) {
        var fragment = url.lastIndexOf('#');
        var edittableUrl = url
        if (fragment > 0) {
            edittableUrl = url.substring(0, fragment);
        }

        var query = edittableUrl.lastIndexOf('?');
        if (query > 0) {
            edittableUrl = edittableUrl.substring(0, query);
        }

        var filenamePos = edittableUrl.lastIndexOf('/');
        var filename = if (0 <= filenamePos) edittableUrl.substring(filenamePos + 1) else url

        // if the filename contains special characters, we don't
        // consider it valid for our matching purposes:
        var dotPos = filename.lastIndexOf('.');
        if (0 <= dotPos) {
            return filename.substring(dotPos + 1);
        }
    }
    return "";
}

fun getGson()= GsonBuilder()
    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    .registerTypeAdapterFactory(ClassTypeAdapterFactory())
    .registerTypeAdapter(Class::class.java,  ClassTypeAdapter())
    .registerTypeAdapter(Entity::class.java,  UserTypeAdapter<Entity>())
//    .registerTypeAdapter(Partner::class.java,  AccountTypeAdapter<Partner>())
//    .registerTypeAdapter(Notification::class.java, NotificationTypeAdapter<Notification>())
//    .registerTypeAdapter(Promo::class.java,  PromoTypeAdapter<Promo>())
    .registerTypeAdapter(DateTime::class.java,  DateTimeTypeAdapter())
    .create()

fun glide(ctx: Context, url: String?= null, target: ImageView, placeholder: Int, @DrawableRes def: Int) {
    Glide.with(ctx)
        .load(url)
        .centerCrop()
        .apply(RequestOptions.errorOf(def))
        .placeholder(placeholder)
        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        .dontAnimate()
        .into(target)
}



/**
 * Helper function to generate a random number n digits in length using a system random.
 */
fun randomWithNDigits(n: Int): String {
    var n = n

    n = Math.abs(n)

    return String.format("%s", Math.pow(10.0, (n - 1).toDouble()).toInt() + random.nextInt(9 * Math.pow(10.0, (n - 1).toDouble()).toInt()))
}

fun createOrGetFile(destination: File, fileName: String, directory: String): File {
    val folder = File(destination, directory)
    if (!folder.exists()) {
        if (!folder.mkdirs()) {
            throw IllegalAccessError()
        }
    }
    return File(folder, fileName)
}

fun getDateString(date: DateTime): String {
    //This is the formatter to get a right format for the date
    val formatter = DateTimeFormat.mediumDate().withLocale(Locale.getDefault()).withZone(DateTimeZone.getDefault())
    return formatter.print(date)
}

fun getDateString(date: DateTime, zone:DateTimeZone): String {
    //This is the formatter to get a right format for the date
    val formatter = DateTimeFormat.mediumDate().withZone(zone).withLocale(Locale.getDefault())
    return formatter.print(date)
}

fun getTimeString(time: LocalTime, zone:DateTimeZone): String {
    //This is the formatter to get a right format for the date
    val formatter = DateTimeFormat.shortTime().withZone(zone).withLocale(Locale.getDefault())
    return formatter.print(time)

}


fun getDateString(dateString: String): String {

    val date= DateTime.parse(dateString, DateTimeFormat.forPattern("yyyy-MM-dd"))
    val formatter = DateTimeFormat.mediumDate().withLocale(Locale.getDefault()).withZone(DateTimeZone.getDefault())
    return formatter.print(date)

}

fun getDateString(dateString: String, zone: DateTimeZone): String {

    val date= DateTime.parse(dateString, DateTimeFormat.forPattern("yyyy-MM-dd"))
    val formatter = DateTimeFormat.mediumDate().withZone(zone).withLocale(Locale.getDefault())
    return formatter.print(date)

}

fun getDateString(date: DateTime?, formatter: DateTimeFormatter): String? {
    //This is the formatter to get a right format for the date
    if(date== null) return null

    val formatter = formatter.withZone(DateTimeZone.getDefault())
    return formatter.print(date)
}

fun getDateString(date: DateTime?, formatter: DateTimeFormatter, timeZone: DateTimeZone): String? {
    //This is the formatter to get a right format for the date
    if(date== null) return null

    val formatter = formatter.withZone(timeZone)
    return formatter.print(date)
}


fun getShortDateString(date: DateTime?): String? {
    //This is the formatter to get a right format for the date
    if(date== null) return null

    val formatter = DateTimeFormat.forPattern("yyyy-MM-dd").withZone(DateTimeZone.getDefault())
    return formatter.print(date)
}


fun getDate(date: DateTime): DateTime? {
    val formatter = DateTimeFormat.forPattern("yyyy-MM-dd").withZone(DateTimeZone.getDefault())
    return formatter.parseDateTime(formatter.print(date))
}

fun getDate(date: DateTime, zone: DateTimeZone): DateTime? {
    val formatter = DateTimeFormat.forPattern("yyyy-MM-dd").withZone(zone)
    return formatter.parseDateTime(formatter.print(date))
}

fun getTimeString(ctx: Context, date: DateTime): String {
    //This is the formatter to get a right format for the date
    val formatter = DateTimeFormat.mediumTime().withLocale(Locale.getDefault()).withZone(DateTimeZone.getDefault())
    return formatter.print(date)
}

@Throws(IOException::class)
fun savebitmap(bmp: Bitmap, url: String): File {
    val bytes = ByteArrayOutputStream()
    bmp.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
    val f = getOutputMediaFile(url)
    val fo = FileOutputStream(f!!)
    fo.write(bytes.toByteArray())
    fo.close()
    return f
}

fun getDateTimeString(ctx: Context, date: DateTime): String {
    val formatter = DateTimeFormat.mediumDateTime().withZone(DateTimeZone.getDefault())
    return formatter.print(date)
}

fun getDateTimeString(date: DateTime, zone: DateTimeZone): String {
    val formatter = DateTimeFormat.mediumDateTime().withZone(zone)
    return formatter.print(date)
}

fun getOutputMediaFile(url: String): File? {
    val mediaStorageDir = File(
        Environment
            .getExternalStorageDirectory(),
        "Sun Loto Center")
    if (!mediaStorageDir.exists()) {
        if (!mediaStorageDir.mkdirs()) {
            return null
        }
    }
    // Create a media file name

    val imageNameArray = url.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    val imageName = "SUN_" + System.currentTimeMillis() + "_" + randomWithNDigits(10) + ".PNG"
    return File(mediaStorageDir.path + File.separator
            + imageName)
}

fun filePathFromUri(uri: Uri, ctx: Context): String? {
    val proj = arrayOf(MediaStore.Images.Media.DATA)
    val cursor = ctx.contentResolver.query(uri, proj, null, null, null)
    var path: String? = null
    if (cursor!!.moveToFirst()) {
        val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        path = cursor.getString(column_index)
    }
    cursor.close()
    return path
}

fun dateUntil(startDate: DateTime, endDate: DateTime): List<Date> {
    var sd = getDate(startDate, DateTimeZone.forID("America/New_York"))

    val ed = getDate(endDate, DateTimeZone.forID("America/New_York"))

    val allDates = ArrayList<Date>()

    while (sd!!.isBefore(ed) || sd == ed) {
        /*
        Now we need to add this date to our list only if it's
        lower than today.
         */
        if (sd.isAfter(getDate(DateTime(), DateTimeZone.forID("America/New_York"))) || sd == getDate(DateTime(), DateTimeZone.forID("America/New_York"))) {
            allDates.add(sd.toLocalDate().toDate())
        }
        sd = sd.plusDays(1)
    }
    return allDates
}


/**
 * Allows us to create a simple neutral dialog with only one button.
 * @param context
 * @param title
 * @param message
 * @param neutralText
 */
fun showDialog(context: Context, title: String, message: String, neutralText: String,
               listener: ClickListener,
               isCancelable: Boolean,
               dialogType: DialogType) {
    val builder = AlertDialog.Builder(context)
    val view = LayoutInflater.from(context).inflate(R.layout.dialog_layout, null)

    val txtTitle = view.findViewById<TextView>(R.id.txtTitle)
    txtTitle.text = title
    val txtMessage = view.findViewById<TextView>(R.id.txtMessage)
    txtMessage.text = message
    val txtPositiveButton = view.btnPositive
    txtPositiveButton.text = neutralText


    val dialog = builder.setView(view).show()
    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.setCancelable(isCancelable)
    txtPositiveButton.setOnClickListener { view1 -> if (!listener.onClick()) dialog.dismiss() }

    controlDialogType(dialogType, view, context)
}

    private fun controlDialogType(dialogType: DialogType, view: View, context: Context) {
        when(dialogType){
            DialogType.NEUTRAL-> {
                view.btnPositive.background= ContextCompat.getDrawable(context, R.drawable.black_corner_radius)
            }
            DialogType.SUCCESS-> view.btnPositive.background= ContextCompat.getDrawable(context, R.drawable.green_corner_radius)
            DialogType.WARNING-> {
                view.btnPositive.background= ContextCompat.getDrawable(context, R.drawable.yellow_corner_radius)
                view.btnPositive.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
            }
            DialogType.ERROR-> view.btnPositive.background= ContextCompat.getDrawable(context, R.drawable.red_corner_radius)
        }
    }

/**
 * Allows us to create a simple dialog with 2 buttons.
 * @param context
 * @param title
 * @param message
 * @param positiveText
 * @param negativeText
 * @param positiveListener
 * @param negativeListener
 */
fun showDialog(context: Context, title: String, message: String,
               positiveText: String, negativeText: String,
               positiveListener: ClickListener,
               negativeListener: ClickListener,
               isCancelable: Boolean,
               dialogType: DialogType= DialogType.NEUTRAL) {
    val builder = AlertDialog.Builder(context)
    val view = LayoutInflater.from(context).inflate(R.layout.dialog_layout, null)

    val txtTitle = view.findViewById<TextView>(R.id.txtTitle)
    txtTitle.text = title
    val txtMessage = view.findViewById<TextView>(R.id.txtMessage)
    txtMessage.text = message
    val txtPositiveButton = view.findViewById<TextView>(R.id.btnPositive)
    txtPositiveButton.text = positiveText
    val txtNegativeButton = view.findViewById<TextView>(R.id.btnNegative)
    txtNegativeButton.text = negativeText
    txtNegativeButton.visibility = View.VISIBLE
    controlDialogType(dialogType, view, context)

    val dialog = builder.setView(view).show()
    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.setCancelable(isCancelable)

    txtPositiveButton.setOnClickListener { view1 -> if (!positiveListener.onClick()) dialog.dismiss() }
    txtNegativeButton.setOnClickListener { view12 -> if (!negativeListener.onClick()) dialog.dismiss() }
}

/**
 * Here we can give our own text view to the dialog.
 * @param context
 * @param title
 * @param txtMessage
 * @param positiveText
 * @param negativeText
 * @param positiveListener
 * @param negativeListener
 */
fun showDialog(context: Context, title: String, txtMessage: TextView,
               positiveText: String, negativeText: String,
               positiveListener: ClickListener,
               negativeListener: ClickListener,
               isCancelable: Boolean,
               dialogType: DialogType= DialogType.NEUTRAL) {
    val builder = AlertDialog.Builder(context)
    val view = LayoutInflater.from(context).inflate(R.layout.dialog_layout, null)

    val txtTitle = view.findViewById<TextView>(R.id.txtTitle)
    txtTitle.text = title
    view.findViewById<View>(R.id.txtMessage).visibility = View.GONE
    val centerView = view.findViewById<LinearLayout>(R.id.view)
    txtMessage.layoutParams = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.WRAP_CONTENT,
        LinearLayout.LayoutParams.WRAP_CONTENT)
    centerView.addView(txtMessage)
    val txtPositiveButton = view.findViewById<Button>(R.id.btnPositive)
    txtPositiveButton.text = positiveText
    val txtNegativeButton = view.findViewById<TextView>(R.id.btnNegative)
    txtNegativeButton.text = negativeText
    txtNegativeButton.visibility = View.VISIBLE

    val dialog = builder.setView(view).show()
    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.setCancelable(isCancelable)
    txtPositiveButton.setOnClickListener { v -> if (!positiveListener.onClick()) dialog.dismiss() }
    txtNegativeButton.setOnClickListener { v -> if (!negativeListener.onClick()) dialog.dismiss() }

    controlDialogType(dialogType, view, context)
}

/**
 * Here we can give a custom view to the dialog
 * @param context
 * @param title
 * @param child
 * @param positiveText
 * @param negativeText
 * @param positiveListener
 * @param negativeListener
 */
fun showDialog(context: Context, title: String, child: View,
               positiveText: String, negativeText: String,
               positiveListener: ClickListener,
               negativeListener: ClickListener,
               dialogType: DialogType= DialogType.NEUTRAL) {
    //Here we create the dialog.
    val builder = AlertDialog.Builder(context)
    //This the view from wich we are going to inflate the children.
    val view = LayoutInflater.from(context).inflate(R.layout.dialog_layout, null)

    //The title of the dialog.
    val txtTitle = view.findViewById<TextView>(R.id.txtTitle)
    txtTitle.text = title
    //We need to hide the already there textview.
    view.findViewById<View>(R.id.txtMessage).visibility = View.GONE
    //This is where we are going to add our custom childview
    val centerView = view.findViewById<LinearLayout>(R.id.view)
    child.layoutParams = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT)
    centerView.addView(child)
    val btnPositiveButton = view.findViewById<Button>(R.id.btnPositive)
    btnPositiveButton.text = positiveText
    val btnNegativeButton = view.findViewById<Button>(R.id.btnNegative)
    btnNegativeButton.text = negativeText
    btnNegativeButton.visibility = View.VISIBLE

    val dialog = builder.setView(view).show()
    dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.setCancelable(false)
    btnPositiveButton.setOnClickListener { v ->
        if (!positiveListener.onClick()) dialog.dismiss()

    }
    btnNegativeButton.setOnClickListener { v -> if (!negativeListener.onClick()) dialog.dismiss() }

    controlDialogType(dialogType, view, context)
}

fun clickableString(textView: TextView, textToShow: String, listener: ClickListener) {
    var textToShow = textToShow

    /*
    Here we get the index of the #
     */
    val start = textToShow.indexOf("#")
    val end = textToShow.lastIndexOf("#") - 1

    /*
    Now we can replace them
     */
    textToShow = textToShow.replace("#", "")

    val ss = SpannableString(textToShow)

    val clickableSpan = object : ClickableSpan() {
        override fun onClick(textView: View) {
            listener.onClick()
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.isUnderlineText = true
        }
    }


    ss.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

    textView.text = ss
    textView.movementMethod = LinkMovementMethod.getInstance()
    textView.highlightColor = Color.TRANSPARENT
}

fun clickableHtmlString(textView: TextView, textToShow: String, listener: ClickListener) {
    var textToShow = textToShow
    /*
    Here we get the index of the #
     */
    var start = textToShow.indexOf("#")
    var end = textToShow.lastIndexOf("#")

    val word = textToShow.substring(start + 1, end)

    /*
    Now we can replace them
     */
    textToShow = textToShow.replace("#", "")


    val ss = SpannableString(HtmlCompat.fromHtml(textToShow, HtmlCompat.FROM_HTML_MODE_COMPACT))

    val clickableSpan = object : ClickableSpan() {
        override fun onClick(textView: View) {
            listener.onClick()
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.isUnderlineText = true
        }
    }

    start = ss.toString().indexOf(word)
    end = start + word.length

    ss.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

    textView.text = ss
    textView.movementMethod = LinkMovementMethod.getInstance()
    textView.highlightColor = Color.TRANSPARENT
}

/**
 * Return a top layer indeterminate dialog
 * @param ctx
 * @return
 */
fun getDialog(ctx: Context): KProgressHUD {
    return KProgressHUD.create(ctx)
        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
        .setLabel(ctx.getString(R.string.please_wait))
        .setCancellable(false)
        .setAnimationSpeed(2)
        .setDimAmount(0.5f)
}

//fun formatPhoneNumber(phone: String, region: String): String {
//    val phoneNumberUtil = PhoneNumberUtil.getInstance()
//    val phoneNumber: Phonenumber.PhoneNumber
//    try {
//        phoneNumber = phoneNumberUtil.parse(phone,
//            region)
//        return phoneNumberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.NATIONAL)
//    } catch (e: NumberParseException) {
//        return phone
//    }
//
//}


////Used to animate view.
//fun bounce(view: View, ctx: Context) {
//    val animation = AnimationUtils.loadAnimation(ctx, R.anim.anim_bounce)
//    view.startAnimation(animation)
//}

interface ClickListener {
    /**
     * Return true when the click is handled.
     * @return
     */
    fun onClick(): Boolean
}

enum class DialogType {
    NEUTRAL,
    SUCCESS,
    ERROR,
    WARNING
}

fun format(value:Double):String{
    val df = DecimalFormat("#.##");
    return df.format(value)

}


fun isNotEmpty(text:String?):Boolean{
    if(text!= null && text.trim().isNotEmpty())
        return true
    return false
}
//
//fun openSlider(ctx:Context, paths:ArrayList<String>){
//    val intent = Intent(ctx, SliderActivity::class.java)
//
//    intent.putExtra(SliderActivity.IMAGES_EXTRA, paths)
//
//    ctx.startActivity(intent)
//}

