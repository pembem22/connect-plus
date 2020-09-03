package me.andreww7985.connectplus.manager

import me.andreww7985.connectplus.manager.bleoperations.BleOperation
import timber.log.Timber
import java.util.concurrent.LinkedBlockingQueue

/* https://www.youtube.com/watch?v=jDykHjn-4Ng */
object BleOperationManager {
    private val operationsQueue = LinkedBlockingQueue<BleOperation>()
    private var currentOperation: BleOperation? = null

    @Synchronized
    fun request(operation: BleOperation, replacePrevious: Boolean = false) {
        Timber.d("request operation ${operation.javaClass.simpleName}")
        operationsQueue.add(operation)

        if (replacePrevious) {
            operationsQueue.removeIf {
                it.bleConnection == operation.bleConnection && it.javaClass == operation.javaClass
            }
        }

        performOperation()
    }

    @Synchronized
    fun operationComplete() {
        Timber.d("operationComplete")
        currentOperation ?: Timber.e("operationComplete called when currentOperation is null")

        currentOperation = null

        performOperation()
    }

    private fun performOperation() {
        if (currentOperation != null) {
            return
        }

        currentOperation = operationsQueue.poll()
        currentOperation?.perform()
    }
}