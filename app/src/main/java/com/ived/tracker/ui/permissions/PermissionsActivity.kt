package com.ived.tracker.ui.permissions

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.*
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.ived.tracker.utils.services.IvedService
import com.ived.tracker.utils.tools.Const
import org.webrtc.*

class PermissionsActivity : AppCompatActivity() {
    private var ivedServiceMessenger: Messenger? = null
    private var ivedServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            ivedServiceMessenger = Messenger(service)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            ivedServiceMessenger = null
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent(this, IvedService::class.java)
        bindService(intent, ivedServiceConnection, Context.BIND_AUTO_CREATE)

        when (intent.action) {
            Const.ACTION_CAMERA_AUDIO_PERMISSIONS -> requestCameraAndAudioPermission()
        }
    }

    private fun requestCameraAndAudioPermission(dialogShown: Boolean = false) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, CAMERA_PERMISSION) &&
            ActivityCompat.shouldShowRequestPermissionRationale(this, AUDIO_PERMISSION) &&
            !dialogShown
        ) {
            showPermissionRationaleDialog()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(CAMERA_PERMISSION, AUDIO_PERMISSION),
                Const.CODE_CAMERA_AUDIO_PERMISSION_REQUEST
            )
        }
    }

    private fun showPermissionRationaleDialog() {
        AlertDialog.Builder(this)
            .setTitle("Camera And Audio Permission Required")
            .setMessage("This app need the camera and audio to function")
            .setPositiveButton("Grant") { dialog, _ ->
                dialog.dismiss()
                requestCameraAndAudioPermission(true)
            }
            .setNegativeButton("Deny") { dialog, _ ->
                dialog.dismiss()
                onCameraPermissionDenied()
            }
            .show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Const.CODE_CAMERA_AUDIO_PERMISSION_REQUEST && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            val msg = Message.obtain(null, Const.MSG_CAMERA_AUDIO_PERMISSION_GRANTED, 0, 0)
            try {
                ivedServiceMessenger?.send(msg)
            }
            catch (e: RemoteException){
                e.printStackTrace()
                Log.e(TAG, "onRequestPermissionsResult: Failed")
            }
        } else {
            onCameraPermissionDenied()
        }
        finish()
    }

    private fun onCameraPermissionDenied() {
        Toast.makeText(this, "Camera and Audio Permission Denied", Toast.LENGTH_LONG).show()
    }

    override fun onStop() {
        super.onStop()
        unbindService(ivedServiceConnection)
    }
    companion object {
        private const val CAMERA_PERMISSION = Manifest.permission.CAMERA
        private const val AUDIO_PERMISSION = Manifest.permission.RECORD_AUDIO
        private const val TAG = "PermissionsActivity"
    }
}