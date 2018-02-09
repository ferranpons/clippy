package com.ferranpons.clippylib.view

import android.content.Context
import android.graphics.PixelFormat
import android.os.*
import android.view.GestureDetector
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout

import timber.log.Timber
import android.os.Build


class FloatingView(context: Context) : FrameLayout(context), View.OnTouchListener {

    private val gestureDetector: GestureDetector
    private val layoutParams: WindowManager.LayoutParams
    private val windowManager: WindowManager

    private var floatingViewCLickListener: FloatingViewCLickListener? = null

    private var childView: View? = null

    private var xDelta: Int = 0
    private var yDelta: Int = 0

    private var killed = false

    init {
        this.gestureDetector = GestureDetector(context, GestureListener())
        this.windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val layoutFlag: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }

        this.layoutParams = WindowManager.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                layoutFlag,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT)

        this.layoutParams.gravity = Gravity.CENTER

        this.setOnTouchListener(this)

        this.windowManager.addView(this, layoutParams)
    }

    fun kill() {
        isAlive()
        this.killed = true
        this.windowManager.removeViewImmediate(this)
        this.floatingViewCLickListener = null
        Timber.d("Somebody killed me")
    }

    override fun addView(v: View) {
        isAlive()
        Timber.d("Add View: %s", v)
        this.removeAllViews()
        this.childView = v

        super.addView(v)
    }

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        val gestureDetected = gestureDetector.onTouchEvent(motionEvent)

        val x = motionEvent.rawX.toInt()
        val y = motionEvent.rawY.toInt()

        var motionDetected = false
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                val layoutParams = view.layoutParams as WindowManager.LayoutParams
                xDelta = x - layoutParams.x
                yDelta = y - layoutParams.y
                motionDetected = true
            }
            MotionEvent.ACTION_MOVE -> {
                updateLocation(x - xDelta, y - yDelta)
                motionDetected = true
            }
        }

        return gestureDetected || motionDetected
    }

    fun setFloatingViewCLickListener(floatingViewCLickListener: FloatingViewCLickListener) {
        isAlive()
        this.floatingViewCLickListener = floatingViewCLickListener
    }

    private fun updateLocation(x: Int, y: Int) {
        layoutParams.x = x
        layoutParams.y = y
        windowManager.updateViewLayout(this, layoutParams)
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            Timber.d("Single tap detected")
            if (this@FloatingView.floatingViewCLickListener != null) {
                Handler().post { this@FloatingView.floatingViewCLickListener!!.onSingleTap(this@FloatingView) }
            }
            return super.onSingleTapConfirmed(e)
        }

        override fun onLongPress(e: MotionEvent) {
            Timber.d("Long press detected")
            if (this@FloatingView.floatingViewCLickListener != null) {
                Handler().post { this@FloatingView.floatingViewCLickListener!!.onLongPress(this@FloatingView) }
            }
            super.onLongPress(e)
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            Timber.d("Double tap detected")
            if (this@FloatingView.floatingViewCLickListener != null) {
                Handler().post { this@FloatingView.floatingViewCLickListener!!.onDoubleTap(this@FloatingView) }
            }
            return super.onDoubleTap(e)
        }
    }

    private fun isAlive() {
        if (killed) {
            Timber.e("View is dead, leave it alone!")
            //throw RuntimeException("Floatingview is dead x.x")
        }
    }


    interface FloatingViewCLickListener {
        fun onSingleTap(v: View)
        fun onDoubleTap(v: View)
        fun onLongPress(v: View)
    }

}
