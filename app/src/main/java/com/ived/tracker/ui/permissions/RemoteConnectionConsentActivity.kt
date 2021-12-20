package com.ived.tracker.ui.permissions

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.ived.tracker.utils.services.IvedService
import com.ived.tracker.utils.tools.Const

class RemoteConnectionConsentActivity : AppCompatActivity() {
    private var ivedServiceMessenger: Messenger? = null
    private val ivedServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            ivedServiceMessenger = Messenger(service)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            ivedServiceMessenger = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindService(
            Intent(this, IvedService::class.java),
            ivedServiceConnection,
            Context.BIND_AUTO_CREATE
        )

        when (intent.action) {
            Const.ACTION_CONSENT_CAMERA_ACCESS -> {
                val remoteId = intent.getStringExtra(Const.KEY_REMOTE_DEVICE_ID)
                if (remoteId != null) {
                    cameraAccessConsent(remoteId)
                } else finish()
            }
            else -> finish()
        }
    }

    private fun cameraAccessConsent(remoteId: String) {
        AlertDialog.Builder(this)
            .setTitle("IVED TRACKER")
            .setMessage("$remoteId is requesting to access your camera")
            .setPositiveButton("Allow") { _, _ ->
                val msg = Message.obtain(null, Const.MSG_CAMERA_ACCESS_CONSENT_GRANTED, 0, 0)
                ivedServiceMessenger?.send(msg)
                finish()
            }
            .setNegativeButton("Deny") { _, _ -> finish() }
            .create()
            .show()
    }

    override fun onStop() {
        super.onStop()
        unbindService(ivedServiceConnection)
    }
}