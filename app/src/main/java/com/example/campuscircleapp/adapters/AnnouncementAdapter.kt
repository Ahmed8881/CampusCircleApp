package com.example.campuscircleapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.campuscircleapp.R
import com.example.campuscircleapp.features.notifications.models.NotificationModel
import com.google.android.material.button.MaterialButton

class AnnouncementAdapter(
    private var items: List<NotificationModel>,
    private val onMarkAsRead: (NotificationModel) -> Unit
) : RecyclerView.Adapter<AnnouncementAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageText: TextView = itemView.findViewById(R.id.announcementMessage)
        val spaceText: TextView = itemView.findViewById(R.id.announcementSpace)
        val dateText: TextView = itemView.findViewById(R.id.announcementDate)
        val btnMarkRead: MaterialButton = itemView.findViewById(R.id.btnMarkRead)
        val unreadIndicator: View = itemView.findViewById(R.id.unreadIndicator)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_announcement, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.messageText.text = item.body
        holder.spaceText.text = item.title ?: "General"
        holder.dateText.text = item.createdAt?.take(10) ?: ""

        // Update visibility based on read status
        holder.unreadIndicator.isVisible = !item.isRead
        holder.btnMarkRead.isVisible = !item.isRead

        holder.btnMarkRead.setOnClickListener {
            onMarkAsRead(item)
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<NotificationModel>) {
        items = newItems
        notifyDataSetChanged()
    }
}
