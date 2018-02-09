package com.ferranpons.clippylib

import java.util.Random

enum class AnimationPause(val settingsValue: String, private val basePause: Int) {

    Seldom("s", 30000),
    Normal("n", 5000),
    Often("o", 1000);

    private val random: Random = Random()

    val randomPause: Int
        get() = random.nextInt(basePause) + basePause

    companion object {

        fun getAnimationPauseFromSettings(settingsValue: String): AnimationPause {
            for (animationPause in AnimationPause.values()) {
                if (animationPause.settingsValue == settingsValue) {
                    return animationPause
                }
            }
            return Normal
        }
    }
}
