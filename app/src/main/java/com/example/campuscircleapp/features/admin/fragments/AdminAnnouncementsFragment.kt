package com.example.campuscircleapp.features.admin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.example.campuscircleapp.R
import com.example.campuscircleapp.core.models.UiState
import com.example.campuscircleapp.features.home.models.SpaceResponse
import com.example.campuscircleapp.features.home.viewModels.AdminAnnouncementsViewModel
import com.example.campuscircleapp.shared.services.SessionManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class AdminAnnouncementsFragment : Fragment() {

    private val viewModel by lazy {
        ViewModelProvider(this)[AdminAnnouncementsViewModel::class.java]
    }

    private lateinit var targetRadioGroup: RadioGroup
    private lateinit var spaceSpinner: Spinner
    private lateinit var messageInput: TextInputEditText
    private lateinit var sendBtn: MaterialButton
    private lateinit var successText: TextView
    private lateinit var errorText: TextView

    private var spaces: List<SpaceResponse> = emptyList()
    private var token: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_admin_announcements, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        targetRadioGroup = view.findViewById(R.id.targetRadioGroup)
        spaceSpinner = view.findViewById(R.id.spaceSpinner)
        messageInput = view.findViewById(R.id.messageInput)
        sendBtn = view.findViewById(R.id.sendAnnouncementBtn)
        successText = view.findViewById(R.id.announceSuccessText)
        errorText = view.findViewById(R.id.announceErrorText)

        val sessionToken = SessionManager.getToken(requireContext())
        if (sessionToken.isNullOrBlank()) return
        token = sessionToken

        targetRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            spaceSpinner.isVisible = checkedId == R.id.radioSpecific
        }

        sendBtn.setOnClickListener { submitAnnouncement() }

        observeStates()
        viewModel.loadSpaces(token)
    }

    private fun observeStates() {
        viewModel.spacesState.asLiveData().observe(viewLifecycleOwner) { state ->
            if (state is UiState.Success) {
                spaces = state.data
                val names = spaces.map { it.name }
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, names)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spaceSpinner.adapter = adapter
            }
        }

        viewModel.announceState.asLiveData().observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    sendBtn.isEnabled = false
                    successText.isVisible = false
                    errorText.isVisible = false
                }
                is UiState.Success -> {
                    sendBtn.isEnabled = true
                    successText.isVisible = true
                    successText.text = state.message ?: "Announcement sent successfully!"
                    errorText.isVisible = false
                    messageInput.setText("")
                    viewModel.resetAnnounceState()
                }
                is UiState.Error -> {
                    sendBtn.isEnabled = true
                    errorText.isVisible = true
                    errorText.text = state.message
                    successText.isVisible = false
                }
                else -> sendBtn.isEnabled = true
            }
        }
    }

    private fun submitAnnouncement() {
        val message = messageInput.text?.toString()?.trim() ?: ""
        if (message.length < 5) {
            errorText.isVisible = true
            errorText.text = "Message must be at least 5 characters"
            successText.isVisible = false
            return
        }

        errorText.isVisible = false
        successText.isVisible = false

        val isAllSpaces = targetRadioGroup.checkedRadioButtonId == R.id.radioAll
        if (isAllSpaces) {
            viewModel.announceToAll(token, message)
        } else {
            val selectedPos = spaceSpinner.selectedItemPosition
            if (spaces.isEmpty() || selectedPos < 0) {
                errorText.isVisible = true
                errorText.text = "Please select a space"
                return
            }
            viewModel.announceToSpace(token, spaces[selectedPos].id, message)
        }
    }
}
