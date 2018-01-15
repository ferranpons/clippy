package com.ferranpons.clippylib.backend.source

import android.content.Context

import com.ferranpons.clippylib.utils.O
import com.ferranpons.clippylib.model.AgentType
import com.ferranpons.clippylib.model.raw.Agent

interface AgentSource {
    fun getAgent(context: Context, agentType: AgentType): O<Agent>
}
