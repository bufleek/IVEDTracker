package com.ived.tracker.ui.connect

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ived.tracker.R
import com.ived.tracker.databinding.DialogConnectBinding
import com.ived.tracker.ui.rtc.RTCActivity

class ConnectDialog(context: Context) : Dialog(context, R.style.Theme_IVEDTracker_PauseDialog) {
    private lateinit var binding: DialogConnectBinding
    private val db by lazy { Firebase.firestore }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogConnectBinding.inflate(LayoutInflater.from(context))
        setContentView(binding.root)

        binding.connect.setOnClickListener {
            if (binding.meetingId.text.toString().trim().isNullOrEmpty())
                binding.meetingId.error = "Please enter meeting id"
            else {
                db.collection("calls")
                    .document(binding.meetingId.text.toString())
                    .get()
                    .addOnSuccessListener {
                        if (it["type"] == "OFFER" || it["type"] == "ANSWER" || it["type"] == "END_CALL") {
                            binding.meetingId.error = "Please enter new meeting ID"
                        } else {
                            val intent = Intent(context, RTCActivity::class.java)
                            intent.putExtra("meetingID", binding.meetingId.text.toString())
                            intent.putExtra("isJoin", false)
                            context.startActivity(intent)
                        }
                    }
                    .addOnFailureListener {
                        binding.meetingId.error = "Please enter new meeting ID"
                    }
            }
        }
    }
}