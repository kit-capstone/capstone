package com.example.banlancegameex.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.banlancegameex.R
import com.example.banlancegameex.contentsList.ContentModel
import com.example.banlancegameex.contentsList.ContentRVAdapter
import com.example.banlancegameex.databinding.FragmentHomeBinding
import com.example.banlancegameex.utils.FBAuth
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.random.Random

class HomeFragment : Fragment() {

    private lateinit var binding : FragmentHomeBinding
    private lateinit var auth : FirebaseAuth
    lateinit var postRef : DatabaseReference
    val bookmarkIdList = mutableListOf<String>()

    lateinit var rvAdapter : ContentRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // 홈에서 뒤로 가기 버튼을 누르면 종료
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        })

        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater)

        auth = FirebaseAuth.getInstance()

        // 게시물을 가져올 ContentModel형태의 list
        val items = ArrayList<ContentModel>()
        // ContentModel형태로 가져온 게시물들을 key 값을 저장할 list
        val itemKeyList = ArrayList<String>()

        rvAdapter = ContentRVAdapter(requireContext(), items, itemKeyList, bookmarkIdList)

        val database = Firebase.database

        postRef = database.getReference("post")

        postRef.orderByChild("inquiry").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                for(dataModel in snapshot.children){
                    val item = dataModel.getValue(ContentModel::class.java)
                    items.add(item!!)
                    itemKeyList.add(dataModel.key.toString())
                }
                items.reverse()
                itemKeyList.reverse()

                rvAdapter.notifyDataSetChanged()

                val subList = items.subList(0, 3)
                val randomIndex = Random.nextInt(subList.size)
                val recommedElement = subList[randomIndex]

                binding.gameTitleTxt.text = recommedElement.title
                binding.gameOption1Txt.text = recommedElement.option1
                binding.gameOption2Txt.text = recommedElement.option2
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("ContentListActivity", "loadPost:onCancelled", error.toException())
            }
        })

        val rv : RecyclerView = binding.contentsRV

        rv.adapter = rvAdapter

        rv.layoutManager = GridLayoutManager(requireContext(), 1)

        getBookmarkData()


        binding.bookmarkTap.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_bookMarkFragment)
        }

        binding.localgroupTap.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_localGroupFragment)
        }

        binding.searchTap.setOnClickListener {
            // tag Fragment는 search Fragment로 이름 변경됨
            it.findNavController().navigate(R.id.action_homeFragment_to_tagFragment)
        }

        binding.profileTap.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
        }

        return binding.root
    }

    private fun getBookmarkData() {
        val bookmarkRef : DatabaseReference
        bookmarkRef = Firebase.database.getReference("bookmark_list")

        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                bookmarkIdList.clear()

                for(dataModel in snapshot.children) {
                    bookmarkIdList.add(dataModel.key.toString())
                }
                rvAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("HomeFragment", "loadPost:onCancelled", error.toException())
            }
        }
        bookmarkRef.child(FBAuth.getuid()).addValueEventListener(postListener)
    }

}

//FBRef.postRef.push().setValue(
//ContentModel("테스트 제목1", "테스트 옵션1", "테스트 옵션2", 0, "test", "경상북도 구미시")
//)
//
//FBRef.postRef.push().setValue(
//ContentModel("테스트 제목2", "테스트 옵션1", "테스트 옵션2", 1, "test", "경상북도 구미시")
//)
//
//FBRef.postRef.push().setValue(
//ContentModel("테스트 제목3", "테스트 옵션1", "테스트 옵션2", 2, "test", "경상북도 구미시")
//)
//
//FBRef.postRef.push().setValue(
//ContentModel("테스트 제목4", "테스트 옵션1", "테스트 옵션2", 3, "test", "경상북도 구미시")
//)
//
//FBRef.postRef.push().setValue(
//ContentModel("테스트 제목5", "테스트 옵션1", "테스트 옵션2", 4, "test", "경상북도 구미시")
//)