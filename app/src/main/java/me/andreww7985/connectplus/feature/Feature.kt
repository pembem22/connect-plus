package me.andreww7985.connectplus.feature

import android.content.Context
import me.andreww7985.connectplus.feature.model.BaseFeatureModel
import me.andreww7985.connectplus.feature.model.BatteryNameFeatureModel
import me.andreww7985.connectplus.feature.model.FeedbackSoundsFeatureModel
import me.andreww7985.connectplus.feature.model.FirmwareVersionFeatureModel
import me.andreww7985.connectplus.feature.presenter.BaseFeaturePresenter
import me.andreww7985.connectplus.feature.presenter.BatteryNameFeaturePresenter
import me.andreww7985.connectplus.feature.presenter.FeedbackSoundsFeaturePresenter
import me.andreww7985.connectplus.feature.presenter.FirmwareVersionFeaturePresenter
import me.andreww7985.connectplus.feature.view.BatteryNameFeatureView
import me.andreww7985.connectplus.feature.view.FeedbackSoundsFeatureView
import me.andreww7985.connectplus.feature.view.FirmwareVersionFeatureView
import me.andreww7985.connectplus.speaker.SpeakerModel

enum class Feature {
    BATTERY_NAME,
    FEEDBACK_SOUNDS,
    FIRMWARE_VERSION;

    object Factory {
        fun makeFeatureModel(feature: Feature, speakerModel: SpeakerModel) = when (feature) {
            BATTERY_NAME -> BatteryNameFeatureModel(speakerModel)
            FEEDBACK_SOUNDS -> FeedbackSoundsFeatureModel(speakerModel)
            FIRMWARE_VERSION -> FirmwareVersionFeatureModel(speakerModel)
        }

        fun makeFeatureView(feature: Feature, context: Context, featurePresenter: BaseFeaturePresenter) = when (feature) {
            BATTERY_NAME -> BatteryNameFeatureView(context, featurePresenter as BatteryNameFeaturePresenter)
            FEEDBACK_SOUNDS -> FeedbackSoundsFeatureView(context, featurePresenter as FeedbackSoundsFeaturePresenter)
            FIRMWARE_VERSION -> FirmwareVersionFeatureView(context, featurePresenter as FirmwareVersionFeaturePresenter)
        }

        fun makeFeaturePresenter(feature: Feature, featureModel: BaseFeatureModel) = when (feature) {
            BATTERY_NAME -> BatteryNameFeaturePresenter(featureModel as BatteryNameFeatureModel)
            FEEDBACK_SOUNDS -> FeedbackSoundsFeaturePresenter(featureModel as FeedbackSoundsFeatureModel)
            FIRMWARE_VERSION -> FirmwareVersionFeaturePresenter(featureModel as FirmwareVersionFeatureModel)
        }
    }
}