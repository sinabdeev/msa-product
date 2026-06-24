package com.example.msaproductviewmobile.util

import com.jakewharton.timber Timber

/**
 * Utility class for initializing and managing Timber logging.
 */
object Logger {

    fun init() {
        Timber.plant(object : Timber.DebugTree() {
            override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                if (priority == Log.ERROR || priority == Log.ASSERT) {
                    t?.let {
                        Timber.w(it, message)
                        return
                    }
                }
                Timber.log(priority, tag, message)
            }
        })
    }

    fun initRelease() {
        // In release mode, no trees are planted by default.
        // Add a release tree (e.g., Crashlytics) as needed.
        Timber.d("Release logging initialized")
    }
}
