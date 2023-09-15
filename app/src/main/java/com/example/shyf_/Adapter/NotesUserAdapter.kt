package com.example.shyf_.Adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.shyf_.R
import com.example.shyf_.databinding.NotesLayoutItemBinding
import com.example.shyf_.databinding.StorysLayoutItemBinding
import com.example.shyf_.model.Notes

class NotesUserAdapter(
    var activity: Activity,
    var arrayList: ArrayList<Notes>,
    var rootView: ConstraintLayout
) :
    RecyclerView.Adapter<NotesUserAdapter.Holder>() {
    class Holder(var binding: StorysLayoutItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        var root = StorysLayoutItemBinding.inflate(activity.layoutInflater, parent, false)
        return NotesUserAdapter.Holder(root)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("ResourceAsColor", "MissingInflatedId", "SetTextI18n")
    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.binding.username.text = arrayList[position].nameUser
        holder.binding.textNotes.text = arrayList[position].note
        val imagePathUser = arrayList[position].imageUser.toString()
        val extractedTextUser = imagePathUser.substringBefore(".jpg")
        Glide.with(activity).load("http://10.0.2.2/db/shyf/$extractedTextUser.jpg")
            .apply(RequestOptions().override(600, 600)).error(R.drawable.img_food)
            .into(holder.binding.profileImageStory)
    }


}