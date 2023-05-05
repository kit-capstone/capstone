package com.example.banlancegameex.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.work.WorkManager
import com.example.banlancegameex.MainActivity
import com.example.banlancegameex.R
import com.example.banlancegameex.UserDataModel
import com.example.banlancegameex.contentsList.GameMakeActivity
import com.example.banlancegameex.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment() {
    private lateinit var binding : FragmentProfileBinding
    private lateinit var auth : FirebaseAuth

    val database = Firebase.database.reference
    var user_data = ""

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
        auth = Firebase.auth

        binding.editPost.setOnClickListener{
            val intent = Intent(context, GameMakeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

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

        binding.logout.setOnClickListener{
            auth.signOut()
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }


        binding.userDataUpdate.setOnClickListener {
            WorkManager.getInstance(requireContext()).cancelAllWork()
        }

        database.child("userdata").orderByChild("email").equalTo(auth.currentUser?.email.toString())
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(datamodel in snapshot.children){
                        user_data = datamodel.getValue(UserDataModel::class.java)?.nickname?:"nothing"
                        Log.d("디버그 테스트", user_data)
                    }

                    binding.nicknameArea.setText(user_data)
                    binding.emailArea.text = auth.currentUser?.email.toString()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "DB연동에 실패하였습니다.", Toast.LENGTH_LONG).show()
                }
            })

        return binding.root
    }
}