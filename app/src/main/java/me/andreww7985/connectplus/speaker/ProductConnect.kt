package me.andreww7985.connectplus.speaker

import me.andreww7985.connectplus.R

enum class ProductConnect(val nameId: Int, val iconId: Int) {
    CONNECT(R.string.menu_connect, R.drawable.ic_connect),
    CONNECT_PLUS(R.string.menu_connect_plus, R.drawable.ic_connect_plus),
    PARTYBOOST(R.string.menu_partyboost, R.drawable.ic_partyboost);

    companion object {
        fun from(productModel: ProductModel) = when (productModel) {
            ProductModel.FLIP3, ProductModel.PULSE2, ProductModel.XTREME, ProductModel.UNKNOWN -> CONNECT

            ProductModel.CHARGE3, ProductModel.CHARGE4, ProductModel.FLIP4, ProductModel.BOOMBOX, ProductModel.XTREME2, ProductModel.PULSE3,
            ProductModel.CHARGE3_QCC, ProductModel.CHARGE4_QCC, ProductModel.FLIP4_QCC, ProductModel.BOOMBOX_QCC, ProductModel.XTREME2_QCC,
            ProductModel.PULSE3_QCC -> CONNECT_PLUS

            ProductModel.FLIP5, ProductModel.PULSE4, ProductModel.BOOMBOX2 -> PARTYBOOST
        }
    }
}