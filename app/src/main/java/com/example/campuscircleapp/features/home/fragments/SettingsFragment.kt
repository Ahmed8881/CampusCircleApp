package com.example.campuscircleapp.features.home.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.example.campuscircleapp.R
import com.example.campuscircleapp.core.models.UiState
import com.example.campuscircleapp.features.home.viewModels.SettingsViewModel
import com.example.campuscircleapp.shared.services.SessionManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

class SettingsFragment : Fragment() {

    private lateinit var viewModel: SettingsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_settings, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[SettingsViewModel::class.java]

        val oldPasswordInput = view.findViewById<TextInputEditText>(R.id.settingsOldPassword)
        val newPasswordInput = view.findViewById<TextInputEditText>(R.id.settingsNewPassword)
        val confirmPasswordInput = view.findViewById<TextInputEditText>(R.id.settingsConfirmPassword)
        val saveBtn = view.findViewById<MaterialButton>(R.id.settingsSaveBtn)

        val token = SessionManager.getToken(requireContext()) ?: return

        viewModel.resetState.asLiveData().observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> saveBtn.isEnabled = false
                is UiState.Success -> {
                    saveBtn.isEnabled = true
                    MotionToast.createColorToast(
                        requireActivity(),
                        "Success",
                        "Password changed successfully!",
                        MotionToastStyle.SUCCESS,
                        MotionToast.GRAVITY_TOP,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(requireContext(), www.sanju.motiontoast.R.font.helvetica_regular)
                    )
                    oldPasswordInput.text?.clear()
                    newPasswordInput.text?.clear()
                    confirmPasswordInput.text?.clear()
                    viewModel.resetIdle()
                }
                is UiState.Error -> {
                    saveBtn.isEnabled = true
                    MotionToast.createColorToast(
                        requireActivity(),
                        "Error",
                        state.message,
                        MotionToastStyle.ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(requireContext(), www.sanju.motiontoast.R.font.helvetica_regular)
                    )
                    viewModel.resetIdle()
                }
                else -> saveBtn.isEnabled = true
            }
        }

        saveBtn.setOnClickListener {
            val old = oldPasswordInput.text.toString()
            val new = newPasswordInput.text.toString()
            val confirm = confirmPasswordInput.text.toString()

            if (old.isBlank() || new.isBlank() || confirm.isBlank()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (new != confirm) {
                Toast.makeText(requireContext(), "New passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (new.length < 6) {
                Toast.makeText(requireContext(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.resetPassword(token, old, new)
        }
    }
}
