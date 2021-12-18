package com.ived.tracker.utils.broadcast_receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ived.tracker.utils.services.IvedService
import com.ived.tracker.utils.tools.startIvedService

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            context.startIvedService(Intent(context, IvedService::class.java))
        }
    }
}