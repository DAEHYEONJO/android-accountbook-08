package com.example.accountbook.presentation

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
    private var openInitiated = false

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

    override fun performClick(): Boolean {
        openInitiated = true
        if (onSpinnerEventsListener != null) {
            onSpinnerEventsListener!!.onPopupWindowOpened(this)
        }
        return super.performClick()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        if (openInitiated && hasFocus) {
            performClosedEvent()
        }
    }

    private fun performClosedEvent() {
        openInitiated = false
        if (onSpinnerEventsListener != null) {
            onSpinnerEventsListener!!.onPopupWindowClosed(this)
        }
    }
}