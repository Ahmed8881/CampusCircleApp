package com.example.campuscircleapp.features.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.campuscircleapp.R

class OnboardingPageFragment : Fragment() {

    companion object {
        private const val ARG_PAGE = "page"
        fun newInstance(page: OnboardingPage): OnboardingPageFragment {
            return OnboardingPageFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PAGE, page)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_onboarding_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val page = arguments?.getSerializable(ARG_PAGE) as? OnboardingPage ?: return
        view.findViewById<ImageView>(R.id.onboardingImage).setImageResource(page.imageRes)
        view.findViewById<TextView>(R.id.onboardingTitle).text = page.title
        view.findViewById<TextView>(R.id.onboardingDescription).text = page.description
    }
}
