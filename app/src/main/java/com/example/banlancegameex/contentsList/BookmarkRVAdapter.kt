package com.example.banlancegameex.contentsList

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.banlancegameex.MainActivity
import com.example.banlancegameex.R
import com.example.banlancegameex.utils.FBAuth
import com.example.banlancegameex.utils.FBRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class BookmarkRVAdapter (val context : Context,
                         val items : ArrayList<ContentModel>,
                         val keyList : ArrayList<String>,
                         val bookmarkIdList : MutableList<String>)

    : RecyclerView.Adapter<BookmarkRVAdapter.Viewholder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BookmarkRVAdapter.Viewholder {val v = LayoutInflater.from(parent.context).inflate(R.layout.content_rv_item, parent, false)

        Log.d("BookmarkRVAdapter", keyList.toString())
        Log.d("BookmarkRVAdapter", bookmarkIdList.toString())
        return Viewholder(v)
    }

    override fun onBindViewHolder(holder: BookmarkRVAdapter.Viewholder, position: Int) {
        holder.bindItems(items[position], keyList[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class Viewholder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(item : ContentModel, key : String) {

            itemView.setOnClickListener {
                val intent = Intent(context, GameInsideActivity::class.java)
                intent.putExtra("key", key)
                itemView.context.startActivity(intent)

            }

            val gameTitle = itemView.findViewById<TextView>(R.id.game_title_txt)
            val gameOption1 = itemView.findViewById<TextView>(R.id.game_option1_txt)
            val gameOption2 = itemView.findViewById<TextView>(R.id.game_option2_txt)
            val bookmarkArea = itemView.findViewById<ImageView>(R.id.bookmark_area)

            if(bookmarkIdList.contains(key)) {
                bookmarkArea.setImageResource(R.drawable.bookmark_select)
            } else {
                //bookmarkArea.setImageResource(R.drawable.bookmark_unselect)
            }

            bookmarkArea.setOnClickListener {
                if(bookmarkIdList.contains(key)){
                    bookmarkIdList.remove(key)
                    val builder = AlertDialog.Builder(context)
                        .setTitle("북마크 제거")
                        .setMessage("북마크를 해제 하시겠습니까?")
                        .setPositiveButton("네", DialogInterface.OnClickListener{ dialog, which ->
                            // user_bookmark 테이블에서 삭제
                            FBRef.bookmarkRef
                                .child(FBAuth.getuid())
                                .child(key)
                                .removeValue()
                            notifyDataSetChanged()

                       })
                        .setNegativeButton("아니오", DialogInterface.OnClickListener { dialog, which ->

                        })
                    builder.show()

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