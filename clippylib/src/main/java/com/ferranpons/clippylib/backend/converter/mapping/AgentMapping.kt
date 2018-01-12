package com.ferranpons.clippylib.backend.converter.mapping

interface AgentMapping {

    val mapping: IntArray
    val soundMapping: IntArray
    val numberRows: Int
    val numberColumns: Int
    val emptyFrameId: Int
    val firstFrameId: Int

}
