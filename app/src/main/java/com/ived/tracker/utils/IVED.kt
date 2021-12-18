package com.ived.tracker.utils

import android.app.Application
import android.content.Intent
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ived.tracker.utils.services.IvedService
import com.ived.tracker.utils.tools.Const
import com.ived.tracker.utils.tools.startIvedService

class IVED: Application() {
    val dataStore by preferencesDataStore(name = "prefs")
    val db by lazy { Firebase.firestore }
    private val _deviceId: MutableLiveData<String?> = MutableLiveData(null)
    val deviceId: LiveData<String?> get() = _deviceId

    override fun onCreate() {
        super.onCreate()
        startIvedService(Intent(this, IvedService::class.java))
    }

    fun setDeviceId(id: String?){
        id?.let{
            startService(Intent(this, IvedService::class.java).apply {
                putExtra(Const.KEY_DEVICE_ID, it)
            })
        }
        _deviceId.value = id
    }
}