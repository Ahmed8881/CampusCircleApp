package com.example.campuscircleapp.features.notifications.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.campuscircleapp.databinding.ItemNotificationBinding
import com.example.campuscircleapp.features.notifications.models.NotificationModel

class NotificationAdapter(
    private val onNotificationClick: (NotificationModel) -> Unit
) : ListAdapter<NotificationModel, NotificationAdapter.NotificationViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding = ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class NotificationViewHolder(private val binding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(notification: NotificationModel) {
            binding.tvTitle.text = notification.title
            binding.tvBody.text = notification.body
            binding.tvTime.text = notification.createdAt // You might want to format this date string

            // Show indicator only if unread
            binding.viewUnreadIndicator.visibility = if (notification.isRead) View.GONE else View.VISIBLE

            binding.root.setOnClickListener {
                onNotificationClick(notification)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<NotificationModel>() {
        override fun areItemsTheSame(oldItem: NotificationModel, newItem: NotificationModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: NotificationModel, newItem: NotificationModel): Boolean {
            return oldItem == newItem
        }
    }
}
