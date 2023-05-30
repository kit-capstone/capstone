package com.example.banlancegameex

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.banlancegameex.contentsList.ContentModel
import com.example.banlancegameex.contentsList.CountModel
import com.example.banlancegameex.contentsList.GameInsideActivity
import com.example.banlancegameex.utils.FBRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class MyPostRVAdapter (val context : Context,
                      val items : ArrayList<ContentModel>,
                      val keyList : ArrayList<String>)
    : RecyclerView.Adapter<MyPostRVAdapter.Viewholder>() {

    lateinit var game_count : CountModel
    var opt1_count = 0.0
    var opt2_count = 0.0

    private var alertDialog: AlertDialog? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPostRVAdapter.Viewholder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.mypost_rv_item, parent, false)

        return Viewholder(v)
    }

    override fun onBindViewHolder(holder: MyPostRVAdapter.Viewholder, position: Int) {
        holder.bindItems(items[position], keyList[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class Viewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(item : ContentModel, key : String){

            itemView.setOnClickListener {
                // 게임 페이지로 이동 코드 구현 필요
                val intent = Intent(context, GameInsideActivity::class.java)
                intent.putExtra("key", key)
                itemView.context.startActivity(intent)
            }

            val gameTitle = itemView.findViewById<TextView>(R.id.game_title_txt)
            val gameOption1 = itemView.findViewById<TextView>(R.id.game_option1_txt)
            val gameOption2 = itemView.findViewById<TextView>(R.id.game_option2_txt)

            val countListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (data in dataSnapshot.children) {
                        if(data.key.toString() == key){
                            game_count = data.getValue(CountModel::class.java)!!

                            // 프로그레스 바 및 비율 출력
                            val opt1_progress = itemView.findViewById<View>(R.id.opt1_progress_bar)
                            val opt2_progress = itemView.findViewById<View>(R.id.opt2_progress_bar)
                            val opt1_percent = itemView.findViewById<TextView>(R.id.game_opt1_percent)
                            val opt2_percent = itemView.findViewById<TextView>(R.id.game_opt2_percent)

                            var total = game_count.total_opt1 + game_count.total_opt2
                            opt1_count = ((game_count.total_opt1.toDouble() / total.toDouble()) * 100)
                            Log.d("숫자 확인", opt1_count.toString())
                            opt2_count = ((game_count.total_opt2.toDouble() / total.toDouble()) * 100)

                            if(opt1_count.isNaN()){
                                opt1_count = 0.0
                            }

                            if(opt2_count.isNaN()){
                                opt2_count = 0.0
                            }

                            val opt1_data = String.format("%.1f", opt1_count)
                            val opt2_data = String.format("%.1f", opt2_count)

                            opt1_percent.text = opt1_data + "%"
                            opt2_percent.text = opt2_data + "%"

                            val opt1_count_int = opt1_count.toInt()
                            val opt2_count_int = opt2_count.toInt()

                            val opt1_percent_weight = opt1_percent.layoutParams as LinearLayout.LayoutParams
                            opt1_percent_weight.weight = opt1_count_int.toFloat() // 변경할 weight 값
                            val opt2_percent_weight = opt2_percent.layoutParams as LinearLayout.LayoutParams
                            opt2_percent_weight.weight = opt2_count_int.toFloat() // 변경할 weight 값

                            val opt1_progress_weight = opt1_progress.layoutParams as LinearLayout.LayoutParams
                            opt1_progress_weight.weight = (120 - opt1_count_int).toFloat() // 변경할 weight 값
                            val opt2_progress_weight = opt2_progress.layoutParams as LinearLayout.LayoutParams
                            opt2_progress_weight.weight = (120 - opt2_count_int).toFloat() // 변경할 weight 값

                            opt1_percent.layoutParams = opt1_percent_weight
                            opt2_percent.layoutParams = opt2_percent_weight
                            opt1_progress.layoutParams = opt1_progress_weight
                            opt2_progress.layoutParams = opt2_progress_weight
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            }
            FBRef.countRef.addListenerForSingleValueEvent(countListener)

            val menuArea = itemView.findViewById<ImageView>(R.id.menu_area)

            menuArea.setOnClickListener {
                showDialog(key, item.title)
            }

            gameTitle.text = item.title
            gameOption1.text = item.option1
            gameOption2.text = item.option2
        }
    }

    private fun showDialog(key : String, title : String){

        val mDialogView = LayoutInflater.from(context).inflate(R.layout.game_setting_dialog, null)
        val mBuilder = AlertDialog.Builder(context)
            .setView(mDialogView)
            .setTitle("게시글 삭제")

        alertDialog = mBuilder.show()

        //다이얼로그의 백그라운드를 둥글게 깎기 위해선 이 코드가 필요
        alertDialog?.window?.setBackgroundDrawableResource(R.drawable.background_radius)

        alertDialog?.findViewById<Button>(R.id.editBtn)?.setOnClickListener{
            Toast.makeText(context,"aa", Toast.LENGTH_SHORT).show()
        }
        alertDialog?.findViewById<Button>(R.id.removeBtn)?.setOnClickListener{
            FBRef.postRef.child(key).removeValue()
            FBRef.countRef.child(key).removeValue()
            Toast.makeText(context,"삭제완료", Toast.LENGTH_SHORT).show()
            alertDialog?.dismiss()
        }
    }
}