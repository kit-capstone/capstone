package com.example.banlancegameex.contentsList

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.banlancegameex.R
import com.example.banlancegameex.utils.FBAuth
import com.example.banlancegameex.utils.FBRef

class ContentRVAdapter (val context : Context,
                        val items : ArrayList<ContentModel>,
                        val keyList : ArrayList<String>,
                        val bookmarkIdList : MutableList<String>)
    :RecyclerView.Adapter<ContentRVAdapter.Viewholder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentRVAdapter.Viewholder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.content_rv_item, parent, false)

        return Viewholder(v)
    }

    override fun onBindViewHolder(holder: ContentRVAdapter.Viewholder, position: Int) {
        holder.bindItems(items[position], keyList[position], keyList)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(item : ContentModel, key : String, keylist: ArrayList<String>){

            itemView.setOnClickListener {
                // 조회수 갱신 기능 추가
                item.inquiry ++
                FBRef.postRef.child(key).setValue(item)

                // 게임 페이지로 이동 코드 구현 필요
                val intent = Intent(context, GameInsideActivity::class.java)
                intent.putExtra("key", key)
                intent.putStringArrayListExtra("keylist", keylist)
                itemView.context.startActivity(intent)
            }

            val gameTitle = itemView.findViewById<TextView>(R.id.game_title_txt)
            val gameOption1 = itemView.findViewById<TextView>(R.id.game_option1_txt)
            val gameOption2 = itemView.findViewById<TextView>(R.id.game_option2_txt)
            val bookmarkArea = itemView.findViewById<ImageView>(R.id.bookmark_area)

            if(bookmarkIdList.contains(key)){
                // 사용자에게 체크된 북마크
                bookmarkArea.setImageResource(R.drawable.bookmark_select)
            }else {
                // 사용자가 체크하지 않은 북마크
                bookmarkArea.setImageResource(R.drawable.bookmark_unselect)
            }

            bookmarkArea.setOnClickListener {
                if(bookmarkIdList.contains(key)){
                    bookmarkIdList.remove(key)
                    // user_bookmark 테이블에서 삭제
                    FBRef.bookmarkRef
                        .child(FBAuth.getuid())
                        .child(key)
                        .removeValue()
                } else {
                    // user_bookmark 테이블에서 추가
                    FBRef.bookmarkRef
                        .child(FBAuth.getuid())
                        .child(key)
                        .setValue(BookmarkModel(true))
                }
            }

            gameTitle.text = item.title
            gameOption1.text = item.option1
            gameOption2.text = item.option2
        }
    }
    }