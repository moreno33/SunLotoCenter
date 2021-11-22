package com.sunlotocenter.validator

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.emarkall.worldwidephonenumberedittext.Validable
import com.sunlotocenter.activity.R
import com.sunlotocenter.adapter.ErrorAdapter
import kotlinx.android.synthetic.main.validatable_edit_text_layout.view.*
import android.view.inputmethod.EditorInfo
import org.w3c.dom.TypeInfo


class ValidatableEditText : LinearLayout, Validable {

    constructor(context: Context?) : super(context) {
        init()
    }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
        setUpAttributes(attrs)
    }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
        setUpAttributes(attrs)
    }

    var text: String= ""
        set(value) {
            edxText.setText(value)
            field= value
        }
        get() { return edxText.text.toString()}
    private var hint: String?= ""
    private var textSize: Float?= 0f
    private var textColor: Int?= 0
    private var drawableLeft: Drawable?= null
    private var inputType: Int= EditorInfo.TYPE_CLASS_TEXT
    private var myHeight: Float= 0f
    private var maxLength: Int= 0
    private var active= true

    val validators= ArrayList<MyValidator>()
    private val errMsgs= ArrayList<String>()

    fun addValidator(vararg v: MyValidator){
        v.forEach {
           validators.add(it)
        }
    }

    override fun isValid(): Boolean{

        synchronized(this){
            var isValid= true
            errMsgs.clear()
            validators.forEach {
                it.editText= this
                if(!it.isValid()) {
                    isValid= false
                    errMsgs.add(it.errMsg)
                }
            }

            invalidate()
            var adapter= ErrorAdapter(this.errMsgs)
            rclError.layoutManager= LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            rclError.adapter= adapter

            return isValid
        }

    }


    private fun setUpAttributes(attrs: AttributeSet?) {
        val typedArray: TypedArray =
            context.theme.obtainStyledAttributes(attrs, R.styleable.ValidatableEditText, 0, 0)
        try {
            textColor = typedArray.getColor(
                R.styleable.ValidatableEditText_text_color,
                Color.parseColor("#393E46")
            )
            textSize =
                typedArray.getDimensionPixelSize(R.styleable.ValidatableEditText_text_size, 12).toFloat()
            text = if(typedArray.getString(R.styleable.ValidatableEditText_text)!= null) typedArray.getNonResourceString(R.styleable.ValidatableEditText_text) else ""
            hint= typedArray.getString(R.styleable.ValidatableEditText_hint)
            drawableLeft = typedArray.getDrawable(R.styleable.ValidatableEditText_drawable_left)
            inputType = typedArray.getInt(R.styleable.ValidatableEditText_android_inputType, EditorInfo.TYPE_CLASS_TEXT)
            myHeight = typedArray.getLayoutDimension(R.styleable.ValidatableEditText_android_layout_height, 0).toFloat()
            maxLength= typedArray.getInt(R.styleable.ValidatableEditText_max_length, 0)
            active= typedArray.getBoolean(R.styleable.ValidatableEditText_active, true)
            addAttributes()

        } finally {
            typedArray.recycle()
        }
    }

    /**
     * Here we initialize the necessary view
     */
    private fun init() {
        inflate(context, R.layout.validatable_edit_text_layout, this)
    }

    /**
     * This method allows to parse the attributes to the view
     */
    private fun addAttributes() {
        textSize.let{ edxText.setTextSize(TypedValue.COMPLEX_UNIT_PX, it!!) }
        textColor.let { edxText.setTextColor(it!!) }
        edxText.setText(text)
        edxText.hint= hint
        edxText.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, null, null);
        edxText.inputType= inputType
        if (myHeight> 0f){
            val layoutParams= edxText.layoutParams
            layoutParams.height= myHeight.toInt()
            edxText.layoutParams= layoutParams
        }
        if (maxLength> 0){
            edxText.filters= arrayOf(InputFilter.LengthFilter(maxLength))
        }
        edxText.isEnabled= active
    }

    fun addTextChangedListener (textWatcher: TextWatcher){
        this.edxText.addTextChangedListener(textWatcher)
    }

    fun setSelection(position:Int){
        this.edxText.setSelection(position)
    }
    fun focus(){
        this.edxText.requestFocus()
    }
}