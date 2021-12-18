package com.ived.tracker.utils.tools

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.datastore.preferences.core.stringPreferencesKey

val deviceIdPref = stringPreferencesKey("deviceId_temp")
val deviceUUIDPref = stringPreferencesKey("deviceUUID_temp")

fun Context.startIvedService(intent: Intent) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        startForegroundService(intent)
        return
    }
    startService(intent)
}