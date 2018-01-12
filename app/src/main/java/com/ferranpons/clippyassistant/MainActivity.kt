package com.ferranpons.clippyassistant

import android.content.*
import android.os.*
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import com.ferranpons.clippylib.*
import com.ferranpons.clippylib.model.AgentType
import com.ferranpons.clippylib.utils.IntentHelper


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val button = findViewById<Button>(R.id.clippy_button_start)
        button.setOnClickListener({
            startService(IntentHelper.getCommandIntent(this, FloatingService.Command.Kill))
            Handler().postDelayed({
                startService(IntentHelper.getShowIntent(this, AgentType.CLIPPY))
            }, 500)
            //startService(IntentHelper.getShowIntent(this.applicationContext, AgentType.CLIPPY))
        })
        Global.INSTANCE.init(applicationContext)
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(agentBroadcastReceiver, IntentFilter(FloatingService.AGENT_STATE_ACTION))
        initFabs()
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(agentBroadcastReceiver)
    }

    private fun initFabs() {
        //setFabVisible(false, fabKill, fabMute, fabUnmute, fabStart, fabStop)
        startService(IntentHelper.getCommandIntent(this, FloatingService.Command.State))
        Log.d("****** CLIPPY","Init Floating Action Buttons")
    }

    private val agentBroadcastReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == FloatingService.AGENT_STATE_ACTION) {

                val isRunning = intent.getBooleanExtra(FloatingService.AGENT_STATE_RUNNING, false)
                Log.d("****** CLIPPY","AgentStateBroadcastReceiver called - isRunning: {$isRunning}")

                if (isRunning) {

                    val mute = intent.getBooleanExtra(FloatingService.AGENT_STATE_MUTE, false)
                    val started = intent.getBooleanExtra(FloatingService.AGENT_STATE_STARTED, false)
                    val agentType = intent.getSerializableExtra(FloatingService.AGENT_STATE_TYPE) as AgentType

                    /*initFab(fabKill, false, IntentClickListener(IntentHelper.getCommandIntent(context, FloatingService.Command.Kill), context))
                    initFab(fabMute, mute, IntentClickListener(IntentHelper.getCommandIntent(context, FloatingService.Command.Mute), context))
                    initFab(fabUnmute, !mute, IntentClickListener(IntentHelper.getCommandIntent(context, FloatingService.Command.UnMute), context))
                    initFab(fabStart, started, IntentClickListener(IntentHelper.getStartStopIntent(context, FloatingService.Command.Start, true), context))
                    initFab(fabStop, !started, IntentClickListener(IntentHelper.getStartStopIntent(context, FloatingService.Command.Stop, true), context))*/

                    Log.d("****** CLIPPY", "AgentStateBroadcastReceiver called - mute: {$mute}, started: {$started}")

                } else {
                    /*setFabVisible(false, fabStart, fabKill, fabMute, fabUnmute, fabStop)
                    (recyclerView.getAdapter() as AgentAdapter).setSelectedAgent(null)*/

                }
            }
        }

    }
}
