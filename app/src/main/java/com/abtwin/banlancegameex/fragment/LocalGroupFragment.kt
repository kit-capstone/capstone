package com.abtwin.banlancegameex.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.abtwin.banlancegameex.databinding.FragmentLocalGroupBinding
import com.abtwin.banlancegameex.R

class LocalGroupFragment : Fragment() {
    private lateinit var binding : FragmentLocalGroupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_localGroupFragment_to_homeFragment)
            }
        })

        // Inflate the layout for this fragment
        binding = FragmentLocalGroupBinding.inflate(layoutInflater)

        binding.bookmarkTap.setOnClickListener {
            it.findNavController().navigate(R.id.action_localGroupFragment_to_bookMarkFragment)
        }

        binding.homeTap.setOnClickListener {
            it.findNavController().navigate(R.id.action_localGroupFragment_to_homeFragment)
        }

        binding.searchTap.setOnClickListener {
            // tag Fragment는 search Fragment로 이름 변경됨
            it.findNavController().navigate(R.id.action_localGroupFragment_to_tagFragment)
        }

        binding.profileTap.setOnClickListener {
            it.findNavController().navigate(R.id.action_localGroupFragment_to_profileFragment)
        }
        return binding.root
    }

}