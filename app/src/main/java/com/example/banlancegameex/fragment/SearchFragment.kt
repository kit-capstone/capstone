package com.example.banlancegameex.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.banlancegameex.R
import com.example.banlancegameex.contentsList.ContentModel
import com.example.banlancegameex.contentsList.ContentRVAdapter
import com.example.banlancegameex.contentsList.SearchContentModel
import com.example.banlancegameex.databinding.FragmentSearchBinding
import com.example.banlancegameex.utils.FBAuth
import com.example.banlancegameex.utils.FBRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SearchFragment : Fragment() {
    private lateinit var binding : FragmentSearchBinding

    val bookmarkIdList = mutableListOf<String>()

    var search_title : String = ""

    // recycler view를 위한 adapter
    lateinit var rvAdapter : ContentRVAdapter

    var tagList : ArrayList<String> = arrayListOf()

    // 게시물을 가져올 ContentModel형태의 list
    val items = ArrayList<ContentModel>()
    // ContentModel형태로 가져온 게시물들을 key 값을 저장할 list
    val itemKeyList = ArrayList<String>()

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

        binding = FragmentSearchBinding.inflate(layoutInflater)


        val itemssearch = ArrayList<SearchContentModel>()

        var search_filter : List<SearchContentModel> = listOf()

        binding.loveBtn.setOnClickListener{
            if(binding.loveBtn.isChecked){
                tagList.add("연애")
            } else {
                tagList.remove("연애")
            }
            items.clear()
            itemKeyList.clear()

            search_title = binding.searchText.text.toString()
            if(search_title == ""){
                setTagPost(itemssearch)
            }else {
                setTagPost(search_filter)
            }

            rvAdapter.notifyDataSetChanged()
        }

        binding.gameBtn.setOnClickListener{
            if(binding.gameBtn.isChecked){
                tagList.add("게임")
            } else {
                tagList.remove("게임")
            }
            items.clear()
            itemKeyList.clear()

            search_title = binding.searchText.text.toString()
            if(search_title == ""){
                setTagPost(itemssearch)
            }else {
                setTagPost(search_filter)
            }

            rvAdapter.notifyDataSetChanged()
        }

        binding.foodBtn.setOnClickListener{
            if(binding.foodBtn.isChecked){
                tagList.add("음식")
            } else {
                tagList.remove("음식")
            }
            items.clear()
            itemKeyList.clear()

            search_title = binding.searchText.text.toString()
            if(search_title == ""){
                setTagPost(itemssearch)
            }else {
                setTagPost(search_filter)
            }

            rvAdapter.notifyDataSetChanged()
        }

        binding.favoriteBtn.setOnClickListener{
            if(binding.favoriteBtn.isChecked){
                tagList.add("취미")
            } else {
                tagList.remove("취미")
            }
            items.clear()
            itemKeyList.clear()

            search_title = binding.searchText.text.toString()
            if(search_title == ""){
                setTagPost(itemssearch)
            }else {
                setTagPost(search_filter)
            }

            rvAdapter.notifyDataSetChanged()
        }

        binding.aniBtn.setOnClickListener{
            if(binding.aniBtn.isChecked){
                tagList.add("만화")
            } else {
                tagList.remove("운동")
            }
            items.clear()
            itemKeyList.clear()

            search_title = binding.searchText.text.toString()
            if(search_title == ""){
                setTagPost(itemssearch)
            }else {
                setTagPost(search_filter)
            }

            rvAdapter.notifyDataSetChanged()
        }

        binding.exerciseBtn.setOnClickListener{
            if(binding.exerciseBtn.isChecked){
                tagList.add("운동")
            } else {
                tagList.remove("운동")
            }
            items.clear()
            itemKeyList.clear()

            search_title = binding.searchText.text.toString()
            if(search_title == ""){
                setTagPost(itemssearch)
            }else {
                setTagPost(search_filter)
            }

            rvAdapter.notifyDataSetChanged()
        }

        binding.searchBtn.setOnClickListener {
            search_title = binding.searchText.text.toString()
            if(search_title != ""){
                search_filter = itemssearch.filter { it.title.contains(search_title) }
                items.clear()
                itemKeyList.clear()
                toggleInit()

                for(item in search_filter) {
                    items.add(ContentModel(item.title, item.option1, item.option1Sub, item.option2, item.option2Sub,
                        item.inquiry, item.tag, item.locate, item.uid, item.time))
                    itemKeyList.add(item.key)

                }
            } else {
                items.clear()
                itemKeyList.clear()
                for(item in itemssearch){
                    items.add(ContentModel(item.title, item.option1, item.option1Sub, item.option2, item.option2Sub,
                        item.inquiry, item.tag, item.locate, item.uid, item.time))
                    itemKeyList.add(item.key)
                }
            }
            rvAdapter.notifyDataSetChanged()
        }

        rvAdapter = ContentRVAdapter(requireContext(), items, itemKeyList, bookmarkIdList)

        FBRef.postRef.orderByChild("inquiry").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                for(datasnapshot in snapshot.children){
                    val item = datasnapshot.getValue(ContentModel::class.java)

                    items.add(item!!)
                    itemssearch.add(SearchContentModel(item.title, item.option1, item.option1Sub, item.option2, item.option2Sub, item.inquiry, item.tag,
                    item.locate, item.uid, item.time, datasnapshot.key.toString()))

                    // push한 게임의 key 값을 itemKeyList에 저장
                    // 사용자가 북마크한 게임을 식별하기 위한 변수
                    itemKeyList.add(datasnapshot.key.toString())
                }

                // 가져온 게임 정보들을 내림차순으로 정렬
                // inquiry(조회수)가 큰 게임부터 출력하기 위한 코드
                items.reverse()
                itemKeyList.reverse()

                rvAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("ContentListActivity", "loadPost:onCancelled", error.toException())
            }
        })

        // recycler view 불러옴
        val rv : RecyclerView = binding.contentsRV

        // recycler view와 adapter 연결
        rv.adapter = rvAdapter

        rv.layoutManager = GridLayoutManager(requireContext(), 1)

        // 북마크한 게임 정보를 불러오기 위한 함수
        getBookmarkData()

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

    private fun getBookmarkData() {
        val bookmarkRef : DatabaseReference
        // 사용자의 uid를 객체의 key값으로 지정
        // 각 객체에 북마크한 게임 정보의 key값을 bookmark_list 테이블에 저장, 불러오는 코드
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

    private fun toggleInit(){
        tagList.clear()
        binding.loveBtn.isChecked = false
        binding.gameBtn.isChecked = false
        binding.foodBtn.isChecked = false
        binding.favoriteBtn.isChecked = false
        binding.aniBtn.isChecked = false
        binding.exerciseBtn.isChecked = false
    }

    private fun setTagPost(postList : List<SearchContentModel>) {
        if(tagList.isEmpty()) {
            for(item in postList){
                items.add(ContentModel(item.title, item.option1, item.option1Sub, item.option2, item.option2Sub,
                    item.inquiry, item.tag, item.locate, item.uid, item.time))
                itemKeyList.add(item.key)
            }
        } else {
            for(tag in tagList) {
                for(item in postList){
                    if(item.tag.contains(tag)){
                        items.add(ContentModel(item.title, item.option1, item.option1Sub, item.option2, item.option2Sub,
                            item.inquiry, item.tag, item.locate, item.uid, item.time))
                        itemKeyList.add(item.key)
                    }
                }
            }
        }
    }

}