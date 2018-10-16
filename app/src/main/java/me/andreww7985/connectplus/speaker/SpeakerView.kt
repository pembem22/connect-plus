package me.andreww7985.connectplus.speaker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_dashboard.*
import me.andreww7985.connectplus.R
import me.andreww7985.connectplus.mvp.BaseView
import me.andreww7985.connectplus.mvp.PresenterManager

class SpeakerView : BaseView, Fragment() {
    companion object {
        const val TAG = "SpeakerView"
    }

    private val presenter = PresenterManager.getPresenter(SpeakerPresenter::class.java) as SpeakerPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    fun setDeveloperData(mac: String, data: String, color: String, model: String) {
        mac_value.text = mac
        data_value.text = data
        color_value.text = color
        model_value.text = model
    }

    fun setSpeakerImages(logoDrawableId: Int, speakerDrawableId: Int) {
        if (speakerDrawableId != 0) product_image.setImageResource(speakerDrawableId)
        if (logoDrawableId != 0) product_logo.setImageResource(logoDrawableId)
    }

    fun showBatteryNameFeature(deviceName: String, batteryLevel: Int, batteryCharging: Boolean) {
        dashboard_battery_value.text = if (batteryCharging == true)
            getString(R.string.dashboard_battery_level_charging, batteryLevel)
        else
            getString(R.string.dashboard_battery_level, batteryLevel)
        dashboard_name_value.text = deviceName

        dashboard_battery_name_feature.visibility = View.VISIBLE
    }

    fun showFirmwareVersionFeature(minor: Int, major: Int, build: Int?) {
        dashboard_firmware_version_value.text =
                "$major.$minor${if (build != null) ".$build" else ""}"

        dashboard_firmware_version_feature.visibility = View.VISIBLE
    }

    fun showFeedbackSoundsDisabledSnackbar() {
        Snackbar.make(view!!, getString(R.string.dashboard_feedback_sounds_disabled), Snackbar.LENGTH_SHORT).show()
    }

    fun showFeedbackSoundsFeature(enabled: Boolean) {
        dashboard_feedback_sounds_value.isChecked = enabled

        dashboard_feedback_sounds_feature.visibility = View.VISIBLE
    }

    fun showSpeakerphoneModeFeature(enabled: Boolean) {
        dashboard_speakerphone_mode_value.isChecked = enabled

        dashboard_speakerphone_mode_feature.visibility = View.VISIBLE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        product_audio_button.setOnClickListener { presenter.onPlaySoundPressed() }
        dashboard_speakerphone_mode_value.setOnCheckedChangeListener { _, isChecked -> presenter.onSpeakerphoneModeChanged(isChecked) }
        dashboard_feedback_sounds_value.setOnCheckedChangeListener { _, isChecked -> presenter.onFeedbackSoundsChanged(isChecked) }

        presenter.attachView(this)
    }

    override fun onDestroy() {
        super.onDestroy()

        presenter.detachView()

        if (isRemoving) {
            PresenterManager.destroyPresenter(SpeakerPresenter::class.java)
        }
    }
}
