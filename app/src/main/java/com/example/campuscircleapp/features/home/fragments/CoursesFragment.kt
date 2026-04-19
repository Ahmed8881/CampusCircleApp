package com.example.campuscircleapp.features.home.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.campuscircleapp.R
import com.example.campuscircleapp.adapters.CourseAdapter
import com.example.campuscircleapp.models.Course

class CoursesFragment : Fragment() {

    private val courses = listOf(
        Course("1", "Mathematics"),
        Course("2", "Physics"),
        Course("3", "Chemistry")
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_courses, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val coursesRecyclerView = view.findViewById<RecyclerView>(R.id.coursesRecyclerView)
        coursesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        coursesRecyclerView.adapter = CourseAdapter(courses)
    }
}