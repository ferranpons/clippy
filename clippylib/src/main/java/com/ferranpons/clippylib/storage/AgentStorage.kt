package com.ferranpons.clippylib.storage

import android.content.Context
import android.content.SharedPreferences

import com.ferranpons.clippylib.model.AgentType

class AgentStorage(context: Context) {

    private val sharedPreferences: SharedPreferences

    var isMute: Boolean
        get() = sharedPreferences.getBoolean(AGENT_MUTE, AGENT_MUTE_DEFAULT)
        set(mute) = sharedPreferences.edit()
                .putBoolean(AGENT_MUTE, mute)
                .apply()

    val lastUsedAgent: AgentType
        get() {
            val agentString = sharedPreferences.getString(AGENT_LAST_USED, AGENT_LAST_USED_DEFAUlT.name)
            return AgentType.valueOf(agentString)
        }

    var isAgentStop: Boolean
        get() = sharedPreferences.getBoolean(AGENT_STOP, AGENT_STOP_DEFAULT)
        set(stop) = sharedPreferences.edit()
                .putBoolean(AGENT_STOP, stop)
                .apply()

    init {
        this.sharedPreferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
    }

    fun setAgentLastUsed(agentLastUsed: AgentType) {
        sharedPreferences.edit()
                .putString(AGENT_LAST_USED, agentLastUsed.name)
                .apply()
    }

    companion object {

        private val NAME = "agent_storage"

        private val AGENT_MUTE = "agent_mute"
        private val AGENT_MUTE_DEFAULT = false

        private val AGENT_STOP = "agent_stop"
        private val AGENT_STOP_DEFAULT = false

        private val AGENT_LAST_USED = "agent_lastused"
        private val AGENT_LAST_USED_DEFAUlT = AgentType.CLIPPY
    }

}
