package me.andreww7985.connectplus.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import me.andreww7985.connectplus.R

class ConnectPlusFragment : Fragment() {
    companion object {
        private const val TAG = "ConnectPlusFragment"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_connect_plus, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        /*val context = context!!
        list_left.adapter = SpeakersAdapter(context, SpeakerManager.leftSpeakers)
        list_stereo.adapter = SpeakersAdapter(context, SpeakerManager.stereoSpeakers)
        list_right.adapter = SpeakersAdapter(context, SpeakerManager.rightSpeakers)*/
    }
}
