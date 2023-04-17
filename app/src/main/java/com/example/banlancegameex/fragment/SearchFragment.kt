package com.example.banlancegameex.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.banlancegameex.R
import com.example.banlancegameex.databinding.FragmentSearchBinding

class SearchFragment : Fragment() {
    private lateinit var binding : FragmentSearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_tagFragment_to_homeFragment)
            }
        })

        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(layoutInflater)

        binding.bookmarkTap.setOnClickListener {
            it.findNavController().navigate(R.id.action_tagFragment_to_bookMarkFragment)
        }

        binding.localgroupTap.setOnClickListener {
            it.findNavController().navigate(R.id.action_tagFragment_to_localGroupFragment)
        }

        binding.homeTap.setOnClickListener {
            it.findNavController().navigate(R.id.action_tagFragment_to_homeFragment)
        }

        binding.profileTap.setOnClickListener {
            it.findNavController().navigate(R.id.action_tagFragment_to_profileFragment)
        }

        return binding.root
    }

}