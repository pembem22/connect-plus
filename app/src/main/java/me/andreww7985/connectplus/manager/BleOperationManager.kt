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
            /* TODO: Use removeIf when minimum API is 24 */
            operationsQueue
                    .filter { currentOperation -> currentOperation.bleConnection == operation.bleConnection }
                    .forEach { currentOperation -> operationsQueue.remove(currentOperation) }
            Timber.d("removed previous operation")
        }

        if (currentOperation == null) {
            currentOperation = operationsQueue.poll()
            currentOperation?.perform()
        }
    }

    @Synchronized
    fun operationComplete() {
        Timber.d("operationComplete")
        currentOperation ?: Timber.e("operationComplete called when currentOperation is null")

        currentOperation = null

        if (operationsQueue.peek() != null) {
            currentOperation = operationsQueue.poll()
            currentOperation!!.perform()
        }
    }
}