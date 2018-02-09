package com.ferranpons.clippylib

import android.annotation.SuppressLint
import android.content.Context
import com.ferranpons.clippylib.backend.AgentService
import com.ferranpons.clippylib.backend.AgentServiceImpl
import com.ferranpons.clippylib.backend.source.AgentSource
import com.ferranpons.clippylib.backend.source.AgentSourceImpl
import com.ferranpons.clippylib.storage.AgentStorage
import com.ferranpons.clippylib.storage.SettingsStorage
import timber.log.Timber
import java.lang.RuntimeException


enum class Global {
    @SuppressLint("StaticFieldLeak")
    INSTANCE;

    private lateinit var agentSource: AgentSource
    internal lateinit var agentService: AgentService
    internal lateinit var agentStorage: AgentStorage
    internal lateinit var settingsStorage: SettingsStorage
    internal lateinit var logTree: Timber.Tree
    private lateinit var context: Context
    private var init = false


    private val releaseTree = object : Timber.Tree() {
        override fun log(priority: Int, tag: String, message: String, t: Throwable) {
            //NOP
        }
    }

    fun init(context: Context) {
        this.context = context
        this.agentSource = AgentSourceImpl()
        this.agentService = AgentServiceImpl(agentSource)
        this.agentStorage = AgentStorage(context)
        this.settingsStorage = SettingsStorage(context)

        //TODO
        this.logTree = releaseTree
        init = true
    }

    fun getAgentService(): AgentService? {
        checkInit()
        return agentService
    }

    fun getLogTree(): Timber.Tree? {
        checkInit()
        return logTree
    }

    fun getAgentStorage(): AgentStorage? {
        checkInit()
        return agentStorage
    }

    fun getSettingsStorage(): SettingsStorage? {
        checkInit()
        return settingsStorage
    }

    fun getContext(): Context? {
        checkInit()
        return context
    }

    private fun checkInit() {
        if (!init) {
            throw RuntimeException("Global context not initialized")
        }
    }
}
