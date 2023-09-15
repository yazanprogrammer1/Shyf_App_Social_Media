package com.example.shyf_.Adapter

import android.app.Activity
import android.media.MediaPlayer
import android.util.Log
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.shyf_.R
import com.example.shyf_.apis.RetrofitDeleteRequestUser
import com.example.shyf_.databinding.CardPersonRequestsBinding
import com.example.shyf_.model.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Request_Adapter_ForUser(
    var activity: Activity,
    var arrayList: ArrayList<Request>
) :
    RecyclerView.Adapter<Request_Adapter_ForUser.Holder>() {

    class Holder(var binding: CardPersonRequestsBinding) : RecyclerView.ViewHolder(binding.root)


    fun deleteItem(
        i: Int,
        message: String,
        namePositiveButton: String,
        nameNegativeButton: String
    ) {
        //initMediaPlayer
        var mediaPlayerClick: MediaPlayer = MediaPlayer.create(activity, R.raw.delete_post)
        mediaPlayerClick.isLooping = false
        mediaPlayerClick.start()
        val alertDialog = AlertDialog.Builder(activity)
        alertDialog.setIcon(R.drawable.baseline_delete_24)
        alertDialog.setMessage(message)
        alertDialog.setTitle(namePositiveButton)
        alertDialog.setCancelable(true)
        alertDialog.setPositiveButton(namePositiveButton) { p, d ->
            val idRequest: Int = arrayList.get(i).idRequest!!
            val call: Call<Request> =
                RetrofitDeleteRequestUser.getInstance().getMyApi().deleteRequestById(idRequest)
            call.enqueue(object : Callback<Request> {
                override fun onResponse(call: Call<Request>, response: Response<Request>) {
                    if (response.body()!!.error_post === false) {
                        arrayList.remove(arrayList.get(i))
                        notifyDataSetChanged()
                    }
                    Log.e("yazan", "Created :)")
                }

                override fun onFailure(call: Call<Request>, t: Throwable) {
                    Log.e("yazan", t.message!!)
                }
            })
            notifyDataSetChanged()
        }
        alertDialog.setNegativeButton(nameNegativeButton) { n, d ->
        }
        alertDialog.create().show()
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): Request_Adapter_ForUser.Holder {
        var root = CardPersonRequestsBinding.inflate(activity.layoutInflater, parent, false)
        return Holder(root)
    }

    override fun onBindViewHolder(holder: Request_Adapter_ForUser.Holder, position: Int) {
        holder.binding.infoPersonRequest.setText(arrayList.get(position).description)
        holder.binding.countPersonRequest.setText(arrayList.get(position).count.toString())
        holder.binding.pricePersonRequest.setText(arrayList.get(position).totalPrice.toString())
        val imagePath_User = arrayList.get(position).imageUser.toString()
        val imagePath_Background = arrayList.get(position).BackgroundImage.toString()
        val imagePath_Food = arrayList.get(position).image.toString()

        val extractedTextUser = imagePath_User.substringBefore(".jpg")
        val extractedTextBackground = imagePath_Background.substringBefore(".jpg")
        val extractedTextFood = imagePath_Food.substringBefore(".jpg")
        Glide.with(activity).load("http://10.0.2.2/db/shyf/$extractedTextFood.jpg")
            .apply(RequestOptions().override(600, 600)).error(R.drawable.img_food)
            .into(holder.binding.bannerImagePersonRequest)
    }


    override fun getItemCount(): Int {
        return arrayList.size
    }

}