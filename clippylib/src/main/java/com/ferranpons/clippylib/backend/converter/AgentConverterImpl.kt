package com.ferranpons.clippylib.backend.converter

import java.util.ArrayList
import java.util.HashMap

import com.ferranpons.clippylib.backend.converter.mapping.AgentMapping
import com.ferranpons.clippylib.model.AgentType
import com.ferranpons.clippylib.model.gui.UiAgent
import com.ferranpons.clippylib.model.gui.UiAnimation
import com.ferranpons.clippylib.model.gui.UiBranch
import com.ferranpons.clippylib.model.gui.UiFrame
import com.ferranpons.clippylib.model.raw.Agent
import com.ferranpons.clippylib.model.raw.Animation
import com.ferranpons.clippylib.model.raw.Branching

class AgentConverterImpl(agentType: AgentType) : AgentConverter {

    private val agentMapping: AgentMapping = agentType.agentMapping

    override fun agentToUiAgent(agent: Agent): UiAgent {
        return UiAgent(
                agent.overlayCount,
                agent.frameSize[0],
                agent.frameSize[1],
                convertAnimationMap(agent.animations, agent),
                agentMapping.firstFrameId
        )
    }


    private fun convertAnimationMap(animationMap: Map<String, Animation>, agent: Agent): Map<String, UiAnimation> {
        val uiAnimationMap = HashMap<String, UiAnimation>()

        for (key in animationMap.keys) {
            val animation = animationMap[key]
            uiAnimationMap[key] = convertAnimation(animation!!, agent)
        }

        return uiAnimationMap
    }

    private fun convertAnimation(animation: Animation, agent: Agent): UiAnimation {

        val frames = animation.frames
        val uiAnimationList = frames.map {
            UiFrame(
                    it.duration,
                    convertImageListToId(it.images, agent),
                    it.exitBranch,
                    convertBranchingToUiBranches(it.branching),
                    if (it.sound != null) agentMapping.soundMapping[it.sound - 1] else null
            )
        }

        return UiAnimation(uiAnimationList)
    }


    private fun convertBranchingToUiBranches(branching: Branching?): List<UiBranch>? {
        if (branching == null) {
            return null
        }

        val branches = branching.branches

        return branches.map { UiBranch(it.frameIndex, it.weight) }
    }

    private fun convertImageListToId(lists: List<List<Int>>?, agent: Agent): List<Int> {
        if (lists == null) {
            val emptyFrame = ArrayList<Int>()
            for (i in 0 until agent.overlayCount) {
                emptyFrame.add(agentMapping.emptyFrameId)
            }
            return emptyFrame
        }

        val result = lists.mapTo(ArrayList()) {
            imagePositionToId(
                    getFrameWidth(agent),
                    getFrameHeight(agent),
                    agentMapping.numberColumns,
                    agentMapping.numberRows,
                    it[0],
                    it[1]
            )!!
        }

        while (result.size < agent.overlayCount) {
            result.add(agentMapping.emptyFrameId)
        }

        return result
    }

    private fun imagePositionToId(frameWidth: Int, frameHeight: Int, numberColumns: Int, numberRows: Int, posX: Int, posY: Int): Int? {
        val logicColumn = posX / frameWidth
        val logicRow = posY / frameHeight
        val posInMapping = numberColumns * logicRow + logicColumn

        return agentMapping.mapping[posInMapping]
    }


    private fun getFrameWidth(agent: Agent): Int {
        return agent.frameSize[0]
    }

    private fun getFrameHeight(agent: Agent): Int {
        return agent.frameSize[1]
    }
}
