package me.andreww7985.connectplus.ui

import android.support.v4.app.Fragment
import me.andreww7985.connectplus.controller.FragmentController

abstract class IUpdatableFragment : Fragment() {
    abstract fun update()
    override fun onResume() {
        FragmentController.view = this
        super.onResume()
    }

    override fun onPause() {
        FragmentController.view = null
        super.onPause()
    }
}