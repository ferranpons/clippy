package com.ferranpons.clippylib.backend

import android.content.Context
import com.ferranpons.clippylib.backend.converter.AgentConverterImpl
import com.ferranpons.clippylib.backend.source.AgentSource
import com.ferranpons.clippylib.model.AgentType
import com.ferranpons.clippylib.model.gui.UiAgent
import com.ferranpons.clippylib.utils.O

class AgentServiceImpl(private val agentSource: AgentSource) : AgentService {

    override fun getUiAgent(context: Context, agentType: AgentType): O<UiAgent> {
        val agent = agentSource.getAgent(context, agentType)
        val agentConverter = AgentConverterImpl(agentType)

        return if (agent.isSuccess) {
            O(agentConverter.agentToUiAgent(agent.data!!))
        } else {
            O(agent.error)
        }
    }


}
