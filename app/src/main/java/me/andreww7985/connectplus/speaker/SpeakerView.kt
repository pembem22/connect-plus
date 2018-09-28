package me.andreww7985.connectplus.speaker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.size
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_dashboard.*
import me.andreww7985.connectplus.R
import me.andreww7985.connectplus.feature.view.BaseFeatureView
import me.andreww7985.connectplus.mvp.BaseView
import me.andreww7985.connectplus.mvp.PresenterManager

class SpeakerView : BaseView, Fragment() {
    companion object {
        const val TAG = "SpeakerView"
    }

    private val speakerPresenter = PresenterManager.getPresenter(SpeakerPresenter::class.java) as SpeakerPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    fun setMac(mac: String) {
        mac_value.text = mac
    }

    fun setData(data: String) {
        data_value.text = data
    }

    fun setColor(color: String) {
        color_value.text = color
    }

    fun setModel(model: String) {
        model_value.text = model
    }

    fun setSpeakerImage(drawable: Int) {
        product_image.setImageResource(drawable)
    }

    fun setLogoImage(drawable: Int) {
        product_logo.setImageResource(drawable)
    }

    fun showFeatureView(featureView: BaseFeatureView) {
        dashboard_list.addView(featureView, dashboard_list.size - 1)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        product_audio_button.setOnClickListener { speakerPresenter.onPlaySoundPressed() }
        speakerPresenter.attachView(this)

        /*feedback_switch.setOnCheckedChangeListener { _, isChecked ->
            speaker.audioFeedback = isChecked
        }

        product_audio_button.setOnClickListener {
            val audioFeedback = speaker.audioFeedback
            if (audioFeedback != false)
                BluetoothProtocol.playSound(speaker)
            else
                Snackbar.make(view, getString(R.string.dashboard_feedback_sounds_disabled), Snackbar.LENGTH_SHORT).show()
        }

        name_edit_button.setOnClickListener {
            val dialogView = View.inflate(context, R.layout.dialog_rename, null)
            val textEdit = dialogView.findViewById<TextInputEditText>(R.id.rename_input)
            textEdit.text = Editable.Factory.getInstance().newEditable(speaker.name)

            AlertDialog.Builder(context!!).setCancelable(true).setView(dialogView).setTitle(R.string.dialog_rename_device_title).setPositiveButton(R.string.dialog_rename_device_rename) { _, _ ->
                BluetoothProtocol.renameSpeaker(speaker, textEdit.text.toString())
            }.setMessage(R.string.dialog_rename_device_message).setNeutralButton(R.string.dialog_rename_device_cancel) { _, _ -> }.show()
        }*/
    }

    override fun onDestroy() {
        super.onDestroy()

        speakerPresenter.detachView()

        if (isRemoving)
            PresenterManager.destroyPresenter(SpeakerPresenter::class.java)
    }

    fun update() {
        /*activity!!.runOnUiThread {
            val audioFeedback = speaker.audioFeedback
            card_feedback?.let {
                it.visibility = if (audioFeedback == null) View.GONE else View.VISIBLE
                audioFeedback?.let {
                    feedback_switch.isChecked = audioFeedback
                }
            }

            val batteryCharging = speaker.batteryCharging
            val batteryLevel = speaker.batteryLevel

            val firmware = speaker.firmware
            card_firmware?.let {
                it.visibility = if (firmware == null) View.GONE else View.VISIBLE
                firmware?.let {
                    firmware_value.text = firmware
                }
            }

            val name = speaker.name
            card_info?.let {
                it.visibility = if (name == null) View.GONE else View.VISIBLE
                name?.let {
                    name_value.text = name
                }

                batteryLevel?.let {
                    battery_value.text = if (batteryCharging == true)
                        getString(R.string.dashboard_battery_level_charging, batteryLevel)
                    else
                        getString(R.string.dashboard_battery_level, batteryLevel)
                }
            }
        }*/
    }
}
