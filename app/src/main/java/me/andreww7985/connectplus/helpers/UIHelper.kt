package me.andreww7985.connectplus.helpers

import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import android.util.Log
import android.widget.Toast
import me.andreww7985.connectplus.ConnectPlusApp
import me.andreww7985.connectplus.R
import me.andreww7985.connectplus.speakers.Speaker
import me.andreww7985.connectplus.ui.activities.MainActivity

object UIHelper {
    private const val TAG = "UIHelper"
    private val app = ConnectPlusApp.instance
    private val resources = app.resources
    private val packageName = app.packageName

    fun getSpeakerImage(speaker: Speaker): Int? {
        val imageName = String.format("img_%s_%s", speaker.model.toString().toLowerCase(), speaker.color.toString().toLowerCase())
        Log.d(TAG, "getSpeakerImage image name = $imageName")
        val imageID = resources.getIdentifier(imageName, "drawable", packageName)
        return if (imageID != 0) imageID else null
    }

    fun getLogoImage(speaker: Speaker): Int? {
        val imageName = String.format("logo_%s", speaker.model.toString().toLowerCase())
        Log.d(TAG, "getLogoImage image name = $imageName")
        val imageID = resources.getIdentifier(imageName, "drawable", packageName)
        return if (imageID != 0) imageID else null
    }

    fun showToast(text: String, duration: Int) {
        ConnectPlusApp.instance.let {
            Toast.makeText(app, text, duration).show()
        }
    }

    fun openMainActivity() {
        Log.d(TAG, "openMainActivity")
        val intent = Intent(app, MainActivity::class.java)
        app.startActivity(intent)
    }

    fun showFragment(activity: androidx.fragment.app.FragmentActivity, fragment: androidx.fragment.app.Fragment) {
        Log.d(TAG, "showFragment fragment name = ${fragment.javaClass.simpleName}")
        activity.supportFragmentManager.beginTransaction().replace(R.id.main_fragment, fragment).commitAllowingStateLoss()
    }
}