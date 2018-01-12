package com.ferranpons.clippyassistant

import android.os.*
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import com.ferranpons.clippylib.FloatingService
import com.ferranpons.clippylib.model.AgentType
import com.ferranpons.clippylib.utils.IntentHelper


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val button = findViewById<Button>(R.id.clippy_button_start)
        button.setOnClickListener({
            startService(IntentHelper.getCommandIntent(this, FloatingService.Command.Kill))

            Handler().postDelayed({ startService(IntentHelper.getShowIntent(this, AgentType.CLIPPY)) }, 500)
        })
    }
}
