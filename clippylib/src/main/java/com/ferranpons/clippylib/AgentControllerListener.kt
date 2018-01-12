package com.ferranpons.clippylib

interface AgentControllerListener {
    fun volumeChanged(mute: Boolean)
    fun stateChanged(started: Boolean)
}
