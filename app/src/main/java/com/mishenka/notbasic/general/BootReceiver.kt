package com.mishenka.notbasic.general

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, broadcastIntent: Intent?) {

        broadcastIntent?.action?.let { safeAction ->
            if (safeAction == Intent.ACTION_BOOT_COMPLETED) {
                val intent = Intent(context, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context?.startActivity(intent)
            }
        }

    }

}