package me.andreww7985.connectplus.feature

import android.content.Context
import me.andreww7985.connectplus.feature.model.*
import me.andreww7985.connectplus.feature.presenter.*
import me.andreww7985.connectplus.feature.view.BatteryNameFeatureView
import me.andreww7985.connectplus.feature.view.FeedbackSoundsFeatureView
import me.andreww7985.connectplus.feature.view.FirmwareVersionFeatureView
import me.andreww7985.connectplus.feature.view.SpeakerphoneModeFeatureView
import me.andreww7985.connectplus.speaker.SpeakerModel

enum class Feature {
    BATTERY_NAME,
    FEEDBACK_SOUNDS,
    FIRMWARE_VERSION,
    SPEAKERPHONE_MODE;

    object Factory {
        fun makeFeatureModel(feature: Feature, speakerModel: SpeakerModel) = when (feature) {
            BATTERY_NAME -> BatteryNameFeatureModel(speakerModel)
            FEEDBACK_SOUNDS -> FeedbackSoundsFeatureModel(speakerModel)
            FIRMWARE_VERSION -> FirmwareVersionFeatureModel(speakerModel)
            SPEAKERPHONE_MODE -> SpeakerphoneModeFeatureModel(speakerModel)
        }

        fun makeFeatureView(feature: Feature, context: Context, featurePresenter: BaseFeaturePresenter) = when (feature) {
            BATTERY_NAME -> BatteryNameFeatureView(context, featurePresenter as BatteryNameFeaturePresenter)
            FEEDBACK_SOUNDS -> FeedbackSoundsFeatureView(context, featurePresenter as FeedbackSoundsFeaturePresenter)
            FIRMWARE_VERSION -> FirmwareVersionFeatureView(context, featurePresenter as FirmwareVersionFeaturePresenter)
            SPEAKERPHONE_MODE -> SpeakerphoneModeFeatureView(context, featurePresenter as SpeakerphoneModeFeaturePresenter)
        }

        fun makeFeaturePresenter(feature: Feature, featureModel: BaseFeatureModel) = when (feature) {
            BATTERY_NAME -> BatteryNameFeaturePresenter(featureModel as BatteryNameFeatureModel)
            FEEDBACK_SOUNDS -> FeedbackSoundsFeaturePresenter(featureModel as FeedbackSoundsFeatureModel)
            FIRMWARE_VERSION -> FirmwareVersionFeaturePresenter(featureModel as FirmwareVersionFeatureModel)
            SPEAKERPHONE_MODE -> SpeakerphoneModeFeaturePresenter(featureModel as SpeakerphoneModeFeatureModel)
        }
    }
}