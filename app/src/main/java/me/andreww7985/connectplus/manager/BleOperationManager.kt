package me.andreww7985.connectplus.manager

import android.util.Log
import me.andreww7985.connectplus.manager.bleoperations.BleOperation
import java.util.*

object BleOperationManager {
    private val TAG = "BleOperationManager"
    private val operationsQueue = LinkedList<BleOperation>()
    private var currentOperation: BleOperation? = null

    @Synchronized
    fun request(operation: BleOperation) {
        Log.d(TAG, "request operation = ${operation.javaClass.simpleName}")
        operationsQueue.add(operation)

        if (currentOperation == null) {
            currentOperation = operationsQueue.poll()
            currentOperation?.perform()
        }
    }

    @Synchronized
    fun operationComplete() {
        Log.d(TAG, "operationComplete")
        currentOperation ?: Log.w(TAG, "operationComplete called when currentOperation is null")

        currentOperation = null

        if (operationsQueue.peek() != null) {
            currentOperation = operationsQueue.poll()
            currentOperation!!.perform()
        }
    }
}