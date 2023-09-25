package com.example.shyf_.Adapter

import android.app.Activity
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.shyf_.Activitys.UsersProfileActivity
import com.example.shyf_.R
import com.example.shyf_.apis.RetrofitDeleteComment
import com.example.shyf_.databinding.CommentsLayoutListBinding
import com.example.shyf_.inite.GetUserData
import com.example.shyf_.model.Comments
import com.example.shyf_.model.ResultComments
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class CommentsAdapter(var activity: Activity, var arrayList: ArrayList<Comments>) :
    RecyclerView.Adapter<CommentsAdapter.HolderItem>() {

    class HolderItem(var binding: CommentsLayoutListBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderItem {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: HolderItem, position: Int) {
        holder.binding.nameUserComment.setText(arrayList.get(position).nameUser)
        holder.binding.CommentTxt.setText(arrayList.get(position).comment)
        // استخدام الوقت المحسوب بشكل مناسب
        val timeAgo = getTimeAgoFromString(arrayList[position].TimeAdd.toString())
        // عرض الوقت في الواجهة
        holder.binding.TimeCommentAdd.text = timeAgo
        val imagePath_User = arrayList.get(position).imageUser.toString()
        val imagePath_Background = arrayList.get(position).BackgroundImage.toString()

        val extractedTextUser = imagePath_User.substringBefore(".jpg")
        val extractedTextBackground = imagePath_Background.substringBefore(".jpg")
        Glide.with(activity)
            .load("http://10.0.2.2/db/shyf/$extractedTextUser.jpg")
            .apply(RequestOptions().override(600, 600))
            .error(R.drawable.img_food)
            .into(holder.binding.commentImageUser)
        val shared =
            activity.getSharedPreferences("user_data", AppCompatActivity.MODE_PRIVATE)
        var id = shared.getInt("id", 0).toString().toInt()

        holder.binding.commentImageUser.setOnClickListener {
            if (arrayList.get(position).userId != id) {
                val i = Intent(activity, UsersProfileActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                i.putExtra("idUser", arrayList.get(position).userId)
                i.putExtra("nameUser", arrayList.get(position).nameUser)
                i.putExtra("imageUser", "$extractedTextUser")
                i.putExtra("imageBackground", "$extractedTextBackground")
                i.putExtra(
                    "WhatsappLink",
                    arrayList.get(position).WhatsappLink.toString()
                )
                i.putExtra(
                    "FacebookLink",
                    arrayList.get(position).FacebookLink.toString()
                )
                i.putExtra(
                    "emailUser",
                    arrayList.get(position).emailUser.toString()
                )
                i.putExtra(
                    "userInfo",
                    arrayList.get(position).Information.toString()
                )
                i.putExtra(
                    "numFollowers",
                    arrayList.get(position).numFollowers
                )
                i.putExtra(
                    "numFollowing",
                    arrayList.get(position).numFollowing
                )
                activity.startActivity(i)
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
        }

        //======================================================================= Menu Comments POSTS CODE
        holder.binding.menuComments.setOnClickListener {
            val userId: Int = GetUserData(activity).getId()
            if (userId == arrayList[position].userId) {
                val popupMenu = PopupMenu(activity, it)
                popupMenu.menuInflater.inflate(R.menu.comments_menu, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.deleteComments -> {
                            // اضف أكواد التفاعل عند اختيار الابتسامة الضاحكة
                            deleteCommentsById(position) // تمرير مصدر الصورة للدالة
                            true
                        }
                        // يمكنك إضافة المزيد من الرموز التعبيرية هنا
                        else -> false
                    }
                }
                popupMenu.show()
            } else {
                val popupMenu = PopupMenu(activity, it)
                popupMenu.menuInflater.inflate(R.menu.comment_user_menu, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.copyComment -> {
                            // اضف أكواد التفاعل عند اختيار الابتسامة الضاحكة
                            setClipboard(
                                activity,
                                arrayList[position].comment.toString()
                            )
                            true
                        }
                        // يمكنك إضافة المزيد من الرموز التعبيرية هنا
                        else -> false
                    }
                }
                popupMenu.show()
            }
        }

    }

    private fun deleteCommentsById(position: Int) {
        val alertDialog = AlertDialog.Builder(activity)
        alertDialog.setIcon(R.drawable.baseline_delete_24)
        alertDialog.setMessage("Are you sure to delete ?")
        alertDialog.setTitle(activity.getString(R.string.delete))
        alertDialog.setCancelable(true)
        alertDialog.setPositiveButton(activity.getString(R.string.delete)) { p, d ->
            val userId: Int = GetUserData(activity).getId()
            val idPost: Int = arrayList[position].postId!!
            val call: Call<ResultComments> =
                RetrofitDeleteComment.getInstance().myApi.deleteCommentsById(
                    arrayList[position].id,
                    idPost
                )
            call.enqueue(object : Callback<ResultComments> {
                override fun onResponse(
                    call: Call<ResultComments>,
                    response: Response<ResultComments>,
                ) {
                    if (response.body()!!.error === false) {
                        arrayList.remove(arrayList[position])
                        Toast.makeText(
                            activity,
                            response.body()!!.message.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                        notifyDataSetChanged()
                    }
                    Log.e("yazan", "deleted comments :)$userId")
                }

                override fun onFailure(call: Call<ResultComments>, t: Throwable) {
                    Log.e("yazan", t.message!!)
                    Toast.makeText(activity, "Not deleted $userId", Toast.LENGTH_SHORT).show()
                }
            })
            notifyDataSetChanged()
        }
        alertDialog.setNegativeButton("No") { n, d ->
        }
        alertDialog.create().show()
    }

    private fun getTimeAgoFromString(timeString: String): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")

        try {
            val createdTimeMillis = dateFormat.parse(timeString)?.time ?: return ""

            val currentTime = System.currentTimeMillis()
            val elapsedTimeMillis = currentTime - createdTimeMillis
            val elapsedSeconds = elapsedTimeMillis / 1000

            return when {
                elapsedSeconds < 60 -> "${elapsedSeconds}s" // ثوانٍ
                elapsedSeconds < 3600 -> "${elapsedSeconds / 60}m" // دقائق
                elapsedSeconds < 86400 -> "${elapsedSeconds / 3600}h" // ساعات
                elapsedSeconds < 604800 -> "${elapsedSeconds / 86400}d" // أيام
                elapsedSeconds < 2419200 -> "${elapsedSeconds / 604800}w" // أسابيع
                elapsedSeconds < 29030400 -> "${elapsedSeconds / 2419200}M" // أشهر
                else -> "${elapsedSeconds / 29030400}y" // سنين
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return ""
    }

    // copy Comment methode
    private fun setClipboard(context: Context, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Copied Text", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show()
    }




    override fun getItemCount(): Int {
        return arrayList.size
    }




}