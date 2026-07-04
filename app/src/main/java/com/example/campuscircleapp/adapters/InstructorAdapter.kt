package com.example.campuscircleapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.campuscircleapp.R
import com.example.campuscircleapp.features.admin.models.InstructorResponse

class InstructorAdapter(
    private var items: List<InstructorResponse>,
    private val onEdit: (InstructorResponse) -> Unit,
    private val onDelete: (InstructorResponse) -> Unit
) : RecyclerView.Adapter<InstructorAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.instructorName)
        val email: TextView = itemView.findViewById(R.id.instructorEmail)
        val avatar: TextView = itemView.findViewById(R.id.instructorAvatar)
        val editBtn: ImageButton = itemView.findViewById(R.id.instructorEditBtn)
        val deleteBtn: ImageButton = itemView.findViewById(R.id.instructorDeleteBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_instructor, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.name.text = item.name
        holder.email.text = item.email ?: ""
        holder.avatar.text = item.name.firstOrNull()?.uppercase() ?: "I"
        holder.editBtn.setOnClickListener { onEdit(item) }
        holder.deleteBtn.setOnClickListener { onDelete(item) }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<InstructorResponse>) {
        items = newItems
        notifyDataSetChanged()
    }
}
