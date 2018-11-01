package me.andreww7985.connectplus.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_connect_plus.*
import me.andreww7985.connectplus.R
import me.andreww7985.connectplus.manager.SpeakerManager
import me.andreww7985.connectplus.ui.SpeakersAdapter

class ConnectPlusFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_connect_plus, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        connect_list.layoutManager = LinearLayoutManager(context)
        connect_list.adapter = SpeakersAdapter(context!!, SpeakerManager.speakerList)

        SpeakerManager.linkUpdatedEvent.subscribe {
            this@ConnectPlusFragment.activity?.runOnUiThread {
                connect_list.adapter?.notifyDataSetChanged()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        SpeakerManager.speakerFoundEvent.unsubscribe()
    }
}
