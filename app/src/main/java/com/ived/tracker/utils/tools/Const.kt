package com.ived.tracker.utils.tools

object Const {
    var isCallEnded: Boolean = false
    var isInitiatedNow : Boolean = true

    const val CODE_MAIN_SERVICE = 1000
    const val CODE_MAIN_SERVICE_NOTIFICATION = 1001
    const val CODE_CAMERA_AUDIO_PERMISSION_REQUEST = 1002

    const val MSG_CAMERA_AUDIO_PERMISSION_GRANTED = 2000
    const val MSG_CAMERA_ACCESS_CONSENT_GRANTED = 2001

    const val ACTION_CAMERA_AUDIO_PERMISSIONS = "REQUEST CAMERA & AUDIO PERMISSION"
    const val ACTION_CONSENT_CAMERA_ACCESS = "CAMERA ACCESS CONSENT"

    const val KEY_MAIN_NOTIFICATION_CHANNEL = "MAIN NOTIFICATION BUTTON"
    const val KEY_DEVICE_ID = "DEVICE ID"
    const val KEY_REMOTE_DEVICE_ID = "REMOTE DEVICE"
}