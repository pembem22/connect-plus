package me.andreww7985.connectplus.task

interface ITask {
    fun execute()
    fun onSpeakerFound(): Boolean = true
    fun onPacket(): Boolean = true
    fun done() {
        SpeakerTaskExecutor.onTaskDone()
    }
}