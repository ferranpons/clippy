package com.ferranpons.clippylib

import java.util.Random

enum class AnimationPause private constructor(val settingsValue: String, val basePause: Int) {

    Seldom("s", 30000),
    Normal("n", 5000),
    Often("o", 1000);

    private val random: Random

    val randomPause: Int
        get() = random.nextInt(basePause) + basePause


    init {
        this.random = Random()
    }

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
