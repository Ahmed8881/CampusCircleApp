package com.example.campuscircleapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.campuscircleapp.R
import com.example.campuscircleapp.features.home.models.AnnouncementItem

class AnnouncementAdapter(
    private var items: List<AnnouncementItem>
) : RecyclerView.Adapter<AnnouncementAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageText: TextView = itemView.findViewById(R.id.announcementMessage)
        val spaceText: TextView = itemView.findViewById(R.id.announcementSpace)
        val dateText: TextView = itemView.findViewById(R.id.announcementDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_announcement, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.messageText.text = item.message
        holder.spaceText.text = item.spaceName ?: "All Spaces"
        holder.dateText.text = item.sentAt?.take(10) ?: ""
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<AnnouncementItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}
