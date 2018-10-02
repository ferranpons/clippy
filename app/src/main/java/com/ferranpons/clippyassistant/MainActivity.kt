package com.ferranpons.clippyassistant

import android.content.BroadcastReceiver
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import com.ferranpons.clippylib.model.AgentType
import com.ferranpons.clippylib.utils.IntentHelper
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import com.ferranpons.clippylib.FloatingService
import com.ferranpons.clippylib.Global


private const val REQUEST_CODE = 145

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkDrawOverlayPermission()

        val button = findViewById<Button>(R.id.clippy_button_start)
        button.setOnClickListener {
            startKlippy()
        }

        Global.INSTANCE.init(applicationContext)
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(agentBroadcastReceiver, IntentFilter(FloatingService.AGENT_STATE_ACTION))
        initStateService()
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(agentBroadcastReceiver)
    }

    private fun initStateService() {
        startService(IntentHelper.getCommandIntent(this, FloatingService.Command.State))
    }

    private fun startKlippy() {
        startService(IntentHelper.getCommandIntent(this, FloatingService.Command.Kill))
        Handler().postDelayed({
            startService(IntentHelper.getShowIntent(this, AgentType.CLIPPY))
        }, 500)
    }

    private val agentBroadcastReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == FloatingService.AGENT_STATE_ACTION) {
                val isRunning = intent.getBooleanExtra(FloatingService.AGENT_STATE_RUNNING, false)
                Log.d("****** CLIPPY","AgentStateBroadcastReceiver called - isRunning: {$isRunning}")

                if (isRunning) {
                    val mute = intent.getBooleanExtra(FloatingService.AGENT_STATE_MUTE, false)
                    val started = intent.getBooleanExtra(FloatingService.AGENT_STATE_STARTED, false)
                    Log.d("****** CLIPPY", "AgentStateBroadcastReceiver called - mute: {$mute}, started: {$started}")
                }
            }
        }
    }

    private fun checkDrawOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            startActivityForResult(intent, REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && requestCode == REQUEST_CODE) {
            if (Settings.canDrawOverlays(this)) {
                val button = findViewById<Button>(R.id.clippy_button_start)
                button.setOnClickListener {
                    startKlippy()
                }
                Global.INSTANCE.init(applicationContext)
            }
        }
    }
}
