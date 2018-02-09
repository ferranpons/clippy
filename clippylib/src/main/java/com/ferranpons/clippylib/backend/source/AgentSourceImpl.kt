package com.ferranpons.clippylib.backend.source

import android.content.Context

import com.fasterxml.jackson.databind.ObjectMapper

import java.io.IOException
import java.io.InputStream

import com.ferranpons.clippylib.utils.O
import com.ferranpons.clippylib.model.AgentType
import com.ferranpons.clippylib.model.raw.Agent

class AgentSourceImpl : AgentSource {

    private val objectMapper: ObjectMapper = ObjectMapper()

    override fun getAgent(context: Context, agentType: AgentType): O<Agent> {
        return try {
            val open = context.assets.open(agentType.assetName)
            O(objectMapper.readValue<Agent>(open, Agent::class.java))
        } catch (e: IOException) {
            O(e.localizedMessage)
        }

    }
}
