package me.martichou.be.grateful.utilities

import java.util.concurrent.Executors

/**
 * Utility method to run blocks on a dedicated background thread, used for io/database work.
 */
fun runOnIoThread(f: () -> Unit) {
    Executors.newSingleThreadExecutor().execute(f)
}