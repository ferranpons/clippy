package com.ferranpons.clippylib.view

import android.graphics.drawable.AnimationDrawable
import android.os.Handler

abstract class CustomAnimationDrawableNew(aniDrawable: AnimationDrawable) : AnimationDrawable() {

    private var animationHandler: Handler? = null

    private val totalDuration: Int
        get() {

            return (0 until numberOfFrames).sumBy { getDuration(it) }
        }

    init {
        for (i in 0 until aniDrawable.numberOfFrames) {
            this.addFrame(aniDrawable.getFrame(i), aniDrawable.getDuration(i))
        }
        this.isOneShot = aniDrawable.isOneShot
    }

    override fun start() {
        super.start()
        animationHandler = Handler()
        animationHandler!!.postDelayed({ onAnimationFinish() }, totalDuration.toLong())

    }

    abstract fun onAnimationFinish()
}