package com.example.campuscircleapp.features.admin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.campuscircleapp.R
import com.example.campuscircleapp.adapters.PendingEnrollmentAdapter
import com.example.campuscircleapp.core.models.UiState
import com.example.campuscircleapp.features.home.viewModels.ReviewEnrollmentViewModel
import com.example.campuscircleapp.shared.services.SessionManager
import com.google.android.material.progressindicator.CircularProgressIndicator

class ReviewEnrollmentFragment : Fragment() {

    private val viewModel by lazy {
        ViewModelProvider(this)[ReviewEnrollmentViewModel::class.java]
    }

    private lateinit var loadingView: CircularProgressIndicator
    private lateinit var errorView: TextView
    private lateinit var emptyView: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PendingEnrollmentAdapter

    private var token: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_review_enrollment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingView = view.findViewById(R.id.enrollmentLoading)
        errorView = view.findViewById(R.id.enrollmentError)
        emptyView = view.findViewById(R.id.enrollmentEmpty)
        recyclerView = view.findViewById(R.id.enrollmentRecycler)

        val sessionToken = SessionManager.getToken(requireContext())
        if (sessionToken.isNullOrBlank()) {
            showError("Session expired. Please sign in again.")
            return
        }
        token = sessionToken

        adapter = PendingEnrollmentAdapter(
            emptyList(),
            onApprove = { enrollmentId -> reviewEnrollment(enrollmentId, true) },
            onReject = { enrollmentId -> reviewEnrollment(enrollmentId, false) }
        )
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        observeStates()
        viewModel.loadPendingEnrollments(token)
    }

    private fun observeStates() {
        viewModel.enrollmentsState.asLiveData().observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Idle -> Unit
                is UiState.Loading -> {
                    loadingView.isVisible = true
                    errorView.isVisible = false
                    recyclerView.isVisible = false
                    emptyView.isVisible = false
                }
                is UiState.Success -> {
                    loadingView.isVisible = false
                    errorView.isVisible = false
                    if (state.data.isEmpty()) {
                        emptyView.isVisible = true
                        recyclerView.isVisible = false
                    } else {
                        emptyView.isVisible = false
                        recyclerView.isVisible = true
                        adapter.updateData(state.data)
                    }
                }
                is UiState.Error -> showError(state.message)
                else -> {}
            }
        }

        viewModel.reviewState.asLiveData().observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Success -> {
                    viewModel.resetReviewState()
                    viewModel.loadPendingEnrollments(token)
                }
                is UiState.Error -> showError(state.message)
                else -> {}
            }
        }
    }

    private fun reviewEnrollment(enrollmentId: Long, approve: Boolean) {
        val adminName = SessionManager.getName(requireContext()) ?: "Admin"
        viewModel.reviewEnrollment(token, enrollmentId, approve, adminName)
    }

    private fun showError(message: String) {
        loadingView.isVisible = false
        recyclerView.isVisible = false
        emptyView.isVisible = false
        errorView.isVisible = true
        errorView.text = message
    }
}
