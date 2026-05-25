package com.example.campuscircleapp.features.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.campuscircleapp.databinding.FragmentNotificationDetailBinding

class NotificationDetailFragment : Fragment() {

    private var _binding: FragmentNotificationDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val title = arguments?.getString("title")
        val body = arguments?.getString("body")
        val status = arguments?.getString("attendanceStatus")
        val date = arguments?.getString("attendanceDate")
        val courseId = arguments?.getString("courseId")

        binding.tvTitle.text = title
        binding.tvBody.text = body
        binding.tvStatus.text = "Status: ${status ?: "N/A"}"
        binding.tvDate.text = "Date: ${date ?: "N/A"}"

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(data: Bundle): NotificationDetailFragment {
            val fragment = NotificationDetailFragment()
            fragment.arguments = data
            return fragment
        }
    }
}
