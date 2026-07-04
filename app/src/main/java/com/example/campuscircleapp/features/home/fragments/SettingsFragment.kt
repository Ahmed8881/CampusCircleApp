package com.example.campuscircleapp.features.home.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.example.campuscircleapp.R
import com.example.campuscircleapp.core.models.UiState
import com.example.campuscircleapp.core.theme.ThemeManager
import com.example.campuscircleapp.features.home.viewModels.SettingsViewModel
import com.example.campuscircleapp.shared.services.SessionManager
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.switchmaterial.SwitchMaterial
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

        // --- Dark Mode Toggle ---
        val darkModeSwitch = view.findViewById<SwitchMaterial>(R.id.darkModeSwitch)
        darkModeSwitch.isChecked = ThemeManager.isDarkMode(requireContext())
        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            ThemeManager.saveDarkMode(requireContext(), isChecked)
            requireActivity().recreate()
        }

        // --- Dynamic Role Text ---
        val roleText = view.findViewById<TextView>(R.id.settingsRoleText)
        val role = SessionManager.getRole(requireContext())
        roleText.text = when (role?.lowercase()) {
            "admin", "superadmin" -> "Admin Account"
            "teacher", "instructor" -> "Teacher Account"
            else -> "Student Account"
        }

        // --- Theme Picker ---
        setupThemeGrid(view)

        // --- Password Reset ---
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

    private fun setupThemeGrid(root: View) {
        val themeGrid = root.findViewById<LinearLayout>(R.id.themeGrid)
        val currentThemeId = ThemeManager.getSavedThemeId(requireContext())
        val ctx = requireContext()
        val inflater = LayoutInflater.from(ctx)
        val themes = ThemeManager.availableThemes
        val chunked = themes.chunked(5)

        for (row in chunked) {
            val rowLayout = LinearLayout(ctx).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                orientation = LinearLayout.HORIZONTAL
            }

            for (theme in row) {
                val item = inflater.inflate(R.layout.item_theme_preview, rowLayout, false)
                val circle = item.findViewById<MaterialCardView>(R.id.themeColorCircle)

                val previewColor = androidx.core.content.res.ResourcesCompat.getColor(
                    ctx.resources,
                    when (theme.id) {
                        "seckho" -> R.color.preview_seckho
                        "ocean" -> R.color.preview_ocean
                        "midnight" -> R.color.preview_midnight
                        "forest" -> R.color.preview_forest
                        "lavender" -> R.color.preview_lavender
                        "sunset" -> R.color.preview_sunset
                        "rose" -> R.color.preview_rose
                        "slate" -> R.color.preview_slate
                        "aubergine" -> R.color.preview_aubergine
                        "gold" -> R.color.preview_gold
                        else -> R.color.preview_seckho
                    },
                    null
                )
                circle.setCardBackgroundColor(previewColor)

                if (theme.id == currentThemeId) {
                    circle.setStrokeColor(ctx.getColor(R.color.color_primary))
                    circle.strokeWidth = 4
                }

                val nameView = item.findViewById<android.widget.TextView>(R.id.themeName)
                nameView.text = theme.name

                item.setOnClickListener {
                    if (theme.id != ThemeManager.getSavedThemeId(ctx)) {
                        ThemeManager.saveTheme(ctx, theme.id)
                        requireActivity().recreate()
                    }
                }

                rowLayout.addView(item)
            }

            themeGrid.addView(rowLayout)
        }
    }
}
