package me.andreww7985.connectplus.ui.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_connect_plus.*
import me.andreww7985.connectplus.R
import me.andreww7985.connectplus.speakers.SpeakerManager
import me.andreww7985.connectplus.ui.SpeakersAdapter

class ConnectPlusFragment : Fragment() {
    companion object {
        private const val TAG = "ConnectPlusFragment"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_connect_plus, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val context = context!!
        list_left.adapter = SpeakersAdapter(context, SpeakerManager.leftSpeakers)
        list_stereo.adapter = SpeakersAdapter(context, SpeakerManager.stereoSpeakers)
        list_right.adapter = SpeakersAdapter(context, SpeakerManager.rightSpeakers)
    }
}
