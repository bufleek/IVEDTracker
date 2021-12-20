package com.ived.tracker.utils.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ived.tracker.R
import com.ived.tracker.ui.permissions.RemoteConnectionConsentActivity
import com.ived.tracker.utils.rtc.RtcConnectionAnswerClient
import com.ived.tracker.utils.tools.Const

class IvedService : Service() {
    private lateinit var messenger: Messenger
    private var notificationManager: NotificationManager? = null
    private val db = Firebase.firestore
    private var rtcAnswerClient: RtcConnectionAnswerClient? = null
    private var deviceId: String? = null

    internal inner class IvedServiceHandler : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                Const.MSG_CAMERA_AUDIO_PERMISSION_GRANTED -> {
                    rtcAnswerClient?.onCameraAndAudioPermissionGranted()
                }
                Const.MSG_CAMERA_ACCESS_CONSENT_GRANTED -> {
                    if (deviceId != null) {
                        rtcAnswerClient?.endCall()
                        rtcAnswerClient =
                            RtcConnectionAnswerClient(
                                deviceId!!,
                                deviceId!!,
                                true,
                                this@IvedService
                            )
                        rtcAnswerClient?.answer()
                    }
                }
            }
            super.handleMessage(msg)
        }
    }

    override fun onBind(intent: Intent): IBinder {
        messenger = Messenger(IvedServiceHandler())
        return messenger.binder
    }

    override fun onCreate() {
        super.onCreate()
        showNotification()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            if (intent.hasExtra(Const.KEY_DEVICE_ID)) {
                deviceId = intent.getStringExtra(Const.KEY_DEVICE_ID)
                attachConnectionListener()
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun attachConnectionListener() {
        if (deviceId != null) {
            db.collection("calls").document(deviceId!!)
                .collection("candidates")
                .document("offerCandidate")
                .addSnapshotListener { documentSnapshot, e ->
                    if (documentSnapshot != null && documentSnapshot.data?.get("id") != null) {
                        val remoteId: String = documentSnapshot.data!!["id"].toString()
                        Log.d(TAG, "attachConnectionListener: $remoteId")

                        val intent = Intent(
                            this,
                            RemoteConnectionConsentActivity::class.java
                        )
                        intent.apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            action = Const.ACTION_CONSENT_CAMERA_ACCESS
                            putExtra(Const.KEY_REMOTE_DEVICE_ID, remoteId)
                        }
                        startActivity(intent)
//                    Toast.makeText(this, "Incoming connection request. $remoteId", Toast.LENGTH_LONG).show()
//                        val intent = Intent(this, RTCActivity::class.java)
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                        intent.putExtra("meetingID", deviceId)
//                        intent.putExtra("isJoin", true)
//                        startActivity(intent)
                    }
                }
        }

    }

    private fun showNotification() {
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                val notificationChannel = NotificationChannel(
                    Const.KEY_MAIN_NOTIFICATION_CHANNEL,
                    Const.KEY_MAIN_NOTIFICATION_CHANNEL,
                    NotificationManager.IMPORTANCE_NONE
                )
                notificationChannel.apply {
                    enableLights(false)
                    setShowBadge(false)
                    enableVibration(false)
                    setSound(null, null)
                    lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                }
                notificationManager?.createNotificationChannel(notificationChannel)
            } catch (e: Exception) {
            }
        }
        val notification = NotificationCompat.Builder(this, Const.KEY_MAIN_NOTIFICATION_CHANNEL)
            .setTicker(null)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(getString(R.string.main_service_notification_text))
            .setAutoCancel(false)
            .setOngoing(true)
            .setShowWhen(false)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(Notification.PRIORITY_LOW)
            .build()

        startForeground(Const.CODE_MAIN_SERVICE, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        notificationManager?.cancel(Const.CODE_MAIN_SERVICE_NOTIFICATION)
    }

    companion object {
        private const val TAG = "IvedService"
    }
}