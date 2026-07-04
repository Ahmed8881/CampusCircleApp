package com.example.campuscircleapp.features.admin.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.example.campuscircleapp.R
import com.example.campuscircleapp.core.models.UiState
import com.example.campuscircleapp.features.admin.models.InstructorResponse
import com.example.campuscircleapp.features.admin.models.SemesterResponse
import com.example.campuscircleapp.features.admin.models.StudentResponse
import com.example.campuscircleapp.features.admin.viewModels.AdminAssignmentsViewModel
import com.example.campuscircleapp.features.home.models.SpaceResponse
import com.example.campuscircleapp.shared.services.SessionManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

class AdminAssignmentsFragment : Fragment() {

    private lateinit var viewModel: AdminAssignmentsViewModel
    private lateinit var loadingIndicator: LinearProgressIndicator

    private lateinit var searchInput: TextInputEditText
    private lateinit var instructorsContainer: LinearLayout
    private lateinit var studentsContainer: LinearLayout
    private lateinit var spacesContainer: LinearLayout
    private lateinit var instructorsEmptyView: TextView
    private lateinit var studentsEmptyView: TextView
    private lateinit var spacesEmptyView: TextView
    private lateinit var instructorsCount: TextView
    private lateinit var studentsCount: TextView
    private lateinit var spacesCount: TextView

    private var instructors: List<InstructorResponse> = emptyList()
    private var students: List<StudentResponse> = emptyList()
    private var spaces: List<SpaceResponse> = emptyList()
    private var semesters: List<SemesterResponse> = emptyList()

    private var token: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_admin_assignments, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[AdminAssignmentsViewModel::class.java]

        loadingIndicator = view.findViewById(R.id.loadingIndicator)
        searchInput = view.findViewById(R.id.searchInput)
        instructorsContainer = view.findViewById(R.id.instructorsContainer)
        studentsContainer = view.findViewById(R.id.studentsContainer)
        spacesContainer = view.findViewById(R.id.spacesContainer)
        instructorsEmptyView = view.findViewById(R.id.instructorsEmptyView)
        studentsEmptyView = view.findViewById(R.id.studentsEmptyView)
        spacesEmptyView = view.findViewById(R.id.spacesEmptyView)
        instructorsCount = view.findViewById(R.id.instructorsCount)
        studentsCount = view.findViewById(R.id.studentsCount)
        spacesCount = view.findViewById(R.id.spacesCount)

        val btnAssignSemester = view.findViewById<MaterialButton>(R.id.btnAssignSemester)
        val btnAssignInstructor = view.findViewById<MaterialButton>(R.id.btnAssignInstructor)
        val btnAssignCR = view.findViewById<MaterialButton>(R.id.btnAssignCR)

        val sessionToken = SessionManager.getToken(requireContext())
        if (sessionToken.isNullOrBlank()) return
        token = sessionToken

        observeStates()
        viewModel.loadAll(token)

        btnAssignSemester.setOnClickListener { showAssignSemesterDialog() }
        btnAssignInstructor.setOnClickListener { showAssignInstructorDialog() }
        btnAssignCR.setOnClickListener { showAssignCRDialog() }

        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                filter(s?.toString() ?: "")
            }
        })
    }

    private fun observeStates() {
        viewModel.spacesState.asLiveData().observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Success -> {
                    spaces = state.data
                    renderSpaces()
                }
                is UiState.Error -> showToast("Error", state.message, MotionToastStyle.ERROR)
                else -> {}
            }
        }

        viewModel.semestersState.asLiveData().observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Success -> semesters = state.data
                is UiState.Error -> showToast("Error", state.message, MotionToastStyle.ERROR)
                else -> {}
            }
        }

        viewModel.instructorsState.asLiveData().observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Success -> {
                    instructors = state.data
                    filter(searchInput.text.toString())
                    renderInstructors(instructors)
                }
                is UiState.Error -> showToast("Error", state.message, MotionToastStyle.ERROR)
                else -> {}
            }
        }

        viewModel.studentsState.asLiveData().observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Success -> {
                    students = state.data
                    filter(searchInput.text.toString())
                    renderStudents(students)
                }
                is UiState.Error -> showToast("Error", state.message, MotionToastStyle.ERROR)
                else -> {}
            }
        }

        viewModel.assignSemesterState.asLiveData().observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> loadingIndicator.isVisible = true
                is UiState.Success -> {
                    loadingIndicator.isVisible = false
                    showToast("Success", state.message ?: "Semester assigned", MotionToastStyle.SUCCESS)
                    viewModel.resetAssignSemesterState()
                    viewModel.loadAll(token)
                }
                is UiState.Error -> {
                    loadingIndicator.isVisible = false
                    showToast("Error", state.message, MotionToastStyle.ERROR)
                    viewModel.resetAssignSemesterState()
                }
                else -> {}
            }
        }

        viewModel.assignInstructorState.asLiveData().observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> loadingIndicator.isVisible = true
                is UiState.Success -> {
                    loadingIndicator.isVisible = false
                    showToast("Success", state.message ?: "Instructor assigned", MotionToastStyle.SUCCESS)
                    viewModel.resetAssignInstructorState()
                    viewModel.loadAll(token)
                }
                is UiState.Error -> {
                    loadingIndicator.isVisible = false
                    showToast("Error", state.message, MotionToastStyle.ERROR)
                    viewModel.resetAssignInstructorState()
                }
                else -> {}
            }
        }

        viewModel.assignCRState.asLiveData().observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> loadingIndicator.isVisible = true
                is UiState.Success -> {
                    loadingIndicator.isVisible = false
                    showToast("Success", state.message ?: "CR assigned", MotionToastStyle.SUCCESS)
                    viewModel.resetAssignCRState()
                    viewModel.loadAll(token)
                }
                is UiState.Error -> {
                    loadingIndicator.isVisible = false
                    showToast("Error", state.message, MotionToastStyle.ERROR)
                    viewModel.resetAssignCRState()
                }
                else -> {}
            }
        }
    }

    private fun filter(query: String) {
        val q = query.lowercase()
        val filteredInstructors = if (q.isBlank()) instructors
            else instructors.filter { it.name.lowercase().contains(q) || (it.email ?: "").lowercase().contains(q) }
        val filteredStudents = if (q.isBlank()) students
            else students.filter { it.name.lowercase().contains(q) || (it.username ?: "").lowercase().contains(q) }

        renderInstructors(filteredInstructors)
        renderStudents(filteredStudents)
    }

    private fun renderInstructors(list: List<InstructorResponse>) {
        instructorsContainer.removeAllViews()
        instructorsCount.text = "${list.size}"
        if (list.isEmpty()) {
            instructorsEmptyView.isVisible = true
            instructorsContainer.isVisible = false
            return
        }
        instructorsEmptyView.isVisible = false
        instructorsContainer.isVisible = true
        for (inst in list) {
            instructorsContainer.addView(createInstructorCard(inst))
        }
    }

    private fun renderStudents(list: List<StudentResponse>) {
        studentsContainer.removeAllViews()
        studentsCount.text = "${list.size}"
        if (list.isEmpty()) {
            studentsEmptyView.isVisible = true
            studentsContainer.isVisible = false
            return
        }
        studentsEmptyView.isVisible = false
        studentsContainer.isVisible = true
        for (stu in list) {
            studentsContainer.addView(createStudentCard(stu))
        }
    }

    private fun renderSpaces() {
        spacesContainer.removeAllViews()
        spacesCount.text = "${spaces.size}"
        if (spaces.isEmpty()) {
            spacesEmptyView.isVisible = true
            spacesContainer.isVisible = false
            return
        }
        spacesEmptyView.isVisible = false
        spacesContainer.isVisible = true
        for (space in spaces) {
            spacesContainer.addView(createSpaceCard(space))
        }
    }

    private fun createInstructorCard(inst: InstructorResponse): View {
        val card = layoutInflater.inflate(R.layout.item_assignment_instructor, instructorsContainer, false)
        card.findViewById<TextView>(R.id.instName).text = inst.name
        card.findViewById<TextView>(R.id.instEmail).text = inst.email ?: ""
        card.findViewById<MaterialButton>(R.id.instAssignBtn).setOnClickListener { showAssignInstructorDialog(inst) }
        return card
    }

    private fun createStudentCard(stu: StudentResponse): View {
        val card = layoutInflater.inflate(R.layout.item_assignment_student, studentsContainer, false)
        card.findViewById<TextView>(R.id.stuName).text = stu.name
        card.findViewById<TextView>(R.id.stuUsername).text = stu.username ?: ""
        card.findViewById<MaterialButton>(R.id.stuCrBtn).setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Make Class Representative")
                .setMessage("Are you sure you want to make ${stu.name} the class representative?")
                .setPositiveButton("Yes") { _, _ ->
                    viewModel.performAssignCR(token, stu.username ?: "")
                }
                .setNegativeButton("No", null)
                .show()
        }
        return card
    }

    private fun createSpaceCard(space: SpaceResponse): View {
        val card = layoutInflater.inflate(R.layout.item_assignment_space, spacesContainer, false)
        card.findViewById<TextView>(R.id.spaceName).text = space.name
        card.findViewById<MaterialButton>(R.id.spaceAssignSemesterBtn).setOnClickListener { showAssignSemesterDialog(space) }
        return card
    }

    private fun showAssignSemesterDialog(space: SpaceResponse? = null) {
        val builder = AlertDialog.Builder(requireContext())
        val dialogView = layoutInflater.inflate(R.layout.dialog_assign_semester, null)
        builder.setView(dialogView)
        builder.setTitle("Assign Semester")
        builder.setPositiveButton("Assign") { _, _ -> }
        builder.setNegativeButton("Cancel", null)
        val dialog = builder.create()
        dialog.show()

        val spaceDropdown = dialogView.findViewById<AutoCompleteTextView>(R.id.spaceDropdown)
        val semesterDropdown = dialogView.findViewById<AutoCompleteTextView>(R.id.semesterDropdown)

        val spaceNames = spaces.map { it.name }.toTypedArray()
        val semesterLabels = semesters.map { "${it.number} - ${it.name}" }.toTypedArray()

        spaceDropdown.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, spaceNames))
        semesterDropdown.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, semesterLabels))

        if (space != null) {
            spaceDropdown.setText(space.name, false)
        }

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val spaceText = spaceDropdown.text.toString()
            val semText = semesterDropdown.text.toString()
            val spaceIdx = spaceNames.indexOf(spaceText)
            val semIdx = semesterLabels.indexOf(semText)
            if (spaceIdx < 0 || semIdx < 0) {
                showToast("Error", "Please select both space and semester", MotionToastStyle.WARNING)
                return@setOnClickListener
            }
            viewModel.performAssignSemester(token, spaces[spaceIdx].id, semesters[semIdx].id)
            dialog.dismiss()
        }
    }

    private fun showAssignInstructorDialog(inst: InstructorResponse? = null) {
        val builder = AlertDialog.Builder(requireContext())
        val dialogView = layoutInflater.inflate(R.layout.dialog_assign_instructor, null)
        builder.setView(dialogView)
        builder.setTitle("Assign Instructor to Space")
        builder.setPositiveButton("Assign") { _, _ -> }
        builder.setNegativeButton("Cancel", null)
        val dialog = builder.create()
        dialog.show()

        val instructorDropdown = dialogView.findViewById<AutoCompleteTextView>(R.id.instructorDropdown)
        val spaceDropdown = dialogView.findViewById<AutoCompleteTextView>(R.id.spaceDropdown)

        val instructorLabels = instructors.map { "${it.name} (${it.email ?: ""})" }.toTypedArray()
        val spaceNames = spaces.map { it.name }.toTypedArray()

        instructorDropdown.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, instructorLabels))
        spaceDropdown.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, spaceNames))

        if (inst != null) {
            instructorDropdown.setText("${inst.name} (${inst.email ?: ""})", false)
        }

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val instText = instructorDropdown.text.toString()
            val spaceText = spaceDropdown.text.toString()
            val instIdx = instructorLabels.indexOf(instText)
            val spaceIdx = spaceNames.indexOf(spaceText)
            if (instIdx < 0 || spaceIdx < 0) {
                showToast("Error", "Please select both instructor and space", MotionToastStyle.WARNING)
                return@setOnClickListener
            }
            viewModel.performAssignInstructor(token, instructors[instIdx].id, spaces[spaceIdx].id)
            dialog.dismiss()
        }
    }

    private fun showAssignCRDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val dialogView = layoutInflater.inflate(R.layout.dialog_assign_cr, null)
        builder.setView(dialogView)
        builder.setTitle("Assign CR")
        builder.setPositiveButton("Assign") { _, _ -> }
        builder.setNegativeButton("Cancel", null)
        val dialog = builder.create()
        dialog.show()

        val studentInput = dialogView.findViewById<AutoCompleteTextView>(R.id.studentDropdown)
        val studentLabels = students.map { s ->
            val un = s.username?.takeIf { it.isNotBlank() }
            if (un != null) "${s.name} (@$un)" else s.name
        }.toTypedArray()
        studentInput.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, studentLabels))
        studentInput.threshold = 1

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val text = studentInput.text.toString()
            val idx = studentLabels.indexOf(text)
            val userName: String = if (idx >= 0) (students[idx].username ?: "") else text.trim()
            if (userName.isBlank()) {
                showToast("Error", "Please select or enter a username", MotionToastStyle.WARNING)
                return@setOnClickListener
            }
            viewModel.performAssignCR(token, userName)
            dialog.dismiss()
        }
    }

    private fun showToast(title: String, message: String, style: MotionToastStyle) {
        MotionToast.createColorToast(requireActivity(), title, message, style,
            MotionToast.GRAVITY_TOP, MotionToast.LONG_DURATION,
            ResourcesCompat.getFont(requireContext(), www.sanju.motiontoast.R.font.helvetica_regular))
    }
}
