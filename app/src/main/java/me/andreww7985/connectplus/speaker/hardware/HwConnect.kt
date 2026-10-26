package me.andreww7985.connectplus.speaker.hardware

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import me.andreww7985.connectplus.R

enum class HwConnect(@StringRes val nameId: Int, @DrawableRes val iconId: Int) {
    CONNECT(R.string.menu_connect, R.drawable.ic_connect),
    CONNECT_PLUS(R.string.menu_connect_plus, R.drawable.ic_connect_plus),
    PARTYBOOST(R.string.menu_partyboost, R.drawable.ic_partyboost);

    companion object {
        fun from(hwModel: HwModel) = when (hwModel) {
            // ponytail: Boombox 4 links via Auracast, which isn't supported yet; label it plain Connect until it is
            HwModel.FLIP3, HwModel.PULSE2, HwModel.XTREME, HwModel.BOOMBOX4, HwModel.XTREME5, HwModel.UNKNOWN -> CONNECT

            HwModel.CHARGE3, HwModel.CHARGE4, HwModel.FLIP4, HwModel.BOOMBOX, HwModel.XTREME2,
            HwModel.PULSE3 -> CONNECT_PLUS

            HwModel.FLIP5, HwModel.PULSE4, HwModel.BOOMBOX2, HwModel.XTREME3,
            HwModel.CHARGE5, HwModel.FLIP6, HwModel.PULSE5, HwModel.BOOMBOX3 -> PARTYBOOST
        }
    }
}