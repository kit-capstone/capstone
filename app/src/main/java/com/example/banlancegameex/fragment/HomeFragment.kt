package com.example.banlancegameex.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.banlancegameex.R
import com.example.banlancegameex.contentsList.*
import com.example.banlancegameex.databinding.FragmentHomeBinding
import com.example.banlancegameex.utils.FBAuth
import com.example.banlancegameex.utils.FBRef
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var binding : FragmentHomeBinding
    // 사용자의 uid를 가져오기 위한 FirebaseAuth
    private lateinit var auth : FirebaseAuth
    // firebase DB에 사용자 정보를 저장할 위치
    lateinit var postRef : DatabaseReference
    // 사용자가 북마크한 게임의 key값을 저장할 list
    val bookmarkIdList = mutableListOf<String>()

    var todayRecommended : String = ""
    private lateinit var _sort : String
    // recycler view를 위한 adapter
    lateinit var rvAdapter : ContentRVAdapter

    lateinit var adapter : ArrayAdapter<String>

    val itemArray = ArrayList<SearchContentModel>()

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

        // 홈에서 뒤로 가기 버튼을 누르면 종료
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        })

        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater)


        // FirebaseAuth 초기화
        auth = FirebaseAuth.getInstance()

        // 커스텀 adaper를 불러옴
        rvAdapter = ContentRVAdapter(requireContext(), items, itemKeyList, bookmarkIdList)

        val database = Firebase.database

        setSpinnerSort()
        setupSpinnerHandler()

        postRef = database.getReference("post")

        // post 테이블에서 inquiry(조회수) 속성을 기준으로 오름차순으로 게임 정보를 받아옴
        postRef.orderByChild("inquiry").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                items.clear()
                itemKeyList.clear()
                itemArray.clear()

                for(dataModel in snapshot.children){
                    // 게임 하나의 정보를 items에 push
                    val item = dataModel.getValue(ContentModel::class.java)
                    items.add(item!!)
                    itemArray.add(SearchContentModel(item!!, dataModel.key.toString()))

                    setSpinnerSort()
                    setupSpinnerHandler()

                    // push한 게임의 key 값을 itemKeyList에 저장
                    // 사용자가 북마크한 게임을 식별하기 위한 변수
                    itemKeyList.add(dataModel.key.toString())
                }

                // 가져온 게임 정보들을 내림차순으로 정렬
                // inquiry(조회수)가 큰 게임부터 출력하기 위한 코드
                items.reverse()
                itemKeyList.reverse()

                // 오늘의 추천으로 출력할 게임을 선정하기 위한 코드
                // 위에서 받아온 게임 정보 중 상위 3개 중 임의로 선정
//                val subList = items.subList(0, 3)
//                val subKeyList = itemKeyList.subList(0, 3)
//                val randomIndex = Random.nextInt(subList.size)
//                val recommedElement = subList[randomIndex]
//
//                binding.gameTitleTxt.text = recommedElement.title
//                binding.gameOption1Txt.text = recommedElement.option1
//                binding.gameOption2Txt.text = recommedElement.option2

                // firebase functions를 이용하여 today의 값을 매일 오전 4시 변경
                // 변경된 today의 값을 key값으로 받아와 화면에 출력
                FBRef.todayRef.addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        todayRecommended = snapshot.getValue() as String
                        val result: SearchContentModel? = itemArray.find { it.key == todayRecommended }

                        if(result != null) {
                            binding.gameTitleTxt.text = result.content.title
                            binding.gameOption1Txt.text = result.content.option1
                            binding.gameOption2Txt.text = result.content.option2
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })

                // Log.d("모델 확인용", items.toString())

                // 비동기 방식으로 받아온 정보를 recycler view에 반영하기 위한 코드
                rvAdapter.notifyDataSetChanged()
                adapter.notifyDataSetChanged()

//                todayRecommended = subKeyList[randomIndex]
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("ContentListActivity", "loadPost:onCancelled", error.toException())
            }
        })

        binding.bookmarkArea.setOnClickListener {
            if(bookmarkIdList.contains(todayRecommended)){
                binding.bookmarkArea.setImageResource(R.drawable.bookmark_unselect)
                bookmarkIdList.remove(todayRecommended)
                FBRef.bookmarkRef
                    .child(FBAuth.getuid())
                    .child(todayRecommended)
                    .removeValue()
            } else {
                FBRef.bookmarkRef
                    .child(FBAuth.getuid())
                    .child(todayRecommended)
                    .setValue(BookmarkModel(true))
                binding.bookmarkArea.setImageResource(R.drawable.bookmark_select)
            }
        }

        // recycler view 불러옴
        val rv : RecyclerView = binding.contentsRV

        // recycler view와 adapter 연결
        rv.adapter = rvAdapter

        rv.layoutManager = GridLayoutManager(requireContext(), 1)

        // 북마크한 게임 정보를 불러오기 위한 함수
        getBookmarkData()

        binding.writeBtn.bringToFront()

        binding.writeBtn.setBackgroundResource(R.drawable.ripple_background_radius)
        binding.writeBtn.setOnClickListener {
            val intent = Intent(context, GameMakeActivity::class.java)
            startActivity(intent)
        }

        binding.todayPost.setOnClickListener {
            val intent = Intent(context, GameInsideActivity::class.java)
            intent.putExtra("key", todayRecommended)
            intent.putStringArrayListExtra("keylist", itemKeyList)
            startActivity(intent)
        }

        binding.bookmarkTap.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_bookMarkFragment)
        }

        binding.homeTap.setOnClickListener {

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

                if(bookmarkIdList.contains(todayRecommended)){
                    binding.bookmarkArea.setImageResource(R.drawable.bookmark_select)
                } else {
                    binding.bookmarkArea.setImageResource(R.drawable.bookmark_unselect)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("HomeFragment", "loadPost:onCancelled", error.toException())
            }
        }
        bookmarkRef.child(FBAuth.getuid()).addValueEventListener(postListener)
    }
    private fun setSpinnerSort(){
        val sort = resources.getStringArray(R.array.spinner_sort)
        adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, sort)
        binding.sortSpinner.adapter = adapter
    }

    private fun setupSpinnerHandler() {
        binding.sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // 드롭 다운 버튼 클릭 시 이벤트
                _sort = binding.sortSpinner.getItemAtPosition(position).toString()

                when(_sort) {
                    "인기순" -> {
                        //게시물을 인기순으로 정렬
                        itemArray.sortByDescending { it.content.inquiry }
                        items.clear()
                        itemKeyList.clear()

                        //정렬한 게시물의 정보를 recycler view에 갱신시킴
                        for(i in itemArray) {
                            items.add(i.content)
                            itemKeyList.add(i.key)
                        }
                        
                        rvAdapter.notifyDataSetChanged()
                    }
                    "최신순" -> {
                        // 게시물을 날짜순으로 정렬
                        itemArray.sortWith(compareBy { SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.getDefault()).parse(it.content.time) })
                        itemArray.reverse()
                        items.clear()
                        itemKeyList.clear()

                        //정렬한 게시물의 정보를 recycler view에 갱신시킴
                        for(i in itemArray) {
                            items.add(i.content)
                            itemKeyList.add(i.key)
                        }

                        rvAdapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                // 아무 것도 선택 하지 않았을 시 이벤트
                _sort = "인기순"
            }
        }

    }
}