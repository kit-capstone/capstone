package com.example.banlancegameex.comment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.banlancegameex.R


class CommentRVAdapter(private val commentList: List<CommentModel>) : RecyclerView.Adapter<CommentRVAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.comment_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(commentList[position])
    }

    override fun getItemCount(): Int {
        return commentList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.titleArea)
        private val time: TextView = itemView.findViewById(R.id.timeArea)

        fun bind(comment: CommentModel) {
            title.text = comment.commentTitle
            time.text = comment.commentCreatedTime
        }
    }
}
