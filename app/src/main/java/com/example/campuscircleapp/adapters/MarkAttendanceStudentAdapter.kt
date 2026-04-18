package com.example.campuscircleapp.adapters

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.campuscircleapp.R
import com.example.campuscircleapp.features.home.models.MarkAttendanceStudentUi
import com.google.android.material.button.MaterialButton

class MarkAttendanceStudentAdapter(
    private var students: List<MarkAttendanceStudentUi>
) : RecyclerView.Adapter<MarkAttendanceStudentAdapter.MarkAttendanceViewHolder>() {

    class MarkAttendanceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val studentName: TextView = itemView.findViewById(R.id.markStudentName)
        val rollNo: TextView = itemView.findViewById(R.id.markStudentRollNo)
        val presentBtn: MaterialButton = itemView.findViewById(R.id.btnPresent)
        val absentBtn: MaterialButton = itemView.findViewById(R.id.btnAbsent)
        val leaveBtn: MaterialButton = itemView.findViewById(R.id.btnLeave)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarkAttendanceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mark_attendance_student, parent, false)
        return MarkAttendanceViewHolder(view)
    }

    override fun onBindViewHolder(holder: MarkAttendanceViewHolder, position: Int) {
        val item = students[position]
        holder.studentName.text = item.studentName
        holder.rollNo.text = item.rollNo

        val status = item.status.lowercase()
        styleStatusButtons(holder, status)

        holder.presentBtn.setOnClickListener {
            item.status = "present"
            styleStatusButtons(holder, "present")
        }
        holder.absentBtn.setOnClickListener {
            item.status = "absent"
            styleStatusButtons(holder, "absent")
        }
        holder.leaveBtn.setOnClickListener {
            item.status = "leave"
            styleStatusButtons(holder, "leave")
        }
    }

    override fun getItemCount(): Int = students.size

    fun updateData(newItems: List<MarkAttendanceStudentUi>) {
        students = newItems
        notifyDataSetChanged()
    }

    fun getCurrentData(): List<MarkAttendanceStudentUi> = students

    private fun styleStatusButtons(holder: MarkAttendanceViewHolder, selectedStatus: String) {
        setButtonState(
            button = holder.presentBtn,
            isSelected = selectedStatus == "present",
            activeColor = "#16A34A",
            inactiveColor = "#DCFCE7",
            activeTextColor = "#FFFFFF",
            inactiveTextColor = "#166534"
        )
        setButtonState(
            button = holder.absentBtn,
            isSelected = selectedStatus == "absent",
            activeColor = "#DC2626",
            inactiveColor = "#FEE2E2",
            activeTextColor = "#FFFFFF",
            inactiveTextColor = "#991B1B"
        )
        setButtonState(
            button = holder.leaveBtn,
            isSelected = selectedStatus == "leave",
            activeColor = "#EAB308",
            inactiveColor = "#FEF9C3",
            activeTextColor = "#FFFFFF",
            inactiveTextColor = "#854D0E"
        )
    }

    private fun setButtonState(
        button: MaterialButton,
        isSelected: Boolean,
        activeColor: String,
        inactiveColor: String,
        activeTextColor: String,
        inactiveTextColor: String
    ) {
        val bg = if (isSelected) activeColor else inactiveColor
        val text = if (isSelected) activeTextColor else inactiveTextColor
        button.backgroundTintList = ColorStateList.valueOf(Color.parseColor(bg))
        button.setTextColor(Color.parseColor(text))
    }
}
