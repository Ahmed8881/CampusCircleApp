package com.example.campuscircleapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.campuscircleapp.R
import com.example.campuscircleapp.features.home.models.PendingEnrollmentResponse
import com.google.android.material.button.MaterialButton

class PendingEnrollmentAdapter(
    private var items: List<PendingEnrollmentResponse>,
    private val onApprove: (Long) -> Unit,
    private val onReject: (Long) -> Unit
) : RecyclerView.Adapter<PendingEnrollmentAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val studentName: TextView = itemView.findViewById(R.id.enrollmentStudentName)
        val courseName: TextView = itemView.findViewById(R.id.enrollmentCourseName)
        val courseCode: TextView = itemView.findViewById(R.id.enrollmentCourseCode)
        val requestedAt: TextView = itemView.findViewById(R.id.enrollmentRequestedAt)
        val approveBtn: MaterialButton = itemView.findViewById(R.id.enrollmentApproveBtn)
        val rejectBtn: MaterialButton = itemView.findViewById(R.id.enrollmentRejectBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pending_enrollment, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.studentName.text = item.studentName
        holder.courseName.text = item.courseName
        holder.courseCode.text = item.courseCode
        holder.requestedAt.text = item.requestedAt?.take(10) ?: "—"
        holder.approveBtn.setOnClickListener { onApprove(item.enrollmentId) }
        holder.rejectBtn.setOnClickListener { onReject(item.enrollmentId) }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<PendingEnrollmentResponse>) {
        items = newItems
        notifyDataSetChanged()
    }
}
