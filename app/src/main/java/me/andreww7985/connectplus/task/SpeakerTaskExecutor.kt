package me.andreww7985.connectplus.task

import android.util.Log
import me.andreww7985.connectplus.speaker.SpeakerModel

object SpeakerTaskExecutor {
    const val TAG = "SpeakerTaskExecutor"
    private var currentTask: ITask? = null

    fun startTask(task: ITask) {
        Log.d(TAG, "startTask task = $task")
        task.execute()
        currentTask = task
    }

    fun onTaskDone() {
        currentTask = null
    }

    fun getCurrentTask() = currentTask

    /* Returns true if packet needs to be processed using BluetoothProtocol */
    fun onPacket(packet: ByteArray) = currentTask?.onPacket() ?: false

    /* Returns true if speaker needs to be processed using SpeakerManager */
    fun onSpeakerFound(speaker: SpeakerModel): Boolean = currentTask?.onSpeakerFound() ?: false
}