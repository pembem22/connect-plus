package me.andreww7985.connectplus.speaker

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.fragment_dashboard.*
import me.andreww7985.connectplus.R
import me.andreww7985.connectplus.manager.PresenterManager
import me.andreww7985.connectplus.mvp.BaseView

class SpeakerView : BaseView, Fragment() {
    private val presenter = PresenterManager.getPresenter(SpeakerPresenter::class.java) as SpeakerPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    fun setDeveloperData(mac: String, data: String, color: String, model: String) {
        activity?.runOnUiThread {
            view ?: return@runOnUiThread

            mac_value.text = mac
            data_value.text = data
            color_value.text = color
            model_value.text = model
        }
    }

    fun setSpeakerImages(logoDrawableId: Int, speakerDrawableId: Int) {
        activity?.runOnUiThread {
            view ?: return@runOnUiThread

            if (speakerDrawableId != 0) dashboard_product_image.setImageResource(speakerDrawableId)
            if (logoDrawableId != 0) dashboard_product_logo.setImageResource(logoDrawableId)
        }
    }

    fun showBatteryNameFeature(deviceName: String, batteryLevel: Int, batteryCharging: Boolean) {
        activity?.runOnUiThread {
            view ?: return@runOnUiThread

            dashboard_battery_value.text = if (batteryCharging)
                getString(R.string.dashboard_battery_level_charging, batteryLevel)
            else
                getString(R.string.dashboard_battery_level, batteryLevel)
            dashboard_name_value.text = deviceName

            dashboard_battery_name_feature.visibility = View.VISIBLE
        }
    }

    fun showFirmwareVersionFeature(minor: Int, major: Int, build: Int?) {
        activity?.runOnUiThread {
            view ?: return@runOnUiThread

            dashboard_firmware_version_value.text =
                    "$major.$minor${if (build != null) ".$build" else ""}"

            dashboard_firmware_version_feature.visibility = View.VISIBLE
        }
    }

    fun showFeedbackSoundsDisabledMessage() {
        activity?.runOnUiThread {
            Snackbar.make(view!!, getString(R.string.dashboard_feedback_sounds_disabled), Snackbar.LENGTH_SHORT).show()
        }
    }

    fun showFeedbackSoundsFeature(enabled: Boolean) {
        activity?.runOnUiThread {
            view ?: return@runOnUiThread

            dashboard_feedback_sounds_value.isChecked = enabled
            dashboard_feedback_sounds_feature.visibility = View.VISIBLE
        }
    }

    fun showSpeakerphoneModeFeature(enabled: Boolean) {
        activity?.runOnUiThread {
            view ?: return@runOnUiThread

            dashboard_speakerphone_mode_value.isChecked = enabled
            dashboard_speakerphone_mode_feature.visibility = View.VISIBLE
        }
    }

    fun showBassLevelFeature(level: Int) {
        activity?.runOnUiThread {
            view ?: return@runOnUiThread

            dashboard_bass_level_slider.progress = level
            dashboard_bass_level_feature.visibility = View.VISIBLE
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dashboard_product_audio_button.setOnClickListener { presenter.onPlaySoundPressed() }
        dashboard_speakerphone_mode_value.setOnCheckedChangeListener { _, isChecked -> presenter.onSpeakerphoneModeChanged(isChecked) }
        dashboard_feedback_sounds_value.setOnCheckedChangeListener { _, isChecked -> presenter.onFeedbackSoundsChanged(isChecked) }
        dashboard_name_edit_button.setOnClickListener { presenter.onRenamePressed() }
        dashboard_bass_level_slider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) presenter.onBassLevelChanged(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        presenter.attachView(this)
    }

    fun showRenameAlertDialog(currentName: String) {
        val dialogView = View.inflate(context, R.layout.dialog_rename, null)
        val textEdit = dialogView.findViewById<TextInputEditText>(R.id.rename_input)
        textEdit.text = Editable.Factory.getInstance().newEditable(currentName)

        AlertDialog.Builder(context!!)
                .setCancelable(true)
                .setView(dialogView)
                .setTitle(R.string.dialog_dashboard_rename_title)
                .setMessage(R.string.dialog_dashboard_rename_message)
                .setPositiveButton(R.string.dialog_dashboard_rename_confirm) { _, _ ->
                    presenter.onRenameDialogConfirmed(textEdit.text.toString())
                }.setNeutralButton(R.string.dialog_dashboard_rename_cancel) { _, _ -> }.show()
    }

    override fun onDestroy() {
        super.onDestroy()

        presenter.detachView()

        if (isRemoving) {
            PresenterManager.destroyPresenter(SpeakerPresenter::class.java)
        }
    }
}
