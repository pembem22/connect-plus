package me.andreww7985.connectplus.feature.presenter

import me.andreww7985.connectplus.bluetooth.BluetoothProtocol
import me.andreww7985.connectplus.feature.model.BatteryNameFeatureModel
import me.andreww7985.connectplus.feature.view.BatteryNameFeatureView

class BatteryNameFeaturePresenter(model: BatteryNameFeatureModel) : BaseFeaturePresenter(model) {
    override fun onViewAttached() {
        val model = getModel()
        val view = getView()

        model.setDataChangedListener {
            view.setBatteryState(model.batteryLevel, model.batteryCharging)
            view.setName(model.name)
        }
    }

    fun onEditNamePressed() {
        getView().showRenameDialog(getModel().name)
    }

    fun onEditDialogRenamePressed(newName: String) {
        // TODO: Add name check

        BluetoothProtocol.renameSpeaker(getModel().speaker, newName)
    }

    private fun getView() = view!! as BatteryNameFeatureView
    private fun getModel() = model as BatteryNameFeatureModel
}