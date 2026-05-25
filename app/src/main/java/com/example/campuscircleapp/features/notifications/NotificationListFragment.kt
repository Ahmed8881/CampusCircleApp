package com.example.campuscircleapp.features.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.campuscircleapp.R
import com.example.campuscircleapp.databinding.FragmentNotificationListBinding
import com.example.campuscircleapp.features.notifications.adapters.NotificationAdapter
import com.example.campuscircleapp.features.notifications.models.NotificationModel
import com.example.campuscircleapp.features.notifications.viewmodels.NotificationViewModel
import com.example.campuscircleapp.shared.services.SessionManager

class NotificationListFragment : Fragment() {

    private var _binding: FragmentNotificationListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NotificationViewModel by viewModels()
    private lateinit var adapter: NotificationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
        setupSwipeRefresh()

        fetchNotifications()
    }

    private fun setupRecyclerView() {
        adapter = NotificationAdapter { notification ->
            handleNotificationClick(notification)
        }
        binding.rvNotifications.layoutManager = LinearLayoutManager(requireContext())
        binding.rvNotifications.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.notifications.observe(viewLifecycleOwner) { notifications ->
            adapter.submitList(notifications)
            binding.emptyState.visibility = if (notifications.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.swipeRefresh.isRefreshing = isLoading
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            fetchNotifications()
        }
    }

    private fun fetchNotifications() {
        val token = SessionManager.getToken(requireContext())
        if (token != null) {
            viewModel.fetchNotifications(token)
        } else {
            Toast.makeText(requireContext(), "Session expired. Please login again.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleNotificationClick(notification: NotificationModel) {
        val token = SessionManager.getToken(requireContext())
        if (token != null && !notification.isRead) {
            viewModel.markAsRead(token, notification)
        }

        // Navigate to detail view
        val bundle = Bundle().apply {
            putString("title", notification.title)
            putString("body", notification.body)
            putString("type", notification.type)
            putString("attendanceStatus", "N/A") // From API list, we might not have all extra data
            putString("attendanceDate", notification.createdAt)
            putLong("id", notification.id)
        }

        val detailFragment = NotificationDetailFragment.newInstance(bundle)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, detailFragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
