package com.abtwin.banlancegameex

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abtwin.banlancegameex.databinding.ActivityMyPostBinding
import com.abtwin.banlancegameex.contentsList.ContentModel
import com.abtwin.banlancegameex.utils.FBAuth
import com.abtwin.banlancegameex.utils.FBRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class MyPostActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMyPostBinding

    // recycler view를 위한 adapter
    lateinit var rvAdapter : MyPostRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMyPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 게시물을 가져올 ContentModel형태의 list
        val items = ArrayList<ContentModel>()
        // ContentModel형태로 가져온 게시물들을 key 값을 저장할 list
        val itemKeyList = ArrayList<String>()

        rvAdapter = MyPostRVAdapter(this, items, itemKeyList)

        val query = FBRef.postRef.orderByChild("uid").equalTo(FBAuth.getuid())
        query.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                items.clear()
                itemKeyList.clear()

                for(postSnapShot in snapshot.children){
                    val item = postSnapShot.getValue(ContentModel::class.java)
                    items.add(item!!)
                    itemKeyList.add(postSnapShot.key.toString())
                }

                rvAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        // recycler view 불러옴
        val rv : RecyclerView = binding.mypostRV

        // recycler view와 adapter 연결
        rv.adapter = rvAdapter

        rv.layoutManager = GridLayoutManager(this, 1)
    }
}