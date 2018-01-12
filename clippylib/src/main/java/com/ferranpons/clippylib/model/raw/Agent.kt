package com.ferranpons.clippylib.model.raw

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

class Agent @JsonCreator
constructor(
        @param:JsonProperty("overlayCount") val overlayCount: Int,
        @param:JsonProperty("sounds") val sounds: List<Int>,
        @param:JsonProperty("framesize") val frameSize: List<Int>,
        @param:JsonProperty("animations") val animations: Map<String, Animation>)
