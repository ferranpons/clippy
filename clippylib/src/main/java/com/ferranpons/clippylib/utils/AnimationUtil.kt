package com.ferranpons.clippylib.utils

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat

import java.util.ArrayList
import java.util.Random

import com.ferranpons.clippylib.model.gui.UiAnimation
import com.ferranpons.clippylib.model.gui.UiBranch
import com.ferranpons.clippylib.model.gui.UiFrame

object AnimationUtil {

    private val MAX_BRANCHES = 5

    fun getAnimationDrawable(context: Context, uiAnimation: UiAnimation, numberOverlays: Int): AnimationDrawableResult {
        val uiFrames = getUiFrames(uiAnimation.uiFrames)

        val sound = getSound(uiFrames)
        val animationDrawables = getAnimationDrawAbles(context, uiFrames, numberOverlays)

        return AnimationDrawableResult(animationDrawables, sound)
    }


    private fun getUiFrames(frames: List<UiFrame>): List<UiFrame> {
        val random = Random()

        val uiFrames = ArrayList<UiFrame>()

        var branches = 0

        var i = 0
        while (i < frames.size) {
            var frame = frames[i]

            if (branches < MAX_BRANCHES) {
                var rnd = random.nextInt(100)

                if (frame.branches != null) {
                    for (uiBranch in frame.branches!!) {
                        if (rnd <= uiBranch.weight) {
                            i = uiBranch.frameIndex
                            frame = frames[i]
                            branches++
                            break
                        }
                        rnd -= uiBranch.weight
                    }
                }
            }
            uiFrames.add(frame)
            i++
        }

        return uiFrames
    }

    private fun getAnimationDrawAbles(context: Context, frames: List<UiFrame>, numberOverlays: Int): List<AnimationDrawable> {
        val drawables = ArrayList<AnimationDrawable>()
        for (i in 0 until numberOverlays) {
            val animationDrawable = AnimationDrawable()
            animationDrawable.isOneShot = true
            drawables.add(animationDrawable)
        }

        for (uiFrame in frames) {
            val imageIds = uiFrame.imageIds

            for (j in imageIds.indices) {
                val drawable = ContextCompat.getDrawable(context, imageIds[j])
                drawables[j].addFrame(drawable!!, uiFrame.duration)
            }
        }

        return drawables
    }

    private fun getSound(frames: List<UiFrame>): List<SoundMapping> {
        val soundMappings = ArrayList<SoundMapping>()
        var time: Long = 0

        for (frame in frames) {
            if (frame.soundId != null) {
                soundMappings.add(SoundMapping(time, frame.soundId))
            }
            time += frame.duration.toLong()
        }

        return soundMappings
    }


    class SoundMapping internal constructor(val time: Long, val soundId: Int)

    class AnimationDrawableResult internal constructor(val animationDrawables: List<AnimationDrawable>, val soundMappings: List<SoundMapping>)
}
