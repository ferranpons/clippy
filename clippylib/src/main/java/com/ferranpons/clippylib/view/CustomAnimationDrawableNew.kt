package com.ferranpons.clippylib.view

import android.graphics.drawable.AnimationDrawable
import android.os.Handler

abstract class CustomAnimationDrawableNew(aniDrawable: AnimationDrawable) : AnimationDrawable() {

    private var mAnimationHandler: Handler? = null

    private val totalDuration: Int
        get() {

            val iDuration = (0 until this.numberOfFrames).sumBy { this.getDuration(it) }

            return iDuration
        }

    init {
        for (i in 0 until aniDrawable.numberOfFrames) {
            this.addFrame(aniDrawable.getFrame(i), aniDrawable.getDuration(i))
        }
        this.isOneShot = aniDrawable.isOneShot
    }

    override fun start() {
        super.start()
        mAnimationHandler = Handler()
        mAnimationHandler!!.postDelayed({ onAnimationFinish() }, totalDuration.toLong())

    }

    abstract fun onAnimationFinish()
}