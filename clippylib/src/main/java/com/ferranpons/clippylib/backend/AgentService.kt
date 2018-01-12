package com.ferranpons.clippylib.backend

import android.content.Context

import com.ferranpons.clippylib.model.AgentType
import com.ferranpons.clippylib.model.gui.UiAgent
import com.ferranpons.clippylib.utils.O

interface AgentService {
    fun getUiAgent(context: Context, agentType: AgentType): O<UiAgent>
}
