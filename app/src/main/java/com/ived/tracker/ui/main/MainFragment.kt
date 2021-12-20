package com.ived.tracker.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ived.tracker.databinding.FragmentMainBinding
import com.ived.tracker.ui.connect.ConnectDialog

class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.cardRemoteCameraAccess.setOnClickListener {
            showConnectionDialog()
        }

        binding.cardRemoteControl.setOnClickListener {
            Toast.makeText(requireContext(), "Development in progress...", Toast.LENGTH_LONG).show()
        }
    }

    private fun showConnectionDialog() {
        ConnectDialog(requireContext()).show()
    }
}