package com.example.banlancegameex.contentsList

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.banlancegameex.R

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
                Toast.makeText(context, item.title, Toast.LENGTH_LONG).show()
                val intent = Intent(context, GameInsideActivity::class.java)

            }

            val gameTitle = itemView.findViewById<TextView>(R.id.game_title_txt)
            val gameOption1 = itemView.findViewById<TextView>(R.id.game_option1_txt)
            val gameOption2 = itemView.findViewById<TextView>(R.id.game_option2_txt)
            val bookmarkArea = itemView.findViewById<ImageView>(R.id.bookmark_area)

            if(bookmarkIdList.contains(key)) {
                bookmarkArea.setImageResource(R.drawable.bookmark_select)
            } else {
                bookmarkArea.setImageResource(R.drawable.bookmark_unselect)
            }


            gameTitle.text = item.title
            gameOption1.text = item.option1
            gameOption2.text = item.option2


        }

    }
}