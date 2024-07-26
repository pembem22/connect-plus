package me.andreww7985.connectplus.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import me.andreww7985.connectplus.R
import me.andreww7985.connectplus.databinding.ItemSpeakerBinding
import me.andreww7985.connectplus.protocol.AudioChannel
import me.andreww7985.connectplus.speaker.Feature
import me.andreww7985.connectplus.speaker.SpeakerModel

class SpeakersAdapter(private val speakers: List<SpeakerModel>) : RecyclerView.Adapter<ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_speaker, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val speaker = speakers[position]
        val context = holder.itemView.context

        val speakerDrawableId = context.resources.getIdentifier(
            String.format(
                "img_%s_%s", speaker.hardware.model.modelName,
                speaker.hardware.color.name.lowercase()
            ), "drawable", context.packageName
        )
        if (speakerDrawableId != 0) holder.binding.speakerImage.setImageResource(speakerDrawableId)

        holder.binding.speakerSoundButton.setOnClickListener { speaker.playSound() }

        holder.binding.speakerNameValue.text = speaker.getFeatureOrNull<Feature.BatteryName>()?.deviceName ?: "Unknown"

        holder.binding.speakerChannelButtons.check(when (speaker.audioChannel) {
            AudioChannel.LEFT -> R.id.speaker_left_button
            AudioChannel.RIGHT -> R.id.speaker_right_button
            AudioChannel.STEREO -> R.id.speaker_stereo_button
        })

        holder.binding.speakerChannelButtons.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                speaker.updateAudioChannel(when (checkedId) {
                    R.id.speaker_left_button -> AudioChannel.LEFT
                    R.id.speaker_right_button -> AudioChannel.RIGHT
                    R.id.speaker_stereo_button -> AudioChannel.STEREO
                    else -> throw IllegalArgumentException("Illegal button ID on channel selector")
                })
            }
        }
    }

    override fun getItemCount() = speakers.size
}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val binding = ItemSpeakerBinding.bind(view)
}