package com.ferranpons.clippylib.model.raw

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

class Animation @JsonCreator
constructor(
        @param:JsonProperty("frames") val frames: List<Frame>,
        @param:JsonProperty("useExitBranching") val useExitBranching: Boolean?)
