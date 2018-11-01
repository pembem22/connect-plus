package me.andreww7985.connectplus.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_speaker.view.*
import me.andreww7985.connectplus.R
import me.andreww7985.connectplus.protocol.AudioChannel
import me.andreww7985.connectplus.speaker.Feature
import me.andreww7985.connectplus.speaker.SpeakerModel

class SpeakersAdapter(val context: Context, val speakers: List<SpeakerModel>) : RecyclerView.Adapter<ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_speaker, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val speaker = speakers[position]

        val speakerDrawableId = context.resources.getIdentifier(String.format("img_%s_%s", speaker.model.name.toLowerCase(), speaker.color.name.toLowerCase()), "drawable", context.packageName)
        holder.speakerImage.setImageResource(speakerDrawableId)

        holder.soundButton.setOnClickListener { speaker.playSound() }

        holder.nameLabel.text = (speaker.getFeature(Feature.Type.BATTERY_NAME) as Feature.BatteryName).deviceName

        holder.leftButton.isEnabled = speaker.audioChannel != AudioChannel.LEFT
        holder.leftButton.setOnClickListener { speaker.updateAudioChannel(AudioChannel.LEFT) }

        holder.stereoButton.isEnabled = speaker.audioChannel != AudioChannel.STEREO
        holder.stereoButton.setOnClickListener { speaker.updateAudioChannel(AudioChannel.STEREO) }

        holder.rightButton.isEnabled = speaker.audioChannel != AudioChannel.RIGHT
        holder.rightButton.setOnClickListener { speaker.updateAudioChannel(AudioChannel.RIGHT) }
    }

    override fun getItemCount() = speakers.size
}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val speakerImage = view.speaker_image!!
    val nameLabel = view.speaker_name_value!!
    val leftButton = view.speaker_left_button!!
    val stereoButton = view.speaker_stereo_button!!
    val rightButton = view.speaker_right_button!!
    val soundButton = view.speaker_sound_button!!
}