package com.ferranpons.clippylib.broadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

import com.ferranpons.clippylib.FloatingService
import com.ferranpons.clippylib.Global
import com.ferranpons.clippylib.model.AgentType
import com.ferranpons.clippylib.utils.IntentHelper
import timber.log.Timber


class DeviceUnlock : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        if (intent.action == Intent.ACTION_USER_PRESENT) {
            Timber.d("Device unlocked - Start")
            context.startService(IntentHelper.getStartStopIntent(context, FloatingService.Command.Start, false))

        } else if (intent.action == Intent.ACTION_SCREEN_OFF) {
            Timber.d("Device locked - Stop")
            context.startService(IntentHelper.getStartStopIntent(context, FloatingService.Command.Stop, false))

        } else if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
            val lastUsedAgent = Global.INSTANCE.agentStorage.lastUsedAgent
            val startOnBoot = Global.INSTANCE.settingsStorage.isSettingsStartOnBoot
            Timber.d("Device boot completed - Starting agent - Agent: %s, startOnBoot: %s", lastUsedAgent, startOnBoot)

            if (startOnBoot!!) {
                context.startService(IntentHelper.getShowIntent(context, lastUsedAgent!!))
            }
        }

    }
}
