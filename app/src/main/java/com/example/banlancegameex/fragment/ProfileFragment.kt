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
import com.example.banlancegameex.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
    private lateinit var binding : FragmentProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_profileFragment_to_homeFragment)
            }
        })

        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(layoutInflater)

        binding.bookmarkTap.setOnClickListener {
            it.findNavController().navigate(R.id.action_profileFragment_to_bookMarkFragment)
        }

        binding.localgroupTap.setOnClickListener {
            it.findNavController().navigate(R.id.action_profileFragment_to_localGroupFragment)
        }

        binding.homeTap.setOnClickListener {
            it.findNavController().navigate(R.id.action_profileFragment_to_homeFragment)
        }

        binding.searchTap.setOnClickListener {
            // tag Fragment는 search Fragment로 이름 변경됨
            it.findNavController().navigate(R.id.action_profileFragment_to_tagFragment)
        }

        return binding.root
    }

}