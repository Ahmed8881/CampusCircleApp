package com.example.campuscircleapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.campuscircleapp.R
import com.example.campuscircleapp.features.admin.models.InstructorResponse

class InstructorAdapter(private val items: List<InstructorResponse>) :
    RecyclerView.Adapter<InstructorAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.instructorName)
        val email: TextView = view.findViewById(R.id.instructorEmail)
        val username: TextView = view.findViewById(R.id.instructorUsername)
        val avatar: TextView = view.findViewById(R.id.instructorAvatar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_instructor, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.name.text = item.name
        holder.email.text = item.email
        holder.username.text = "@${item.username}"
        holder.avatar.text = item.name.firstOrNull()?.uppercase() ?: "I"
    }
}
