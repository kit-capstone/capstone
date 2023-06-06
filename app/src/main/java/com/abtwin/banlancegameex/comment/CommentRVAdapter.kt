package com.abtwin.banlancegameex.comment

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.abtwin.banlancegameex.R
import com.abtwin.banlancegameex.utils.FBAuth
import com.abtwin.banlancegameex.utils.FBRef


class CommentRVAdapter(val context : Context,
                       private val postKey : String,
                       private val commentList: ArrayList<CommentModel>,
                        private val keyList : ArrayList<String>) : RecyclerView.Adapter<CommentRVAdapter.ViewHolder>() {

    private val myUid = FBAuth.getuid()
    private var alertDialog: AlertDialog? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.comment_list_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(commentList[position], keyList[position], postKey)
        Log.d("Commentcontext", commentList[position].toString())
    }

    override fun getItemCount(): Int {
        return commentList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.titleArea)
        private val time: TextView = itemView.findViewById(R.id.timeArea)
        private val editButton: ImageView = itemView.findViewById(R.id.commentSettingIcon)
        private val name: TextView = itemView.findViewById(R.id.nameArea)


        fun bind(comment: CommentModel, commentkey: String, postKey: String) {
            title.text = comment.commentTitle
            time.text = comment.commentCreatedTime
            name.text = comment.nickname


            val isMyComment = comment.uid == myUid

            // 수정 버튼의 표시 여부를 설정합니다.
            if (isMyComment) {
                editButton.visibility = View.VISIBLE
            } else {
                editButton.visibility = View.GONE
            }

            editButton.setOnClickListener{
                showDialog(commentkey, postKey)
            }

        }

    }

    private fun showDialog(key : String, postKey: String){

        val mDialogView = LayoutInflater.from(context).inflate(R.layout.game_setting_dialog, null)
        val mBuilder = AlertDialog.Builder(context)
            .setView(mDialogView)
            .setTitle("댓글 삭제")

        alertDialog = mBuilder.show()

        //다이얼로그의 백그라운드를 둥글게 깎기 위해선 이 코드가 필요
        alertDialog?.window?.setBackgroundDrawableResource(R.drawable.background_radius)

        alertDialog?.findViewById<Button>(R.id.editBtn)?.setOnClickListener{
            Toast.makeText(context,"aa", Toast.LENGTH_SHORT).show()
        }
        alertDialog?.findViewById<Button>(R.id.removeBtn)?.setOnClickListener{
            //삭제기능 구현
            FBRef.commentRef.child(postKey).child(key).removeValue()
            Toast.makeText(context,"삭제완료", Toast.LENGTH_SHORT).show()
            alertDialog?.dismiss()
        }
    }

}


