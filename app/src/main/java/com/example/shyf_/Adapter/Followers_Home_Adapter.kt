package com.example.shyf_.Adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.shyf_.R
import com.example.shyf_.Activitys.UsersProfileActivity
import com.example.shyf_.databinding.ListFollowBinding
import com.example.shyf_.model.Followers

class Followers_Home_Adapter(var activity: Activity, var arrayList: ArrayList<Followers>) :
    RecyclerView.Adapter<Followers_Home_Adapter.Holder>() {

    class Holder(var binding: ListFollowBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        var root = ListFollowBinding.inflate(activity.layoutInflater, parent, false)
        return Holder(root)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("ResourceAsColor", "MissingInflatedId", "SetTextI18n")
    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.binding.nameUserFollow.setText(arrayList.get(position).nameUser)
        val imagePath_User = arrayList.get(position).imageUser.toString()
        val imagePath_Background = arrayList.get(position).BackgroundImage.toString()

        val extractedTextUser = imagePath_User.substringBefore(".jpg")
        val extractedTextBackground = imagePath_Background.substringBefore(".jpg")
        Glide.with(activity)
            .load("http://10.0.2.2/db/shyf/$extractedTextUser.jpg")
            .apply(RequestOptions().override(600, 600))
            .error(R.drawable.img_food)
            .into(holder.binding.imageUserFollow)
        val shared =
            activity.getSharedPreferences("user_data", AppCompatActivity.MODE_PRIVATE)
        var id = shared.getInt("id", 0).toString().toInt()

        if (arrayList.get(position).userId == id) {
            holder.binding.btnProfileUser.visibility = View.GONE
        }

        holder.binding.btnProfileUser.setOnClickListener {
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
            activity.overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
        }
    }


    override fun getItemCount(): Int {
        return arrayList.size
    }

}