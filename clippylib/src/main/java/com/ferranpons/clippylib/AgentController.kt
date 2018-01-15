package com.ferranpons.clippylib

import com.ferranpons.clippylib.model.AgentType
import java.util.concurrent.atomic.AtomicBoolean

internal interface AgentController {
    var isKilled: Boolean
    val isRunning: Boolean
    val isMute: Boolean
    val agentType: AgentType
    var isInitialized: Boolean
    fun start(user: Boolean)
    fun stop(user: Boolean)
    fun kill()
    fun mute()
    fun unMute()
    fun setAgentControllerListener(agentControllerListener: AgentControllerListener)
}

