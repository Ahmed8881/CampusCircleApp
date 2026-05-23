package com.example.campuscircleapp.features.admin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.campuscircleapp.R
import com.example.campuscircleapp.adapters.AssignmentAdapter
import com.example.campuscircleapp.core.models.UiState
import com.example.campuscircleapp.features.admin.viewModels.AdminAssignmentsViewModel
import com.example.campuscircleapp.shared.services.SessionManager

class AdminAssignmentsFragment : Fragment() {

    private lateinit var viewModel: AdminAssignmentsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_admin_assignments, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[AdminAssignmentsViewModel::class.java]

        val recycler = view.findViewById<RecyclerView>(R.id.assignmentsRecyclerView)
        val emptyView = view.findViewById<TextView>(R.id.assignmentsEmptyView)
        val loadingView = view.findViewById<View>(R.id.assignmentsLoadingView)

        recycler.layoutManager = LinearLayoutManager(requireContext())

        val token = SessionManager.getToken(requireContext()) ?: return

        viewModel.assignmentsState.asLiveData().observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> { loadingView.visibility = View.VISIBLE; recycler.visibility = View.GONE; emptyView.visibility = View.GONE }
                is UiState.Success -> {
                    loadingView.visibility = View.GONE
                    if (state.data.isEmpty()) { recycler.visibility = View.GONE; emptyView.visibility = View.VISIBLE }
                    else { recycler.visibility = View.VISIBLE; emptyView.visibility = View.GONE; recycler.adapter = AssignmentAdapter(state.data) }
                }
                is UiState.Error -> { loadingView.visibility = View.GONE; emptyView.visibility = View.VISIBLE; emptyView.text = state.message }
                else -> {}
            }
        }

        viewModel.load(token)
    }
}
