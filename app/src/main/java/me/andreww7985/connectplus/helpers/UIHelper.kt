package me.andreww7985.connectplus.helpers

import android.content.Intent
import android.view.View
import androidx.fragment.app.commit
import com.google.android.material.snackbar.Snackbar
import me.andreww7985.connectplus.App
import me.andreww7985.connectplus.R
import me.andreww7985.connectplus.ui.activities.MainActivity
import timber.log.Timber


object UIHelper {
    private val app = App.instance

    fun showToast(
        view: View,
        text: String,
        duration: Int = Snackbar.LENGTH_SHORT,
    ) {
        Snackbar.make(view, text, duration).setAnchorView(view.rootView.findViewById(R.id.nav_menu)).show()
    }

    fun openMainActivity() {
        Timber.d("openMainActivity")
        val intent = Intent(app, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        app.startActivity(intent)
    }

    fun showFragment(
        activity: androidx.fragment.app.FragmentActivity,
        fragment: androidx.fragment.app.Fragment
    ) {
        Timber.d("showFragment fragment name = ${fragment.javaClass.simpleName}")
        activity.supportFragmentManager.commit(allowStateLoss = true) {
            replace(R.id.main_fragment, fragment)
        }
    }
}