package com.ferranpons.clippylib.model.raw

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

class Branching @JsonCreator
constructor(@param:JsonProperty("branches") val branches: List<Branch>)
