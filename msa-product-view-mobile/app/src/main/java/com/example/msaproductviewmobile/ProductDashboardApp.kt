package com.example.msaproductviewmobile

import android.app.Application
import com.example.msaproductviewmobile.util.Logger
import dagger.hilt.android.HiltAndroidApp

/**
 * Hilt-annotated Application class for the product dashboard.
 * Initializes Timber for logging.
 */
@HiltAndroidApp
class ProductDashboardApp : Application() {

    override fun onCreate() {
        super.onCreate()
        initTimber()
    }

    private fun initTimber() {
        if (BuildConfig.DEBUG) {
            Logger.init()
        } else {
            Logger.initRelease()
        }
    }
}
