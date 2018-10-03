package me.andreww7985.connectplus.feature.view

import android.annotation.SuppressLint
import android.content.Context
import kotlinx.android.synthetic.main.feature_firmware_version.view.*
import me.andreww7985.connectplus.R
import me.andreww7985.connectplus.feature.presenter.FirmwareVersionFeaturePresenter

@SuppressLint("ViewConstructor")
class FirmwareVersionFeatureView(context: Context, private val presenter: FirmwareVersionFeaturePresenter) : BaseFeatureView(context, presenter) {
    init {
        addView(inflate(context, R.layout.feature_firmware_version, null))

        presenter.attachView(this)
    }

    fun setFirmwareVersionValue(value: String) {
        dashboard_firmware_version_value.text = value
    }
}