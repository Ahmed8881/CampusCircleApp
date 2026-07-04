package com.example.campuscircleapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.campuscircleapp.R
import com.example.campuscircleapp.core.theme.ThemeManager
import com.example.campuscircleapp.features.home.models.StudentEnrollmentResponse

class EnrolledCourseAdapter(private val items: List<StudentEnrollmentResponse>) :
    RecyclerView.Adapter<EnrolledCourseAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val courseName: TextView = view.findViewById(R.id.enrolledCourseName)
        val semesterName: TextView = view.findViewById(R.id.enrolledSemesterName)
        val attendanceText: TextView = view.findViewById(R.id.enrolledAttendanceText)
        val progressFill: View = view.findViewById(R.id.enrolledProgressFill)
        val statusChip: TextView = view.findViewById(R.id.enrolledStatusChip)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_enrolled_course, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.courseName.text = item.courseName
        holder.semesterName.text = "${item.semesterName} • ${item.spaceName}"

        val pct = if (item.totalClasses > 0) (item.attended * 100 / item.totalClasses) else 0
        holder.attendanceText.text = "$pct%  (${item.attended}/${item.totalClasses} classes)"

        val params = holder.progressFill.layoutParams
        holder.progressFill.post {
            val parent = holder.progressFill.parent as View
            params.width = (parent.width * pct / 100).coerceAtLeast(0)
            holder.progressFill.layoutParams = params
        }

        holder.statusChip.text = item.status
        val ctx = holder.itemView.context
        val chipColor = when (item.status.lowercase()) {
            "approved" -> ThemeManager.resolveColorCompat(ctx, R.attr.colorSuccess)
            "pending" -> ThemeManager.resolveColorCompat(ctx, R.attr.colorWarning)
            else -> ThemeManager.resolveColorCompat(ctx, R.attr.colorError)
        }
        holder.statusChip.setBackgroundColor(chipColor)
    }
}
