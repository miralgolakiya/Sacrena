package com.compose.chatcomponents.helper

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatTextView

class AutoHideTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    // Handler to manage the delay
    private val handler = Handler(Looper.getMainLooper())

    // Runnable to hide the view after 2 seconds
    private val hideRunnable = Runnable { this.visibility = View.GONE }

    // Duration after which the view should hide (2 seconds)
    private val hideDelay = 2000L

    // Method to start the countdown to hide the TextView
    private fun startHideCountdown() {
        handler.removeCallbacks(hideRunnable)  // Cancel previous hide request
        handler.postDelayed(hideRunnable, hideDelay)  // Schedule hide after 2 seconds
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)

        // Only start the hide countdown when the view becomes visible
        if (visibility == View.VISIBLE) {
            handler.removeCallbacks(hideRunnable)
            startHideCountdown()
        }
    }

    // Public method to show the TextView and reset the hide countdown
    fun hideText(string: String) {
        text = string
        this.visibility = View.VISIBLE
    }
}