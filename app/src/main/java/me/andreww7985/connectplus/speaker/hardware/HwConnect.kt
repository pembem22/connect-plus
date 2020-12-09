package me.andreww7985.connectplus.speaker.hardware

import me.andreww7985.connectplus.R

enum class HwConnect(val nameId: Int, val iconId: Int) {
    CONNECT(R.string.menu_connect, R.drawable.ic_connect),
    CONNECT_PLUS(R.string.menu_connect_plus, R.drawable.ic_connect_plus),
    PARTYBOOST(R.string.menu_partyboost, R.drawable.ic_partyboost);

    companion object {
        fun from(hwModel: HwModel) = when (hwModel) {
            HwModel.FLIP3, HwModel.PULSE2, HwModel.XTREME, HwModel.UNKNOWN -> CONNECT

            HwModel.CHARGE3, HwModel.CHARGE4, HwModel.FLIP4, HwModel.BOOMBOX, HwModel.XTREME2, HwModel.PULSE3,
            HwModel.CHARGE3_QCC, HwModel.CHARGE4_QCC, HwModel.FLIP4_QCC, HwModel.BOOMBOX_QCC, HwModel.XTREME2_QCC,
            HwModel.PULSE3_QCC -> CONNECT_PLUS

            HwModel.FLIP5, HwModel.PULSE4, HwModel.BOOMBOX2 -> PARTYBOOST
        }
    }
}