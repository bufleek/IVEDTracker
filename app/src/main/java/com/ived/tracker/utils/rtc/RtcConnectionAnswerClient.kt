package com.ived.tracker.utils.rtc

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.ived.tracker.ui.permissions.PermissionsActivity
import com.ived.tracker.utils.tools.Const
import org.webrtc.*

class RtcConnectionAnswerClient(
    private val deviceId: String,
    private val meetingId: String,
    private val isJoin: Boolean,
    private val context: Context
) {
    private lateinit var rtcClient: RTCClient
    private lateinit var signallingClient: SignalingClient
    private val audioManager by lazy { RTCAudioManager.create(context) }
    private var isMute = false
    private var isVideoPaused = false
    private var inSpeakerMode = true
    private val sdpObserver = object : AppSdpObserver() {
        override fun onCreateSuccess(p0: SessionDescription?) {
            super.onCreateSuccess(p0)
//            signallingClient.send(p0)
        }
    }

    fun answer() {
        checkCameraAndAudioPermission()
        audioManager.selectAudioDevice(RTCAudioManager.AudioDevice.SPEAKER_PHONE)
    }

    private fun checkCameraAndAudioPermission() {
        if ((ContextCompat.checkSelfPermission(context, CAMERA_PERMISSION)
                    != PackageManager.PERMISSION_GRANTED) &&
            (ContextCompat.checkSelfPermission(context, AUDIO_PERMISSION)
                    != PackageManager.PERMISSION_GRANTED)
        ) {
            context.startActivity(Intent(context, PermissionsActivity::class.java).apply {
                action = Const.ACTION_CAMERA_AUDIO_PERMISSIONS
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } else {
            onCameraAndAudioPermissionGranted()
        }
    }

    fun onCameraAndAudioPermissionGranted() {
        rtcClient = RTCClient(
            context,
            object : PeerConnectionObserver() {
                override fun onIceCandidate(p0: IceCandidate?) {
                    super.onIceCandidate(p0)
                    signallingClient.sendIceCandidate(p0, isJoin)
                    rtcClient.addIceCandidate(p0)
                }

                override fun onAddStream(p0: MediaStream?) {
                    super.onAddStream(p0)
                    Log.e(TAG, "onAddStream: $p0")
//                    p0?.videoTracks?.get(0)?.addSink(binding.remoteView)
                }

                override fun onIceConnectionChange(p0: PeerConnection.IceConnectionState?) {
                    Log.e(TAG, "onIceConnectionChange: $p0")
                }

                override fun onIceConnectionReceivingChange(p0: Boolean) {
                    Log.e(TAG, "onIceConnectionReceivingChange: $p0")
                }

                override fun onConnectionChange(newState: PeerConnection.PeerConnectionState?) {
                    Log.e(TAG, "onConnectionChange: $newState")
                }

                override fun onDataChannel(p0: DataChannel?) {
                    Log.e(TAG, "onDataChannel: $p0")
                }

                override fun onStandardizedIceConnectionChange(newState: PeerConnection.IceConnectionState?) {
                    Log.e(TAG, "onStandardizedIceConnectionChange: $newState")
                }

                override fun onAddTrack(p0: RtpReceiver?, p1: Array<out MediaStream>?) {
                    Log.e(TAG, "onAddTrack: $p0 \n $p1")
                }

                override fun onTrack(transceiver: RtpTransceiver?) {
                    Log.e(TAG, "onTrack: $transceiver")
                }
            }
        )

//        rtcClient.initSurfaceView(binding.remoteView)
        if (isJoin) {
//            rtcClient.initSurfaceView(binding.localView)
            rtcClient.startLocalVideoCapture(context)
        }
        signallingClient = SignalingClient(deviceId, meetingId, createSignallingClientListener())
        if (!isJoin)
            rtcClient.call(sdpObserver, meetingId)
    }

    fun endCall(){
        rtcClient.endCall(meetingId)
    }

    private fun createSignallingClientListener() = object : SignalingClientListener {
        override fun onConnectionEstablished() {
//            binding.endCallButton.isClickable = true
        }

        override fun onOfferReceived(description: SessionDescription) {
            rtcClient.onRemoteSessionReceived(description)
            Const.isInitiatedNow = false
            rtcClient.answer(sdpObserver, meetingId)
//            binding.remoteViewLoading.isGone = true
            Log.i(TAG, "onOfferReceived: Offer received")
        }

        override fun onAnswerReceived(description: SessionDescription) {
            rtcClient.onRemoteSessionReceived(description)
            Const.isInitiatedNow = false
//            binding.remoteViewLoading.isGone = true
            Log.i(TAG, "onAnswerReceived: Answer received")
        }

        override fun onIceCandidateReceived(iceCandidate: IceCandidate) {
            rtcClient.addIceCandidate(iceCandidate)
        }

        override fun onCallEnded() {
            if (!Const.isCallEnded) {
                Const.isCallEnded = true
                rtcClient.endCall(meetingId)
//                finish()
//                startActivity(Intent(this@RTCActivity, MainActivity::class.java))
            }
        }
    }

    companion object {
        private const val CAMERA_AUDIO_PERMISSION_REQUEST_CODE = 1
        private const val CAMERA_PERMISSION = Manifest.permission.CAMERA
        private const val AUDIO_PERMISSION = Manifest.permission.RECORD_AUDIO
        private const val TAG = "RtcConnectionClient"
    }
}