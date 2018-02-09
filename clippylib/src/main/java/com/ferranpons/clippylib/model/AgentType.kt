package com.ferranpons.clippylib.model

import com.ferranpons.clippylib.backend.converter.mapping.*

enum class AgentType(val assetName: String, val agentMapping: AgentMapping) {

    CLIPPY("agent_clippy.json", ClippyMapping());

    companion object {
        const val KEY = "extra_agent_type"
    }
}
