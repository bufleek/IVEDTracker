package com.ived.tracker.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.lifecycleScope
import com.ived.tracker.ui.main.MainActivity
import com.ived.tracker.utils.IVED
import com.ived.tracker.utils.tools.deviceIdPref
import com.ived.tracker.utils.tools.deviceUUIDPref
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.*

class SplashActivity : AppCompatActivity() {
    private val ived by lazy { application as IVED }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ived.deviceId.observe(this, {
            if (it != null) {
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                val deviceIdFlow: Flow<String?> = ived.dataStore.data.map { prefs ->
                    prefs[deviceIdPref]
                }

                lifecycleScope.launch {
                    deviceIdFlow.collectLatest { id ->
                        val deviceId = id ?: System.currentTimeMillis().toString()
                            .takeLast(9) //make device Id request instead if null
                        if (id == null) {
                            val uuid = UUID.randomUUID().toString()
                            ived.dataStore.edit { mutablePreferences ->
                                mutablePreferences[deviceUUIDPref] = uuid
                                mutablePreferences[deviceIdPref] = deviceId
                            }
                        }
                        ived.setDeviceId(deviceId)
                    }
                }
            }
        })
    }
}