package me.andreww7985.connectplus.helpers

import android.content.Intent
import android.content.res.Configuration
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.WindowCompat
import androidx.fragment.app.commit
import com.google.android.material.elevation.SurfaceColors
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialSharedAxis
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

    fun updateSystemBarsAppearance(activity: AppCompatActivity, hasNavbar: Boolean = false) {
        activity.window.navigationBarColor =
            (if (hasNavbar) SurfaceColors.SURFACE_2 else SurfaceColors.SURFACE_0).getColor(activity)

        val nightModeEnabled =
            when (AppCompatDelegate.getDefaultNightMode()) {
                AppCompatDelegate.MODE_NIGHT_YES -> true
                AppCompatDelegate.MODE_NIGHT_NO -> false
                else -> when (activity.baseContext.resources?.configuration?.uiMode?.and(
                    Configuration.UI_MODE_NIGHT_MASK
                )) {
                    Configuration.UI_MODE_NIGHT_YES -> true
                    else -> false
                }
            }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            val decor: View = activity.window.decorView
//            if (shouldChangeStatusBarTintToDark) {
//                decor.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
//            } else {
//                // We want to change tint color to white again.
//                // You can also record the flags in advance so that you can turn UI back completely if
//                // you have set other flags before, such as translucent or full screen.
//                decor.systemUiVisibility = 0
//            }
//        }

        Timber.d("DARK MODE -------------------- $nightModeEnabled")

        WindowCompat.getInsetsController(
            activity.window,
            activity.window.decorView
        )
//            .isAppearanceLightNavigationBars = !nightModeEnabled
    }
}