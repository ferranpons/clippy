package com.ferranpons.clippylib.backend.converter


import com.ferranpons.clippylib.model.gui.UiAgent
import com.ferranpons.clippylib.model.raw.Agent

interface AgentConverter {
    fun agentToUiAgent(agent: Agent): UiAgent
}
