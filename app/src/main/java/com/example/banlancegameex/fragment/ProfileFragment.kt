package com.example.banlancegameex.fragment

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.banlancegameex.*
import com.example.banlancegameex.contentsList.GameMakeActivity
import com.example.banlancegameex.databinding.FragmentProfileBinding
import com.example.banlancegameex.utils.FBAuth
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

        binding.profileTap.setOnClickListener {

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
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }


        binding.userDataUpdate.setOnClickListener {
            val intent = Intent(requireContext(), UserDataUpdateActivity::class.java)
            startActivity(intent)
        }

        binding.myPost.setOnClickListener {
            val intent = Intent(requireContext(), MyPostActivity::class.java)
            startActivity(intent)
        }

        binding.accountDelete.setOnClickListener {
            val user = Firebase.auth.currentUser!!

            val builder = AlertDialog.Builder(requireContext())
                .setTitle("계정 삭제")
                .setMessage("정말 계정을 삭제하시겠습니까?")
                .setPositiveButton("네",DialogInterface.OnClickListener{dialog, which ->
                    database.child("userdata").orderByChild("email").equalTo(auth.currentUser?.email.toString())
                        .addListenerForSingleValueEvent(object : ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for(child in snapshot.children){
                                    child.ref.removeValue()
                                    Log.d("계정 삭제 테스트", auth.currentUser?.email.toString())
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Toast.makeText(requireContext(), "DB연동에 실패하였습니다.", Toast.LENGTH_LONG).show()
                            }
                        })

                    user.delete().addOnCompleteListener { task->
                        if (task.isSuccessful) {
                            val intent = Intent(requireContext(), MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            Toast.makeText(requireContext(),"계정 삭제 완료",Toast.LENGTH_SHORT).show()
                        }
                        else {
                            Toast.makeText(requireContext(),"계정 삭제 실패",Toast.LENGTH_SHORT).show()
                        }
                    }
                })
                .setNegativeButton("아니오",DialogInterface.OnClickListener { dialog, which ->
                    Toast.makeText(requireContext(),"삭제 취소",Toast.LENGTH_SHORT).show()
                })
            builder.show()
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
                    if(FBAuth.getProfile().isNotEmpty()){
                        Glide.with(requireActivity())
                            .load(FBAuth.getProfile())
                            .into(binding.userProfileImage)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "DB연동에 실패하였습니다.", Toast.LENGTH_LONG).show()
                }
            })

        return binding.root
    }
}