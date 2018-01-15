package com.ferranpons.clippylib.view

import android.graphics.drawable.AnimationDrawable
import android.os.Handler

abstract class CustomAnimationDrawableNew(aniDrawable: AnimationDrawable) : AnimationDrawable() {

    private var mAnimationHandler: Handler? = null

    val totalDuration: Int
        get() {

            var iDuration = 0

            for (i in 0 until this.numberOfFrames) {
                iDuration += this.getDuration(i)
            }

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