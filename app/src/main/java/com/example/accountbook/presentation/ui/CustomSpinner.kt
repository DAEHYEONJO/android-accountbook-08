package com.example.accountbook.presentation.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.Spinner
import androidx.appcompat.widget.AppCompatSpinner

class CustomSpinner : AppCompatSpinner {
    interface OnSpinnerEventsListener {
        fun onPopupWindowOpened(spinner: Spinner?)
        fun onPopupWindowClosed(spinner: Spinner?)
    }

    private var onSpinnerEventsListener: OnSpinnerEventsListener? = null


    constructor(context: Context?) : super(context!!)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr)

    fun setSpinnerEventsListener(
        onSpinnerEventsListener: OnSpinnerEventsListener?
    ) {
        this.onSpinnerEventsListener = onSpinnerEventsListener
    }

    private var openInitiated = false
    override fun performClick(): Boolean {
        openInitiated = true
        onSpinnerEventsListener?.onPopupWindowOpened(this)
        return super.performClick()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        if (openInitiated && hasFocus) {
            performClosedEvent()
        }
    }

    private fun performClosedEvent() {
        openInitiated = false
        onSpinnerEventsListener?.onPopupWindowClosed(this)
    }
}