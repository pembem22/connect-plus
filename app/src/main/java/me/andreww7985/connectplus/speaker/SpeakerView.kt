package me.andreww7985.connectplus.speaker

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import me.andreww7985.connectplus.R
import me.andreww7985.connectplus.databinding.FragmentDashboardBinding
import me.andreww7985.connectplus.helpers.UIHelper
import me.andreww7985.connectplus.manager.PresenterManager
import me.andreww7985.connectplus.mvp.BaseView
import kotlin.math.roundToInt

class SpeakerView : BaseView, Fragment() {
    private val presenter =
        PresenterManager.getPresenter(SpeakerPresenter::class.java) as SpeakerPresenter

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setDeveloperData(
        mac: String,
        data: String,
        color: String,
        model: String,
        platform: String
    ) {
        lifecycleScope.launch {
            binding.macValue.text = mac
            binding.dataValue.text = data
            binding.colorValue.text = color
            binding.modelValue.text = model
            binding.platformValue.text = platform
        }
    }

    fun setIsPlaying(isPlaying: Boolean) {
        lifecycleScope.launch {
            binding.dashboardProductPlaying.setImageResource(
                if (isPlaying) R.drawable.ic_play
                else R.drawable.ic_pause
            )
        }
    }

    fun setSpeakerImages(logoDrawableId: Int, speakerDrawableId: Int) {
        lifecycleScope.launch {
            if (speakerDrawableId != 0) binding.dashboardProductImage.setImageResource(speakerDrawableId)
            if (logoDrawableId != 0) binding.dashboardProductLogo.setImageResource(logoDrawableId)
        }
    }

    fun showBatteryNameFeature(deviceName: String, batteryLevel: Int, batteryCharging: Boolean) {
        lifecycleScope.launch {
            binding.dashboardBatteryValue.text = if (batteryCharging)
                getString(R.string.dashboard_battery_level_charging, batteryLevel)
            else
                getString(R.string.dashboard_battery_level, batteryLevel)
            binding.dashboardNameValue.text = deviceName

            binding.dashboardBatteryNameFeature.visibility = View.VISIBLE
        }
    }

    @SuppressLint("SetTextI18n")
    fun showFirmwareVersionFeature(minor: Int, major: Int, build: Int?) {
        lifecycleScope.launch {
            binding.dashboardFirmwareVersionValue.text =
                "$major.$minor${if (build != null) ".$build" else ""}"

            binding.dashboardFirmwareVersionFeature.visibility = View.VISIBLE
        }
    }

    fun showFeedbackSoundsDisabledMessage() {
        lifecycleScope.launch {
            UIHelper.showToast(
                requireView(),
                getString(R.string.dashboard_feedback_sounds_disabled),
            )
        }
    }

    fun showFeedbackSoundsFeature(enabled: Boolean) {
        lifecycleScope.launch {
            binding.dashboardFeedbackSoundsValue.isChecked = enabled
            binding.dashboardFeedbackSoundsFeature.visibility = View.VISIBLE
        }
    }

    fun showSpeakerphoneModeFeature(enabled: Boolean) {
        lifecycleScope.launch {
            binding.dashboardSpeakerphoneModeValue.isChecked = enabled
            binding.dashboardSpeakerphoneModeFeature.visibility = View.VISIBLE
        }
    }

    fun showBassLevelFeature(level: Int) {
        lifecycleScope.launch {
            binding.dashboardBassLevelSlider.value = level.toFloat()
            binding.dashboardBassLevelFeature.visibility = View.VISIBLE
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.dashboardProductAudioButton.setOnClickListener { presenter.onPlaySoundPressed() }
        binding.dashboardSpeakerphoneModeValue.setOnCheckedChangeListener { _, isChecked ->
            presenter.onSpeakerphoneModeChanged(
                isChecked
            )
        }
        binding.dashboardFeedbackSoundsValue.setOnCheckedChangeListener { _, isChecked ->
            presenter.onFeedbackSoundsChanged(
                isChecked
            )
        }
        binding.dashboardNameEditButton.setOnClickListener { presenter.onRenamePressed() }
        binding.dashboardBassLevelSlider.addOnChangeListener { _, value, fromUser ->
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

        MaterialAlertDialogBuilder(requireContext())
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
