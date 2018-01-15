package com.ferranpons.clippylib

import android.app.Application

import timber.log.Timber

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Global.INSTANCE.init(applicationContext)
        Timber.plant(Global.INSTANCE.logTree)
    }
}
