package com.compose.chatcomponents.helper

import android.util.Log





fun String.logLongMessage(tag:String){
    val maxLogSize = 2500
    for (i in 0..this.length / maxLogSize) {
        val start = i * maxLogSize
        var end = (i + 1) * maxLogSize
        end = if (end > this.length) this.length else end
        Log.v(tag, this.substring(start, end))
    }
}