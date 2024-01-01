package me.andreww7985.connectplus.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import me.andreww7985.connectplus.R
import me.andreww7985.connectplus.databinding.FragmentConnectBinding
import me.andreww7985.connectplus.manager.BleScanManager
import me.andreww7985.connectplus.manager.SpeakerManager
import me.andreww7985.connectplus.mvp.BaseView
import me.andreww7985.connectplus.ui.SpeakersAdapter

class ConnectFragment : Fragment(), BaseView {
    private var _binding: FragmentConnectBinding? = null
    private val binding get() = _binding


    private var linkUpdatedEventListener: (() -> Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentConnectBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        linkUpdatedEventListener?.also {
            SpeakerManager.speakerFoundEvent.unsubscribe(it)
        }
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding!!.connectList.layoutManager = LinearLayoutManager(context)
        binding!!.connectList.adapter = SpeakersAdapter(SpeakerManager.speakerList)

        linkUpdatedEventListener = {
            lifecycleScope.launch {
                binding?.connectList?.adapter?.notifyDataSetChanged()
            }
        }
        SpeakerManager.linkUpdatedEvent.subscribe(linkUpdatedEventListener!!)

        binding!!.connectScanButton.setOnClickListener {
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
        val binding = binding!!
        if (BleScanManager.isScanning) {
            binding.connectScanProgressbar.visibility = View.VISIBLE
            binding.connectScanValue.text = getString(R.string.connect_searching_on)
            binding.connectScanButton.text = getString(R.string.connect_button_stop)
        } else {
            binding.connectScanProgressbar.visibility = View.GONE
            binding.connectScanValue.text = getString(R.string.connect_searching_off)
            binding.connectScanButton.text = getString(R.string.connect_button_search)
        }
    }
}
