package com.example.shyf_.Activitys

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shyf_.Adapter.Followers_Home_Adapter
import com.example.shyf_.R
import com.example.shyf_.apis.RetrofitGetFollowers
import com.example.shyf_.databinding.ActivityFollowersBinding
import com.example.shyf_.model.Followers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FollowersActivity : AppCompatActivity() {

    lateinit var binding: ActivityFollowersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFollowersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //... code
        setUpAppName()
        getFollowers()
        binding.imageBack.setOnClickListener {
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this@FollowersActivity, binding.imageBack, "transition_name")
            finish()
            overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
        }
    }

    private fun setUpAppName() {
        val spannableString = SpannableString(getString(R.string.followers))
        val colorSpan =
            ForegroundColorSpan(ContextCompat.getColor(applicationContext, R.color.prim1))
        spannableString.setSpan(
            colorSpan, 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.textView8.text = spannableString
    }

    fun getFollowers() {
        var iduser = intent.getIntExtra("idUser2", 0).toString().toInt()
        val shared = getSharedPreferences("user_data", AppCompatActivity.MODE_PRIVATE)
        var idUserfollow = shared.getInt("id", 0).toString().toInt()
        val call: Call<List<Followers>> =
            RetrofitGetFollowers.getInstance().getMyApi().getFollowers(iduser)
        call.enqueue(object : Callback<List<Followers>> {
            override fun onResponse(
                call: Call<List<Followers>>, response: Response<List<Followers>>
            ) {
                val productsList: ArrayList<Followers> =
                    response.body() as ArrayList<Followers>
                val adapter =
                    Followers_Home_Adapter(this@FollowersActivity, productsList)
                val layoutManager: RecyclerView.LayoutManager =
                    LinearLayoutManager(
                        applicationContext,
                        LinearLayoutManager.VERTICAL,
                        false
                    )
                binding.listFollowers.setLayoutManager(layoutManager)
                binding.listFollowers.setHasFixedSize(true)
                binding.listFollowers.setAdapter(adapter)
                Log.e("yazan", "Created :)")
            }

            override fun onFailure(call: Call<List<Followers>>, t: Throwable) {
                binding.listFollowers.visibility = View.GONE
                binding.noData.visibility = View.VISIBLE
                Log.e("yazan", t.message!!)
            }
        })
    }
}