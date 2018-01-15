package com.ferranpons.clippylib.model

import com.ferranpons.clippylib.backend.converter.mapping.*

enum class AgentType private constructor(val assetName: String, val agentMapping: AgentMapping, val isBroken: Boolean) {

    CLIPPY("agent_clippy.json", ClippyMapping(), false);


    companion object {

        val KEY = "extra_agent_type"
    }
}
