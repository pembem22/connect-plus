package me.andreww7985.connectplus.ui.fragments

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import androidx.appcompat.app.AlertDialog
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_dashboard.*
import me.andreww7985.connectplus.R
import me.andreww7985.connectplus.bluetooth.BluetoothProtocol
import me.andreww7985.connectplus.controller.FragmentController
import me.andreww7985.connectplus.helpers.UIHelper
import me.andreww7985.connectplus.speakers.SpeakerManager
import me.andreww7985.connectplus.ui.IUpdatableFragment

class DashboardFragment : IUpdatableFragment() {
    companion object {
        const val TAG = "DashboardFragment"
    }

    private val speaker = SpeakerManager.mainSpeaker!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        FragmentController.view = this

        mac_value.text = speaker.mac
        data_value.text = speaker.scanRecord
        color_value.text = speaker.color.name
        model_value.text = speaker.model.name

        UIHelper.getSpeakerImage(speaker)?.let { product_image.setImageResource(it) }
        UIHelper.getLogoImage(speaker)?.let { product_logo.setImageResource(it) }

        feedback_switch.setOnCheckedChangeListener { _, isChecked ->
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
        }
    }

    override fun update() {
        activity!!.runOnUiThread {
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
        }
    }
}
