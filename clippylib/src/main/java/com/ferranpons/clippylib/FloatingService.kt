package com.ferranpons.clippylib

import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.IBinder

import com.ferranpons.clippylib.broadcastreceiver.DeviceUnlock
import com.ferranpons.clippylib.model.AgentType
import com.ferranpons.clippylib.view.NotificationHelper
import timber.log.Timber

class FloatingService : Service() {

    private val mBinder = LocalBinder()
    private var agentController: AgentController? = null
    private var mReceiver: BroadcastReceiver? = null

    private val agentControllerListener = object : AgentControllerListener {
        override fun volumeChanged(mute: Boolean) {
            Timber.d("AgentControllerListener mute: %s", mute)
            Global.INSTANCE.agentStorage.isMute = mute
            sendAgentState()
        }

        override fun stateChanged(started: Boolean) {
            Timber.d("AgentControllerListener started: %s", started)
            sendAgentState()
        }
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (intent == null) {
            return Service.START_STICKY
        }

        if (intent.hasExtra(Command.KEY)) {

            val command = intent.getSerializableExtra(Command.KEY) as Command
            Timber.d("onStartCommand with command: %s", command)

            when (command) {
                FloatingService.Command.Show -> if (agentController == null) {
                    val agentType = intent.getSerializableExtra(AgentType.KEY) as AgentType
                    Global.INSTANCE.agentStorage.setAgentLastUsed(agentType)

                    this.agentController = AgentControllerImpl(agentType, applicationContext, Global.INSTANCE.agentService)
                    if (Global.INSTANCE.agentStorage.isMute) {
                        agentController!!.mute()
                    } else {
                        agentController!!.unMute()
                    }
                    this.agentController!!.setAgentControllerListener(agentControllerListener)

                    startForeground(NOTIFICATION_ID, NotificationHelper.getNotification(this, agentController!!.agentType, agentController!!.isRunning, agentController!!.isMute))
                    registerBroadcastListener()
                    sendAgentState()
                }

                FloatingService.Command.Start -> if (agentController != null && !agentController!!.isRunning) {
                    val user = intent.getBooleanExtra(AGENT_ACTION_USER, AGENT_ACTION_USER_DEFAULT)
                    agentController!!.start(user)
                }

                FloatingService.Command.Stop -> if (agentController != null && agentController!!.isRunning) {
                    val user = intent.getBooleanExtra(AGENT_ACTION_USER, AGENT_ACTION_USER_DEFAULT)
                    agentController!!.stop(user)
                }

                FloatingService.Command.Kill -> {
                    if (agentController != null) {
                        agentController!!.kill()
                        unregisterBroadcastListener()
                        this.agentController = null
                    }
                    sendAgentState()
                    stopSelf()
                }

                FloatingService.Command.Mute -> if (agentController != null && !agentController!!.isMute) {
                    agentController!!.mute()
                }

                FloatingService.Command.UnMute -> if (agentController != null && agentController!!.isMute) {
                    agentController!!.unMute()
                }

                FloatingService.Command.State -> sendAgentState()
            }

            if (agentController != null) {
                val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                mNotificationManager.notify(NOTIFICATION_ID, NotificationHelper.getNotification(this, agentController!!.agentType, agentController!!.isRunning, agentController!!.isMute))
            }
        }

        return Service.START_NOT_STICKY
    }

    private fun sendAgentState() {
        val intent = Intent(AGENT_STATE_ACTION)
        if (agentController != null && agentController!!.isInitialized) {
            intent.putExtra(AGENT_STATE_MUTE, agentController!!.isMute)
            intent.putExtra(AGENT_STATE_RUNNING, !agentController!!.isKilled)
            intent.putExtra(AGENT_STATE_STARTED, agentController!!.isRunning)
            intent.putExtra(AGENT_STATE_TYPE, agentController!!.agentType)
        } else {
            intent.putExtra(AGENT_STATE_RUNNING, false)
        }
        sendBroadcast(intent)
    }

    private fun registerBroadcastListener() {
        Timber.d("Register BroadcastListener - DeviceUnlock")
        val filter = IntentFilter(Intent.ACTION_SCREEN_ON)
        filter.addAction(Intent.ACTION_SCREEN_OFF)
        this.mReceiver = DeviceUnlock()
        registerReceiver(mReceiver, filter)
    }

    private fun unregisterBroadcastListener() {
        if (mReceiver != null) {
            Timber.d("Unregister BroadcastListener - DeviceUnlock")
            unregisterReceiver(mReceiver)
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return mBinder
    }

    inner class LocalBinder : Binder() {
        internal val service: FloatingService
            get() = this@FloatingService
    }

    enum class Command {
        Show, Start, Stop, Kill, Mute, UnMute, State;


        companion object {

            const val KEY = "COMMAND"
        }
    }

    companion object {

        const val AGENT_STATE_ACTION = "com.ferranpons.clippylib.AGENT_STATE"
        const val AGENT_STATE_MUTE = "agent_state_mute"
        const val AGENT_STATE_STARTED = "agent_state_started"
        const val AGENT_STATE_RUNNING = "agent_state_running"
        const val AGENT_STATE_TYPE = "agent_state_type"

        const val AGENT_ACTION_USER = "extra_agent_user"
        const val AGENT_ACTION_USER_DEFAULT = false

        private const val NOTIFICATION_ID = 14232
    }

}
