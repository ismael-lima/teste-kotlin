package com.ismael.movieapp.customview

import android.content.Context.WINDOW_SERVICE
import android.view.WindowManager
import android.widget.TextView
import android.content.Context
import android.graphics.Point
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat.getColor
import com.ismael.movieapp.R


class MyToolbar : Toolbar {

    private var _titleTextView: TextView? = null
    private var _screenWidth: Int = 0

    private val screenSize: Point
        get() {
            val wm = getContext().getSystemService(WINDOW_SERVICE) as WindowManager
            val display = wm.defaultDisplay
            val screenSize = Point()
            display.getSize(screenSize)

            return screenSize
        }

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        _screenWidth = screenSize.x

        _titleTextView = TextView(getContext())
        _titleTextView?.text = ""
        _titleTextView?.setTextColor(getColor(context,R.color.colorTitle))
        _titleTextView?.setTextSize(getResources().getDimension(R.dimen.title_size))
        _titleTextView?.gravity = Gravity.CENTER
        addView(_titleTextView)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        val location = IntArray(2)
        _titleTextView!!.getLocationOnScreen(location)
        _titleTextView!!.translationX =
            _titleTextView!!.translationX + (-location[0] + _screenWidth / 2 - _titleTextView!!.width / 2)

    }

    override fun setTitle(title: CharSequence) {
        _titleTextView!!.text = title
        requestLayout()
    }

    override fun setTitle(titleRes: Int) {
        _titleTextView!!.setText(titleRes)
        requestLayout()
    }

    fun setTitleSize(size: TitleSize) {
        if(size == TitleSize.SMALL)
            _titleTextView?.setTextSize(getResources().getDimension(R.dimen.title_size_small))
        else
            _titleTextView?.setTextSize(getResources().getDimension(R.dimen.title_size))
    }
    enum class TitleSize {
        NORMAL,SMALL
    }
}