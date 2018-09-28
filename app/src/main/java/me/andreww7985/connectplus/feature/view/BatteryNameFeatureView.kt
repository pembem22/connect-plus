package me.andreww7985.connectplus.feature.view

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import androidx.appcompat.app.AlertDialog
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.battery_name_feature.view.*
import me.andreww7985.connectplus.R
import me.andreww7985.connectplus.feature.presenter.BatteryNameFeaturePresenter

@SuppressLint("ViewConstructor")
class BatteryNameFeatureView(context: Context, private val presenter: BatteryNameFeaturePresenter) : BaseFeatureView(context) {
    init {
        addView(inflate(context, R.layout.battery_name_feature, null))

        dashboard_name_edit.setOnClickListener {
            presenter.onEditNamePressed()
        }

        presenter.attachView(this)
    }

    fun setName(name: String) {
        dashboard_name_value.text = name
    }

    fun showRenameDialog(currentName: String) {
        val dialogView = inflate(context, R.layout.dialog_rename, null)
        val textEdit = dialogView.findViewById<TextInputEditText>(R.id.rename_input)
        textEdit.text = Editable.Factory.getInstance().newEditable(currentName)

        AlertDialog.Builder(context!!)
                .setCancelable(true)
                .setView(dialogView)
                .setTitle(R.string.dialog_rename_device_title)
                .setMessage(R.string.dialog_rename_device_message)
                .setPositiveButton(R.string.dialog_rename_device_rename) { _, _ ->
                    presenter.onEditDialogRenamePressed(textEdit.text.toString())
                }.setNeutralButton(R.string.dialog_rename_device_cancel) { _, _ -> }
                .show()
    }

    fun setBatteryState(level: Int, charging: Boolean) {
        dashboard_battery_value.text = if (charging == true)
            context.getString(R.string.dashboard_battery_level_charging, level)
        else
            context.getString(R.string.dashboard_battery_level, level)
    }
}