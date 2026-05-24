package com.example.campuscircleapp.features.admin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.campuscircleapp.R
import com.google.android.material.card.MaterialCardView

class AdminManagementFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_admin_management, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<MaterialCardView>(R.id.cardCreateWorkspace).setOnClickListener {
            navigate(CreateWorkspaceFragment())
        }
        view.findViewById<MaterialCardView>(R.id.cardCreateSemester).setOnClickListener {
            navigate(CreateSemesterFragment())
        }
        view.findViewById<MaterialCardView>(R.id.cardCreateInstructor).setOnClickListener {
            navigate(CreateInstructorsFragment())
        }
        view.findViewById<MaterialCardView>(R.id.cardTimetable).setOnClickListener {
            navigate(AdminTimetableFragment())
        }
        view.findViewById<MaterialCardView>(R.id.cardAssignments).setOnClickListener {
            navigate(AdminAssignmentsFragment())
        }
    }

    private fun navigate(fragment: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.adminFragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }
}
