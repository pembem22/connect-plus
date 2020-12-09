package me.andreww7985.connectplus.speaker

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.coroutines.launch
import me.andreww7985.connectplus.R
import me.andreww7985.connectplus.manager.PresenterManager
import me.andreww7985.connectplus.mvp.BaseView
import kotlin.math.roundToInt

class SpeakerView : BaseView, Fragment() {
    private val presenter = PresenterManager.getPresenter(SpeakerPresenter::class.java) as SpeakerPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    fun setDeveloperData(mac: String, data: String, color: String, model: String) {
        lifecycleScope.launch {
            mac_value.text = mac
            data_value.text = data
            color_value.text = color
            model_value.text = model
        }
    }

    fun setIsPlaying(isPlaying: Boolean) {
        lifecycleScope.launch {
            dashboard_product_playing.setImageResource(
                    if (isPlaying) R.drawable.ic_play
                    else R.drawable.ic_pause
            )
        }
    }

    fun setSpeakerImages(logoDrawableId: Int, speakerDrawableId: Int) {
        lifecycleScope.launch {
            if (speakerDrawableId != 0) dashboard_product_image.setImageResource(speakerDrawableId)
            if (logoDrawableId != 0) dashboard_product_logo.setImageResource(logoDrawableId)
        }
    }

    fun showBatteryNameFeature(deviceName: String, batteryLevel: Int, batteryCharging: Boolean) {
        lifecycleScope.launch {
            dashboard_battery_value.text = if (batteryCharging)
                getString(R.string.dashboard_battery_level_charging, batteryLevel)
            else
                getString(R.string.dashboard_battery_level, batteryLevel)
            dashboard_name_value.text = deviceName

            dashboard_battery_name_feature.visibility = View.VISIBLE
        }
    }

    @SuppressLint("SetTextI18n")
    fun showFirmwareVersionFeature(minor: Int, major: Int, build: Int?) {
        lifecycleScope.launch {
            dashboard_firmware_version_value.text =
                    "$major.$minor${if (build != null) ".$build" else ""}"

            dashboard_firmware_version_feature.visibility = View.VISIBLE
        }
    }

    fun showFeedbackSoundsDisabledMessage() {
        lifecycleScope.launch {
            Snackbar.make(requireView(), getString(R.string.dashboard_feedback_sounds_disabled), Snackbar.LENGTH_SHORT).show()
        }
    }

    fun showFeedbackSoundsFeature(enabled: Boolean) {
        lifecycleScope.launch {
            dashboard_feedback_sounds_value.isChecked = enabled
            dashboard_feedback_sounds_feature.visibility = View.VISIBLE
        }
    }

    fun showSpeakerphoneModeFeature(enabled: Boolean) {
        lifecycleScope.launch {
            dashboard_speakerphone_mode_value.isChecked = enabled
            dashboard_speakerphone_mode_feature.visibility = View.VISIBLE
        }
    }

    fun showBassLevelFeature(level: Int) {
        lifecycleScope.launch {
            dashboard_bass_level_slider.value = level.toFloat()
            dashboard_bass_level_feature.visibility = View.VISIBLE
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dashboard_product_audio_button.setOnClickListener { presenter.onPlaySoundPressed() }
        dashboard_speakerphone_mode_value.setOnCheckedChangeListener { _, isChecked -> presenter.onSpeakerphoneModeChanged(isChecked) }
        dashboard_feedback_sounds_value.setOnCheckedChangeListener { _, isChecked -> presenter.onFeedbackSoundsChanged(isChecked) }
        dashboard_name_edit_button.setOnClickListener { presenter.onRenamePressed() }
        dashboard_bass_level_slider.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                presenter.onBassLevelChanged(value.roundToInt())
            }
        }

        presenter.attachView(this)
    }

    fun showRenameAlertDialog(currentName: String) {
        val dialogView = View.inflate(context, R.layout.dialog_rename, null)
        val textEdit = dialogView.findViewById<TextInputEditText>(R.id.rename_input)
        textEdit.text = Editable.Factory.getInstance().newEditable(currentName)

        AlertDialog.Builder(requireContext())
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
