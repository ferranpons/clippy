package com.ferranpons.clippylib

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.media.MediaPlayer
import android.os.AsyncTask
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar

import java.lang.ref.WeakReference
import java.util.ArrayList
import java.util.Random
import java.util.concurrent.atomic.AtomicBoolean

import com.ferranpons.clippylib.backend.AgentService
import com.ferranpons.clippylib.model.AgentType
import com.ferranpons.clippylib.model.gui.UiAgent
import com.ferranpons.clippylib.model.gui.UiAnimation
import com.ferranpons.clippylib.utils.AnimationUtil
import com.ferranpons.clippylib.utils.O
import com.ferranpons.clippylib.view.CustomAnimationDrawableNew
import com.ferranpons.clippylib.view.FloatingView
import timber.log.Timber

class AgentControllerImpl(override val agentType: AgentType, private val context: Context, private val agentService: AgentService) : AgentController {
    override var isMute: Boolean = false
    private val floatingView: FloatingView

    private var frameLayout: FrameLayout? = null
    private var imageLayer: MutableList<ImageView>? = null
    private var progressBar: ProgressBar? = null

    private val handler: Handler
    private var animationRunnable: AnimationRunnable? = null

    private val animationIsRunning = AtomicBoolean(true)

    override var isKilled = false
    override var isInitialized = false
    private var agentControllerListener: WeakReference<AgentControllerListener>? = null

    override val isRunning: Boolean
        get() {
            isAlive()
            return animationIsRunning.get()
        }

    private val animationDelay: Long
        get() {
            val animationPause = Global.INSTANCE.settingsStorage?.animationPause
            return animationPause?.randomPause?.toLong()!!
        }

    private val loadAgentData = @SuppressLint("StaticFieldLeak")
    object : AsyncTask<AgentType, Void, O<UiAgent>>() {
        override fun doInBackground(vararg agentTypes: AgentType): O<UiAgent> {
            return agentService.getUiAgent(context, agentTypes[0])
        }

        override fun onPostExecute(uiAgentO: O<UiAgent>) {
            Timber.d("Agent data successfully loaded")
            displayAgent(uiAgentO)
        }
    }


    init {
        this.floatingView = FloatingView(context)
        this.handler = Handler()

        initView()
        loadAgentData.execute(agentType)
    }

    override fun kill() {
        isAlive()
        stop(false)
        this.isKilled = true
        this.floatingView.kill()
        Global.INSTANCE.agentStorage?.isAgentStop = false
    }

    override fun stop(user: Boolean) {
        isAlive()

        // if agent stopped by user .. stop agent and save state
        if (user) {
            Global.INSTANCE.agentStorage?.isAgentStop = true
        }

        animationIsRunning.set(false)
        handler.removeCallbacks(animationRunnable)

        for (imageView in imageLayer!!) {
            if (imageView.background != null && imageView.background is AnimationDrawable) {
                (imageView.background as AnimationDrawable).stop()
            }
        }

        resetImages()

        if (agentControllerListener != null && agentControllerListener!!.get() != null) {
            agentControllerListener!!.get()?.stateChanged(false)
        }
    }

    override fun start(user: Boolean) {
        isAlive()

        val stoppedByUser = Global.INSTANCE.agentStorage?.isAgentStop
        // user -> true ... always start
        // user -> false || stoppedByUser -> false

        Timber.d(
                "Start agent: user: %s, stoppedByUser: %s",
                user, stoppedByUser
        )

        if (user || !user && !stoppedByUser!!) {
            Timber.d("Starting agent")
            Global.INSTANCE.agentStorage?.isAgentStop = false

            if (animationIsRunning.compareAndSet(false, true)) {
                handler.post(animationRunnable)
                if (agentControllerListener != null && agentControllerListener!!.get() != null) {
                    agentControllerListener!!.get()?.stateChanged(true)
                }
            }
        } else {
            Timber.d("Not starting agent")
        }
    }

    override fun mute() {
        isMute = true
        if (agentControllerListener != null && agentControllerListener!!.get() != null) {
            agentControllerListener!!.get()?.volumeChanged(true)
        }
    }

    override fun unMute() {
        isMute = false
        if (agentControllerListener != null && agentControllerListener!!.get() != null) {
            agentControllerListener!!.get()?.volumeChanged(false)
        }
    }

    override fun setAgentControllerListener(agentControllerListener: AgentControllerListener) {
        this.agentControllerListener = WeakReference(agentControllerListener)
    }


    private fun initView() {
        isAlive()
        Timber.d("Init view")

        frameLayout = FrameLayout(context)
        progressBar = ProgressBar(context)

        frameLayout!!.addView(progressBar)
        floatingView.addView(frameLayout!!)
    }

    private fun displayAgent(agentOption: O<UiAgent>) {
        isAlive()
        if (agentOption.isSuccess) {
            Timber.d("Display agent: %s", agentType)

            val agent = agentOption.data
            imageLayer = ArrayList(agent!!.overlayCount)

            for (i in 0 until agent.overlayCount) {
                val imageView = ImageView(context)
                imageLayer!!.add(imageView)
                frameLayout!!.addView(imageView)
            }

            progressBar!!.visibility = View.GONE

            imageLayer!![0].setBackgroundDrawable(ContextCompat.getDrawable(context, agent.firstImage))
            isInitialized = true

            Timber.d("Initial start animation")
            startAnimation(agent)

            if (agentControllerListener != null && agentControllerListener!!.get() != null) {
                agentControllerListener!!.get()?.stateChanged(true)
            }

        } else {
            Timber.e("Failure during loading agent data :(")
            //TODO
            throw RuntimeException(agentOption.error)
        }
    }

    private fun startAnimation(agent: UiAgent) {
        isAlive()
        resetImages()

        val animationDelay = animationDelay
        Timber.d("Start animation in %s ms", animationDelay)

        this.animationRunnable = AnimationRunnable(agent)
        handler.postDelayed(this.animationRunnable, animationDelay)
    }

    private fun startSoundHandler(soundMap: List<AnimationUtil.SoundMapping>) {
        isAlive()
        Timber.d("Start sound handler")
        for (soundMapping in soundMap) {
            Handler().postDelayed(SoundRunnable(soundMapping.soundId), soundMapping.time)
        }
    }

    private fun getRandomAnimation(uiAgent: UiAgent): UiAnimation? {
        isAlive()
        val keys = ArrayList(uiAgent.animations.keys)
        val i = Random().nextInt(keys.size - 1) + 1

        Timber.d("Random animation: %s", keys[i])

        return uiAgent.animations[keys[i]]
    }

    private fun isAlive() {
        if (isKilled) {
            Timber.e("Agent is dead, long live the agent ... but this one is really dead")
            throw RuntimeException("FloatingView is dead x.x")
        }
    }

    private fun resetImages() {
        isAlive()
        imageLayer!![0].setBackgroundDrawable(ContextCompat.getDrawable(context, agentType.agentMapping.firstFrameId))

        for (i in 1 until imageLayer!!.size) {
            imageLayer!![i].setBackgroundDrawable(ContextCompat.getDrawable(context, agentType.agentMapping.emptyFrameId))
        }
    }

    private inner class SoundRunnable internal constructor(private val sound: Int) : Runnable {

        override fun run() {
            Timber.d(
                    "Execute sound runnable: Id: %s, isAnimationRunning: %s, isKilled: %s, isMute: %s",
                    sound, animationIsRunning.get(), isKilled, isMute
            )

            if (animationIsRunning.get() && !isKilled && !isMute) {
                val mediaPlayer = MediaPlayer.create(this@AgentControllerImpl.context, sound)
                mediaPlayer?.start()
            }
        }
    }


    private inner class AnimationRunnable(private val agent: UiAgent) : Runnable {

        override fun run() {
            Timber.d(
                    "Execute animationRunnable: image layer null: %s, #imagelayer: %s, killed: %s, animationRunning: %s",
                    imageLayer == null, if (imageLayer != null) imageLayer!!.size else 0, isKilled, animationIsRunning.get()
            )

            if (imageLayer != null && imageLayer!!.size > 0 && !isKilled && animationIsRunning.get()) {
                val uiAnimation = getRandomAnimation(agent)
                val animationDrawable = AnimationUtil.getAnimationDrawable(this@AgentControllerImpl.context, uiAnimation!!, agent.overlayCount)
                val animationDrawables = animationDrawable.animationDrawables

                for (imageView in imageLayer!!) {
                    imageView.setBackgroundDrawable(ContextCompat.getDrawable(context, agentType.agentMapping.emptyFrameId))
                }

                val firstLayer = object : CustomAnimationDrawableNew(animationDrawable.animationDrawables[0]) {
                    override fun onAnimationFinish() {
                        if (animationIsRunning.get() && !isKilled) {
                            startAnimation(agent)
                        }
                    }
                }

                imageLayer!![0].setBackgroundDrawable(firstLayer)

                for (i in 1 until animationDrawables.size) {
                    imageLayer!![i].setBackgroundDrawable(animationDrawables[i])
                }

                for (imageView in imageLayer!!) {
                    (imageView.background as AnimationDrawable).start()
                }

                startSoundHandler(animationDrawable.soundMappings)
            }
        }
    }

}


