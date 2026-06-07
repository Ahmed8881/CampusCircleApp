package com.example.campuscircleapp.features.home.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.campuscircleapp.R
import com.example.campuscircleapp.adapters.AnnouncementAdapter
import com.example.campuscircleapp.core.models.UiState
import com.example.campuscircleapp.features.home.viewModels.AnnouncementsViewModel
import com.example.campuscircleapp.shared.services.SessionManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

class AnnouncementsFragment : Fragment() {

    private val viewModel by lazy {
        ViewModelProvider(this)[AnnouncementsViewModel::class.java]
    }

    private lateinit var loadingView: CircularProgressIndicator
    private lateinit var errorView: TextView
    private lateinit var emptyView: View
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnClearAll: MaterialButton
    private lateinit var adapter: AnnouncementAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_announcements, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingView = view.findViewById(R.id.announcementsLoading)
        errorView = view.findViewById(R.id.announcementsError)
        emptyView = view.findViewById(R.id.announcementsEmpty)
        recyclerView = view.findViewById(R.id.announcementsRecycler)
        btnClearAll = view.findViewById(R.id.btnClearAll)

        val token = SessionManager.getToken(requireContext()) ?: ""

        adapter = AnnouncementAdapter(emptyList()) { notification ->
            if (token.isNotEmpty()) {
                viewModel.markAsRead(token, notification.id)
            }
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        btnClearAll.setOnClickListener {
            if (token.isNotEmpty()) {
                viewModel.clearAll(token)
            }
        }

        observeState()

        if (token.isBlank()) {
            showError("Session expired. Please sign in again.")
            return
        }

        viewModel.loadAnnouncements(token)
    }

    private fun observeState() {
        viewModel.announcementsState.asLiveData().observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Idle -> Unit
                is UiState.Loading -> {
                    loadingView.isVisible = true
                    errorView.isVisible = false
                    recyclerView.isVisible = false
                    emptyView.isVisible = false
                    btnClearAll.isVisible = false
                }
                is UiState.Success -> {
                    loadingView.isVisible = false
                    errorView.isVisible = false
                    if (state.data.isEmpty()) {
                        emptyView.isVisible = true
                        recyclerView.isVisible = false
                        btnClearAll.isVisible = false
                    } else {
                        emptyView.isVisible = false
                        recyclerView.isVisible = true
                        btnClearAll.isVisible = true
                        adapter.updateData(state.data)
                    }
                }
                is UiState.Error -> showError(state.message)
                else -> {}
            }
        }

        viewModel.actionState.asLiveData().observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Success -> {
                    MotionToast.createColorToast(
                        requireActivity(),
                        "Success",
                        state.message ?: "Action completed",
                        MotionToastStyle.SUCCESS,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.SHORT_DURATION,
                        ResourcesCompat.getFont(requireContext(), www.sanju.motiontoast.R.font.helvetica_regular)
                    )
                    viewModel.resetActionState()
                }
                is UiState.Error -> {
                    MotionToast.createColorToast(
                        requireActivity(),
                        "Error",
                        state.message,
                        MotionToastStyle.ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.SHORT_DURATION,
                        ResourcesCompat.getFont(requireContext(), www.sanju.motiontoast.R.font.helvetica_regular)
                    )
                    viewModel.resetActionState()
                }
                else -> {}
            }
        }
    }

    private fun showError(message: String) {
        loadingView.isVisible = false
        recyclerView.isVisible = false
        emptyView.isVisible = false
        errorView.isVisible = true
        errorView.text = message
        btnClearAll.isVisible = false
    }
}
