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
import com.ferranpons.clippylib.model.raw.Branch
import com.ferranpons.clippylib.model.raw.Branching
import com.ferranpons.clippylib.model.raw.Frame

class AgentConverterImpl(agentType: AgentType) : AgentConverter {

    private val agentMapping: AgentMapping

    init {
        this.agentMapping = agentType.agentMapping
    }

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
            uiAnimationMap.put(key, convertAnimation(animation!!, agent))
        }

        return uiAnimationMap
    }

    private fun convertAnimation(animation: Animation, agent: Agent): UiAnimation {
        val uiAnimationList = ArrayList<UiFrame>()

        val frames = animation.frames
        for (frame in frames) {
            val uiFrame = UiFrame(
                    frame.duration,
                    convertImageListToId(frame.images, agent),
                    frame.exitBranch,
                    convertBranchingToUiBranches(frame.branching)!!,
                    if (frame.sound != null) agentMapping.soundMapping[frame.sound - 1] else null
            )
            uiAnimationList.add(uiFrame)
        }

        return UiAnimation(uiAnimationList)
    }


    private fun convertBranchingToUiBranches(branching: Branching?): List<UiBranch>? {
        if (branching == null) {
            return null
        }

        val uiBranches = ArrayList<UiBranch>()
        val branches = branching.branches
        for (branch in branches) {
            uiBranches.add(UiBranch(branch.frameIndex, branch.weight))
        }

        return uiBranches
    }

    private fun convertImageListToId(lists: List<List<Int>>?, agent: Agent): List<Int> {
        if (lists == null) {
            val emptyFrame = ArrayList<Int>()
            for (i in 0 until agent.overlayCount) {
                emptyFrame.add(agentMapping.emptyFrameId)
            }
            return emptyFrame
        }

        val result = ArrayList<Int>()

        for (imagePos in lists) {
            result.add(
                    imagePositionToId(
                            getFrameWidth(agent),
                            getFrameHeight(agent),
                            agentMapping.numberColumns,
                            agentMapping.numberRows,
                            imagePos[0],
                            imagePos[1]
                    )!!)
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
