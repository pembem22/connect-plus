package me.andreww7985.connectplus.feature

import android.content.Context
import me.andreww7985.connectplus.feature.model.BaseFeatureModel
import me.andreww7985.connectplus.feature.model.BatteryNameFeatureModel
import me.andreww7985.connectplus.feature.presenter.BaseFeaturePresenter
import me.andreww7985.connectplus.feature.presenter.BatteryNameFeaturePresenter
import me.andreww7985.connectplus.feature.view.BatteryNameFeatureView
import me.andreww7985.connectplus.speaker.SpeakerModel

enum class Feature {
    BATTERY_NAME;
    //FEEDBACK_SOUNDS,
    //FIRMWARE_VERSION;

    object Factory {
        fun makeFeatureModel(feature: Feature, speakerModel: SpeakerModel) = when (feature) {
            BATTERY_NAME -> BatteryNameFeatureModel(speakerModel)
        }

        fun makeFeatureView(feature: Feature, context: Context, featurePresenter: BaseFeaturePresenter) = when (feature) {
            BATTERY_NAME -> BatteryNameFeatureView(context, featurePresenter as BatteryNameFeaturePresenter)
        }

        fun makeFeaturePesenter(feature: Feature, featureModel: BaseFeatureModel) = when (feature) {
            BATTERY_NAME -> BatteryNameFeaturePresenter(featureModel as BatteryNameFeatureModel)
        }
    }
}