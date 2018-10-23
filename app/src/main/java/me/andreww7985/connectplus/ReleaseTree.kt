package me.andreww7985.connectplus

import android.util.Log
import timber.log.Timber

class ReleaseTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority != Log.DEBUG)
            super.log(priority, tag, message, t)
    }
}