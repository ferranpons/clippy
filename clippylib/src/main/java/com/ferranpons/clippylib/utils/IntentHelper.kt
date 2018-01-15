package com.ferranpons.clippylib.utils


import android.content.Context
import android.content.Intent

import com.ferranpons.clippylib.FloatingService
import com.ferranpons.clippylib.model.AgentType
import java.lang.IllegalArgumentException

object IntentHelper {


    fun getStartStopIntent(context: Context, command: FloatingService.Command, user: Boolean): Intent {
        if (command === FloatingService.Command.Stop || command === FloatingService.Command.Start) {
            val commandIntent = getCommandIntent(context, command)
            commandIntent.putExtra(FloatingService.AGENT_ACTION_USER, user)
            return commandIntent
        } else {
            throw IllegalArgumentException("Invalid command")
        }
    }

    fun getCommandIntent(context: Context, command: FloatingService.Command): Intent {
        val intent = Intent(context, FloatingService::class.java)
        intent.putExtra(FloatingService.Command.KEY, command)
        return intent
    }

    fun getShowIntent(context: Context, agentType: AgentType): Intent {
        val commandIntent = getCommandIntent(context, FloatingService.Command.Show)
        commandIntent.putExtra(AgentType.KEY, agentType)
        return commandIntent
    }

}
