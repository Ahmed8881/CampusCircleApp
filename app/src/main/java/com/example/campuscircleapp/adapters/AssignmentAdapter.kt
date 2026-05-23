package com.example.campuscircleapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.campuscircleapp.R
import com.example.campuscircleapp.features.admin.models.AssignmentResponse

class AssignmentAdapter(private val items: List<AssignmentResponse>) :
    RecyclerView.Adapter<AssignmentAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.assignmentTitle)
        val type: TextView = view.findViewById(R.id.assignmentType)
        val course: TextView = view.findViewById(R.id.assignmentCourse)
        val dueDate: TextView = view.findViewById(R.id.assignmentDueDate)
        val marks: TextView = view.findViewById(R.id.assignmentMarks)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_assignment, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.title.text = item.title
        holder.type.text = item.type
        holder.course.text = item.courseName ?: "All Courses"
        holder.dueDate.text = "Due: ${item.dueDate}"
        holder.marks.text = "${item.totalMarks} marks"

        val typeColor = when (item.type.lowercase()) {
            "quiz" -> 0xFF8B5CF6.toInt()
            "exam" -> 0xFFEF4444.toInt()
            else -> 0xFF3B82F6.toInt()
        }
        holder.type.setBackgroundColor(typeColor)
    }
}
