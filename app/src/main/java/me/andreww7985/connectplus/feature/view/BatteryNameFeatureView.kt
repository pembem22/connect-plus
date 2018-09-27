package me.andreww7985.connectplus.feature.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.battery_name_feature.*
import me.andreww7985.connectplus.R
import me.andreww7985.connectplus.helpers.UIHelper
import me.andreww7985.connectplus.ui.fragments.DashboardFragment

class BatteryNameFeatureView(val context: Context, val dashboardFragment: DashboardFragment) : BaseFeatureView() {
    private lateinit var nameLabel: TextView
    private lateinit var batteryLabel: TextView
    private lateinit var editNameButton: ImageButton

    override fun initView() {
        val view = LayoutInflater.from(context).inflate(R.layout.battery_name_feature, null)

        nameLabel = view.findViewById(R.id.dashboard_name_value)
        batteryLabel = view.findViewById(R.id.dashboard_battery_value)
        editNameButton = view.findViewById(R.id.dashboard_name_edit)

        editNameButton.setOnClickListener {
            UIHelper.showToast("TEST")
        }

        dashboardFragment.dashboard_list.addView(view)
    }

    fun setName(name: String) {
        nameLabel.text = name
    }

    fun setBatteryState(level: Int, charging: Boolean) {
        batteryLabel.text = if (charging == true)
            context.getString(R.string.dashboard_battery_level_charging, level)
        else
            context.getString(R.string.dashboard_battery_level, level)
    }
}