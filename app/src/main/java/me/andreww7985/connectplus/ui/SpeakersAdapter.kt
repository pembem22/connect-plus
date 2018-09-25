package me.andreww7985.connectplus.ui

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import me.andreww7985.connectplus.R
import me.andreww7985.connectplus.helpers.UIHelper
import me.andreww7985.connectplus.speakers.Speaker

class SpeakersAdapter(context: Context, speakers: ArrayList<Speaker>) : ArrayAdapter<Speaker>(context, 0, speakers) {
    companion object {
        const val TAG = "SpeakerAdapter"
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_speaker, parent, false)!!
        }

        val speakerImage = view.findViewById<ImageButton>(R.id.item_speaker_image)
        val speaker = getItem(position)

        if (speaker == null) {
            Log.e(TAG, "getView getItem = null")
            return view
        }

        UIHelper.getSpeakerImage(speaker)?.let { speakerImage.setImageResource(it) }
        return view
    }
}