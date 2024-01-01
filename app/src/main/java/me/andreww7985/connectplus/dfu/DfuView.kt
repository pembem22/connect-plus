package me.andreww7985.connectplus.dfu

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import me.andreww7985.connectplus.App
import me.andreww7985.connectplus.R
import me.andreww7985.connectplus.databinding.FragmentDfuBinding
import me.andreww7985.connectplus.helpers.HexHelper.toHexString
import me.andreww7985.connectplus.helpers.UIHelper
import me.andreww7985.connectplus.manager.PresenterManager
import me.andreww7985.connectplus.mvp.BaseView

class DfuView : Fragment(), BaseView {
    private var _binding: FragmentDfuBinding? = null
    private val binding get() = _binding!!


    private val presenter = PresenterManager.getPresenter(DfuPresenter::class.java) as DfuPresenter

    fun showFileBrowserError() {
        lifecycleScope.launch {
            UIHelper.showToast(this@DfuView.requireView(), getString(R.string.dfu_error_file_manager), Toast.LENGTH_LONG)
        }
    }

    fun showWrongFile() {
        lifecycleScope.launch {
            UIHelper.showToast(this@DfuView.requireView(), getString(R.string.dfu_error_wrong_file), Toast.LENGTH_LONG)
        }
    }

    fun updateUi(dfu: DfuModel, isSpeakerCharging: Boolean) {
        lifecycleScope.launch {
            binding.dfuBatteryWarningTitle.text =
                if (!isSpeakerCharging)
                    getString(R.string.dfu_connect_charger_title)
                else
                    getString(R.string.dfu_connect_charger_title_connected)

            when (dfu.state) {
                DfuModel.State.FILE_NOT_SELECTED -> {
                    binding.dfuFileSelectorCard.visibility = View.VISIBLE
                    binding.dfuFileSelectorText.text = getString(R.string.dfu_state_file_not_selected)
                    binding.dfuFileSelectorButton.isEnabled = true

                    binding.dfuFileInfoCard.visibility = View.GONE

                    binding.dfuFlashCard.visibility = View.GONE
                }

                DfuModel.State.LOADING_FILE -> {
                    binding.dfuFileSelectorCard.visibility = View.VISIBLE
                    binding.dfuFileSelectorText.text = getString(R.string.dfu_state_loading_file)
                    binding.dfuFileSelectorButton.isEnabled = false

                    binding.dfuFileInfoCard.visibility = View.GONE

                    binding.dfuFlashCard.visibility = View.GONE
                }

                DfuModel.State.READY -> {
                    binding.dfuFileSelectorText.text = dfu.filename!!
                    binding.dfuFileSelectorButton.isEnabled = true

                    binding.dfuFileInfoCard.visibility = View.VISIBLE
                    binding.dfuFileInfoSizeValue.text = dfu.fileSize.toString()
                    binding.dfuFileInfoChecksumValue.text = dfu.checksum!!.toHexString()

                    binding.dfuFlashCard.visibility = if (isSpeakerCharging) View.VISIBLE else View.GONE
                    if (isSpeakerCharging) {
                        binding.dfuFlashCard.visibility = View.VISIBLE

                        binding.dfuFlashProgressbar.visibility = View.GONE

                        binding.dfuFlashButton.isEnabled = true
                        binding.dfuFlashButton.text = getString(R.string.dfu_button_flash)

                        binding.dfuFlashText.text = getString(R.string.dfu_state_ready)

                        binding.dfuFlashButton.setOnClickListener {
                            presenter.startDfu()
                        }
                    }
                }

                DfuModel.State.INITIALIZING_DFU -> {
                    binding.dfuFileSelectorText.text = dfu.filename!!
                    binding.dfuFileSelectorButton.isEnabled = false

                    binding.dfuFileInfoCard.visibility = View.VISIBLE
                    binding.dfuFileInfoSizeValue.text = dfu.fileSize.toString()
                    binding.dfuFileInfoChecksumValue.text = dfu.checksum!!.toHexString()

                    binding.dfuFlashCard.visibility = View.VISIBLE

                    binding.dfuFlashProgressbar.isIndeterminate = true
                    binding.dfuFlashProgressbar.visibility = View.VISIBLE

                    binding.dfuFlashButton.isEnabled = false
                    binding.dfuFlashButton.text = getString(R.string.dfu_button_flash)

                    binding.dfuFlashText.text = getString(R.string.dfu_state_initializing_dfu)
                }

                DfuModel.State.FLASHING_DFU -> {
                    binding.dfuFileSelectorText.text = dfu.filename!!
                    binding.dfuFileSelectorButton.isEnabled = false

                    binding.dfuFileInfoCard.visibility = View.VISIBLE
                    binding.dfuFileInfoSizeValue.text = dfu.fileSize.toString()
                    binding.dfuFileInfoChecksumValue.text = dfu.checksum!!.toHexString()

                    binding.dfuFlashCard.visibility = View.VISIBLE

                    val progress = dfu.getProgress()
                    binding.dfuFlashProgressbar.visibility = View.VISIBLE
                    binding.dfuFlashProgressbar.isIndeterminate = false
                    binding.dfuFlashProgressbar.progress = (progress * 1e5f).toInt()

                    binding.dfuFlashButton.isEnabled = true
                    binding.dfuFlashButton.text = getString(R.string.dfu_button_cancel)

                    binding.dfuFlashText.text = getString(R.string.dfu_state_flashing, progress * 100f)

                    binding.dfuFlashButton.setOnClickListener {
                        presenter.cancelDfu()
                    }
                }

                DfuModel.State.WAITING_REBOOT -> {
                    binding.dfuFileSelectorText.text = dfu.filename!!
                    binding.dfuFileSelectorButton.isEnabled = false

                    binding.dfuFileInfoCard.visibility = View.VISIBLE
                    binding.dfuFileInfoSizeValue.text = dfu.fileSize.toString()
                    binding.dfuFileInfoChecksumValue.text = dfu.checksum!!.toHexString()

                    binding.dfuFlashCard.visibility = View.VISIBLE

                    /* Cannot switch to indeterminate mode while the progress indicator is visible. */
                    binding.dfuFlashProgressbar.visibility = View.GONE
                    binding.dfuFlashProgressbar.isIndeterminate = true
                    binding.dfuFlashProgressbar.visibility = View.VISIBLE

                    binding.dfuFlashButton.isEnabled = false
                    binding.dfuFlashButton.text = getString(R.string.dfu_button_cancel)

                    binding.dfuFlashText.text = when (dfu.status) {
                        DfuModel.Status.SUCCESS -> getString(R.string.dfu_state_done)
                        DfuModel.Status.ERROR -> getString(R.string.dfu_state_error)
                        DfuModel.Status.CANCELED -> getString(R.string.dfu_state_canceled)
                    }
                }
            }
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDfuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (!App.sharedPreferences.getBoolean("dfu_warning_accepted", false)) {
            val dialogView = View.inflate(context, R.layout.dialog_dfu_warning, null)
            val checkBox =
                dialogView.findViewById<MaterialCheckBox>(R.id.dfu_warning_dont_show_checkbox)

            MaterialAlertDialogBuilder(
                requireContext(),
                R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered
            ).setCancelable(false)
                .setIcon(R.drawable.ic_warning)
                .setTitle(getString(R.string.dialog_dfu_warning_title))
                .setView(dialogView)
                .setMessage(getString(R.string.dialog_dfu_warning_text))
                .setPositiveButton(R.string.dialog_dfu_warning_agree) { _, _ ->
                    App.sharedPreferences.edit {
                        putBoolean("dfu_warning_accepted", checkBox.isChecked)
                    }
                }.show()
        }

        binding.dfuFileSelectorButton.setOnClickListener {
            val intent = Intent()
            intent.type = "*/*"
            intent.action = Intent.ACTION_GET_CONTENT

            startActivityForResult(intent, 1)
        }

        presenter.attachView(this)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        data ?: return
        lifecycleScope.launch {
            presenter.loadFile(data.data!!)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        presenter.detachView()

        if (isRemoving) {
            PresenterManager.destroyPresenter(DfuPresenter::class.java)
        }
    }
}
