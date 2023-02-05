package me.andreww7985.connectplus.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_connect.*
import kotlinx.coroutines.launch
import me.andreww7985.connectplus.R
import me.andreww7985.connectplus.manager.BleScanManager
import me.andreww7985.connectplus.manager.SpeakerManager
import me.andreww7985.connectplus.mvp.BaseView
import me.andreww7985.connectplus.ui.SpeakersAdapter

class ConnectFragment : Fragment(), BaseView {
    private var linkUpdatedEventListener: (() -> Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_connect, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        connect_list.layoutManager = LinearLayoutManager(context)
        connect_list.adapter = SpeakersAdapter(SpeakerManager.speakerList)

        linkUpdatedEventListener = {
            lifecycleScope.launch {
                connect_list.adapter?.notifyDataSetChanged()
            }
        }
        SpeakerManager.linkUpdatedEvent.subscribe(linkUpdatedEventListener!!)

        connect_scan_button.setOnClickListener {
            if (BleScanManager.isScanning) {
                BleScanManager.stopScan()
            } else {
                BleScanManager.startScan()
            }

            updateUi()
        }

        updateUi()
    }

    private fun updateUi() {
        if (BleScanManager.isScanning) {
            connect_scan_progressbar.visibility = View.VISIBLE
            connect_scan_value.text = getString(R.string.connect_searching_on)
            connect_scan_button.text = getString(R.string.connect_button_stop)
        } else {
            connect_scan_progressbar.visibility = View.GONE
            connect_scan_value.text = getString(R.string.connect_searching_off)
            connect_scan_button.text = getString(R.string.connect_button_search)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        linkUpdatedEventListener?.also {
            SpeakerManager.speakerFoundEvent.unsubscribe(it)
        }
    }
}
