package com.example.campuscircleapp.features.home.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.campuscircleapp.R
import com.example.campuscircleapp.adapters.StudentAdapter
import com.example.campuscircleapp.models.Course
import com.example.campuscircleapp.models.Student

class AttendanceFragment : Fragment() {

    private lateinit var courseSpinner: Spinner
    private lateinit var studentRecyclerView: RecyclerView
    private lateinit var studentAdapter: StudentAdapter

    private val courses = listOf(
        Course("1", "Mathematics"),
        Course("2", "Physics"),
        Course("3", "Chemistry")
    )

    private val allStudents = listOf(
        Student("S001", "Ali Raza"),
        Student("S002", "Sara Khan"),
        Student("S003", "Ahmed Malik"),
        Student("S004", "Fatima Noor"),
        Student("S005", "Bilal Aslam"),
        Student("S006", "Hassan Tariq"),
        Student("S007", "Ayesha Iqbal")
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_attendance, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        courseSpinner = view.findViewById(R.id.courseSpinner)
        studentRecyclerView = view.findViewById(R.id.studentRecyclerView)

        val courseNames = courses.map { it.name }
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, courseNames)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        courseSpinner.adapter = spinnerAdapter

        studentAdapter = StudentAdapter(emptyList())
        studentRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        studentRecyclerView.adapter = studentAdapter

        studentAdapter.updateData(allStudents)

        courseSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                studentAdapter.updateData(allStudents)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }
}