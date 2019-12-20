package com.mishenka.notbasic.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.mishenka.notbasic.R

class SystemReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val intentAction = intent.action

        intentAction?.let { safeAction ->
            with(context) {
                val msg = when(safeAction) {
                    Intent.ACTION_BATTERY_CHANGED -> getString(R.string.broadcast_battery_changed)
                    Intent.ACTION_POWER_CONNECTED -> getString(R.string.broadcast_power_connected)
                    Intent.ACTION_POWER_DISCONNECTED -> getString(R.string.broadcast_power_disconnected)
                    else -> getString(R.string.broadcast_error)
                }
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            }

        }

    }
}
