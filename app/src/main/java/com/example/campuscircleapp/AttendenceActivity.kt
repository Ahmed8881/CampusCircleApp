package com.example.campuscircleapp

import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.campuscircleapp.adapters.StudentAdapter
import com.example.campuscircleapp.models.Course
import com.example.campuscircleapp.models.Student

class AttendenceActivity : AppCompatActivity() {
    private lateinit var courseSpinner: Spinner
    private lateinit var studentRecyclerView: RecyclerView
    private lateinit var studentAdapter: StudentAdapter

    // Mock data for demonstration
    private val courses = listOf(
        Course("1", "Mathematics"),
        Course("2", "Physics"),
        Course("3", "Chemistry")
    )

    // Show all students for every course
    private val allStudents = listOf(
        Student("S001", "Ali Raza"),
        Student("S002", "Sara Khan"),
        Student("S003", "Ahmed Malik"),
        Student("S004", "Fatima Noor"),
        Student("S005", "Bilal Aslam"),
        Student("S006", "Hassan Tariq"),
        Student("S007", "Ayesha Iqbal")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_attendence)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        courseSpinner = findViewById(R.id.courseSpinner)
        studentRecyclerView = findViewById(R.id.studentRecyclerView)

        // Setup Spinner
        val courseNames = courses.map { it.name }
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, courseNames)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        courseSpinner.adapter = spinnerAdapter

        // Setup RecyclerView
        studentAdapter = StudentAdapter(emptyList())
        studentRecyclerView.layoutManager = LinearLayoutManager(this)
        studentRecyclerView.adapter = studentAdapter

        // Always show all students regardless of course
        studentAdapter.updateData(allStudents)

        courseSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                // No filtering, always show all students
                studentAdapter.updateData(allStudents)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }
}