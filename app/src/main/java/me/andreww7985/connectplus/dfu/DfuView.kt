package me.andreww7985.connectplus.dfu

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_dfu.*
import me.andreww7985.connectplus.App
import me.andreww7985.connectplus.R

class DfuView : Fragment() {
    companion object {
        const val TAG = "DfuView"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dfu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (!App.sharedPreferences.getBoolean("dfu_warning_accepted", false)) {
            AlertDialog.Builder(context!!).setCancelable(false).setTitle(getString(R.string.dialog_dfu_warning_title))
                    .setMessage(getString(R.string.dialog_dfu_warning_text))
                    .setPositiveButton(R.string.dialog_dfu_warning_agree) { _, _ ->
                        App.sharedPreferences.edit {
                            putBoolean("dfu_warning_accepted", true)
                        }
                    }.setNegativeButton(R.string.dialog_dfu_warning_do_not_agree) { _, _ ->
                        activity?.supportFragmentManager?.popBackStack()
                    }.show()
        }

        dfu_file_button.setOnClickListener {
            val intent = Intent()
            intent.type = "*/*"
            intent.action = Intent.ACTION_GET_CONTENT

            startActivityForResult(intent, 1)
        }

        dfu_flash_button.setOnClickListener {
            Log.w(TAG, "dfu_flash_button on click")
            /*if (FragmentController.model!!.dfuState == DfuState.FILE_LOADED) {
                App.logFirebaseEvent("dfu_start")
                FragmentController.model!!.dfu.startFlashing()
            } else {
                App.logFirebaseEvent("dfu_cancel")
                BluetoothProtocol.sendPacket(BluetoothProtocol.makePacket(PacketType.REQ_DFU_CANCEL))
            }*/
        }
    }

    fun update() {
        activity?.runOnUiThread {
            /*val speaker = FragmentController.model!!
            val dfuState = speaker.dfuState

            // DFU Battery warning card
            card_dfu_charge.visibility = if (speaker.batteryCharging == true) View.GONE else View.VISIBLE

            // DFU File card
            dfu_file_text.text = when (dfuState) {
                DfuState.FILE_NOT_SELECTED -> getString(R.string.dfu_file_not_selected)
                DfuState.LOADING_FILE -> getString(R.string.dfu_file_loading)
                DfuState.BAD_FILE -> "File corrupted"
                else -> speaker.dfu.filename
            }
            dfu_file_button.isEnabled = when (dfuState) {
                DfuState.INITIALIZING_FLASHING, DfuState.FLASHING -> false
                else -> true
            }

            // DFU File info card
            if (dfuState >= DfuState.FILE_LOADED) {
                card_dfu_info_size_value.text = speaker.dfu.size.toString()
                card_dfu_info_checksum_value.text = HexHelper.bytesToHex(speaker.dfu.checksum)
                card_dfu_info.visibility = View.VISIBLE
            } else {
                card_dfu_info.visibility = View.GONE
            }

            // DFU Flash card
            card_dfu_flash.visibility = when (dfuState) {
                DfuState.INITIALIZING_FLASHING, DfuState.FLASHING, DfuState.FLASHING_DONE, DfuState.FLASHING_ERROR -> View.VISIBLE
                DfuState.FILE_LOADED -> if (speaker.batteryCharging == true) View.VISIBLE else View.GONE
                else -> View.GONE
            }

            when (dfuState) {
                DfuState.FILE_LOADED -> {
                    dfu_flash_button.isEnabled = true
                    dfu_flash_button.text = getString(R.string.dfu_button_flash)
                }
                DfuState.FLASHING -> {
                    dfu_flash_button.isEnabled = true
                    dfu_flash_button.text = getString(R.string.dfu_button_cancel)
                }
                else -> dfu_flash_button.isEnabled = false
            }

            dfu_flash_progressbar.visibility = when (dfuState) {
                DfuState.FLASHING, DfuState.INITIALIZING_FLASHING -> View.VISIBLE
                else -> View.GONE
            }

            dfu_flash_progressbar.isIndeterminate = dfuState == DfuState.INITIALIZING_FLASHING

            dfu_flash_progress_text.visibility = if (dfuState == DfuState.FLASHING) View.VISIBLE else View.GONE

            when (dfuState) {
                DfuState.FILE_LOADED -> {
                    dfu_flash_text.text = getString(R.string.dfu_flash_ready)
                }
                DfuState.INITIALIZING_FLASHING -> {
                    dfu_flash_text.text = getString(R.string.dfu_flash_initializing)
                }
                DfuState.FLASHING -> {
                    val progress = 100f * speaker.dfu.currentChunk * 104 / speaker.dfu.size
                    dfu_flash_progressbar.progress = progress.toInt()
                    dfu_flash_progress_text.text = String.format("%.2f%%", progress)
                    dfu_flash_text.text = getString(R.string.dfu_flash_flashing)
                }
                DfuState.FLASHING_DONE -> {
                    dfu_flash_text.text = getString(R.string.dfu_flash_done)
                }
                DfuState.FLASHING_ERROR -> {
                    dfu_flash_text.text = getString(R.string.dfu_flash_error)
                }
            }
        }*/
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        /*if (data == null) return
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Thread {
                val speaker = SpeakerManager.mainSpeaker!!
                val intentData = data.data ?: run {
                    Logger.print(TAG, "onActivityResult data.data = null")
                    return@Thread
                }

                speaker.dfuState = DfuState.LOADING_FILE

                val dfu = DfuFirmware(intentData)
                speaker.dfu = dfu
                dfu.check(context!!)
            }.start()
        }*/
    }
}
