package com.ferranpons.clippylib.view

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat

import com.ferranpons.clippylib.FloatingService
import com.ferranpons.clippylib.R
import com.ferranpons.clippylib.utils.IntentHelper
import com.ferranpons.clippylib.model.AgentType
import com.ferranpons.clippylib.utils.StringUtils

object NotificationHelper {

    fun getNotification(context: Context, agentType: AgentType, isRunning: Boolean, isMute: Boolean): Notification {

        //val startMainActivity = PendingIntent.getActivity(context, 0, Intent(context, MainActivity::class.java), PendingIntent.FLAG_CANCEL_CURRENT)

        val startStopCommand = if (isRunning) FloatingService.Command.Stop else FloatingService.Command.Start
        val startStopDrawable = if (isRunning) R.drawable.clippy_ic_action_stop else R.drawable.clippy_ic_action_play
        val startStopString = if (isRunning) context.getString(R.string.clippy_notification_action_stop) else context.getString(R.string.clippy_notification_action_start)
        val startStopPending = PendingIntent.getService(context, 110, IntentHelper.getStartStopIntent(context, startStopCommand, true), PendingIntent.FLAG_CANCEL_CURRENT)

        val muteUnmuteCommand = if (isMute) FloatingService.Command.UnMute else FloatingService.Command.Mute
        val muteUnmuteDrawable = if (isMute) R.drawable.clippy_ic_action_volume_on else R.drawable.clippy_ic_action_volume_muted
        val muteUnmuteString = if (isMute) context.getString(R.string.clippy_notification_action_unmute) else context.getString(R.string.clippy_notification_action_mute)
        val muteUnmutePending = PendingIntent.getService(context, 120, IntentHelper.getCommandIntent(context, muteUnmuteCommand), PendingIntent.FLAG_CANCEL_CURRENT)

        val killPending = PendingIntent.getService(context, 130, IntentHelper.getCommandIntent(context, FloatingService.Command.Kill), PendingIntent.FLAG_CANCEL_CURRENT)

        val content = context.getString(R.string.clippy_notification_content)
        val title = StringUtils.capitalize(agentType.name)

        return NotificationCompat.Builder(context)
                .setSmallIcon(agentType.agentMapping.firstFrameId)
                .setContentTitle(title)
                .setContentText(content)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, agentType.agentMapping.firstFrameId))
                //.setContentIntent(startMainActivity)

                .setStyle(NotificationCompat.BigTextStyle().bigText(content))

                .addAction(startStopDrawable, startStopString, startStopPending)
                .addAction(muteUnmuteDrawable, muteUnmuteString, muteUnmutePending)
                //                .addAction(R.drawable.clippy_ic_action_cancel, context.getString(R.string.notification_action_quit), killPending)
                .build()
    }

}
