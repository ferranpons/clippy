package com.ferranpons.clippylib.model.raw

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

class Branch @JsonCreator
constructor(
        @param:JsonProperty("frameIndex") val frameIndex: Int,
        @param:JsonProperty("weight") val weight: Int)
