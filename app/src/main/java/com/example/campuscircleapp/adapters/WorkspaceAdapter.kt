package com.example.campuscircleapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.campuscircleapp.R
import com.example.campuscircleapp.features.home.models.SpaceResponse

class WorkspaceAdapter(
    private var items: List<SpaceResponse>,
    private val onEdit: (SpaceResponse) -> Unit,
    private val onDelete: (SpaceResponse) -> Unit,
    private val onToggle: (SpaceResponse) -> Unit
) : RecyclerView.Adapter<WorkspaceAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.workspaceName)
        val members: TextView = itemView.findViewById(R.id.workspaceMembers)
        val status: TextView = itemView.findViewById(R.id.workspaceStatus)
        val editBtn: ImageButton = itemView.findViewById(R.id.workspaceEditBtn)
        val deleteBtn: ImageButton = itemView.findViewById(R.id.workspaceDeleteBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_workspace, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val ctx = holder.itemView.context

        holder.name.text = item.name
        holder.members.text = "${item.totalUsers} members"
        holder.status.text = if (item.isActive) "Active" else "Inactive"
        holder.status.setBackgroundResource(
            if (item.isActive) R.drawable.bg_pill_success else R.drawable.bg_pill_error
        )
        holder.status.setTextColor(
            ContextCompat.getColor(
                ctx, if (item.isActive) R.color.color_success else R.color.color_error
            )
        )
        holder.status.setOnClickListener { onToggle(item) }
        holder.editBtn.setOnClickListener { onEdit(item) }
        holder.deleteBtn.setOnClickListener { onDelete(item) }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<SpaceResponse>) {
        items = newItems
        notifyDataSetChanged()
    }
}
