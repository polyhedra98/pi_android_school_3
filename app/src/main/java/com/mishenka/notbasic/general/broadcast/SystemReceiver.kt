package com.mishenka.notbasic.general.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.mishenka.notbasic.R

class SystemReceiver : BroadcastReceiver() {

    private val TAG = "SystemReceiver"


    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.action?.let { safeAction ->
            with(context) {
                val msg = when(safeAction) {
                    Intent.ACTION_POWER_CONNECTED -> context?.getString(R.string.power_connected_ui)
                    Intent.ACTION_POWER_DISCONNECTED -> context?.getString(R.string.power_disconnected_ui)
                    else -> context?.getString(R.string.broadcast_error_ui)
                }
                if (context != null && msg != null) {
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                } else {
                    Log.i("NYA_$TAG", "Can't show toast. Context is null.")
                }
            }
        }
    }

}