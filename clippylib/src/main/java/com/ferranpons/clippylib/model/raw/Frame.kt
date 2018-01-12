package com.ferranpons.clippylib.model.raw

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

class Frame @JsonCreator
constructor(
        @param:JsonProperty("duration") val duration: Int,
        @param:JsonProperty("images") val images: List<List<Int>>,
        @param:JsonProperty("sound") val sound: Int?,
        @param:JsonProperty("exitBranch") val exitBranch: Int?,
        @param:JsonProperty("branching") val branching: Branching)
