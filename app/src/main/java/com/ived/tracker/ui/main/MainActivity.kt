package com.ived.tracker.ui.main

import android.os.Bundle
import android.view.Gravity
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.ived.tracker.R
import com.ived.tracker.databinding.ActivityMainBinding
import com.ived.tracker.utils.IVED
import com.ived.tracker.utils.tools.Const

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val ived by lazy { (application as IVED) }
    private val db by lazy { ived.db }
    private lateinit var tvDeviceId: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tvDeviceId = binding.navView.getHeaderView(0)
            .findViewById(R.id.tv_device_id)

        Const.isInitiatedNow = true
        Const.isCallEnded = true

        ived.deviceId.observe(this, {
            tvDeviceId.text = it?.replace("...".toRegex(), "$0 ") ?: ""
        })

        findViewById<ImageView>(R.id.img_drawer_toggle).setOnClickListener {
            if (binding.drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                binding.drawerLayout.closeDrawer(Gravity.RIGHT)
            } else {
                binding.drawerLayout.openDrawer(Gravity.RIGHT)
            }
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.navView.setupWithNavController(navController)
    }
}