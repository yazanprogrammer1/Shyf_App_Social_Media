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
import com.example.shyf_.apis.RetrofitGetFollowing
import com.example.shyf_.databinding.ActivityFollowingBinding
import com.example.shyf_.model.Followers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FollowingActivity : AppCompatActivity() {

    lateinit var binding: ActivityFollowingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFollowingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //.... code
        setUpAppName()
        getFollowing()
        binding.imageBack.setOnClickListener {
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this@FollowingActivity, binding.imageBack, "transition_name")
            finish()
            overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right)
        }
    }

    private fun setUpAppName() {
        val spannableString = SpannableString(getString(R.string.following))
        val colorSpan =
            ForegroundColorSpan(ContextCompat.getColor(applicationContext, R.color.prim1))
        spannableString.setSpan(
            colorSpan, 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.textView8.text = spannableString
    }

    fun getFollowing() {
        var iduser = intent.getIntExtra("idUser2", 0).toString().toInt()
        val shared = getSharedPreferences("user_data", AppCompatActivity.MODE_PRIVATE)
        var idUserfollow = shared.getInt("id", 0).toString().toInt()
        val call: Call<List<Followers>> =
            RetrofitGetFollowing.getInstance().getMyApi().getFollowing(iduser)
        call.enqueue(object : Callback<List<Followers>> {
            override fun onResponse(
                call: Call<List<Followers>>, response: Response<List<Followers>>
            ) {
                val productsList: ArrayList<Followers> =
                    response.body() as ArrayList<Followers>
                val adapter =
                    Followers_Home_Adapter(this@FollowingActivity, productsList)
                val layoutManager: RecyclerView.LayoutManager =
                    LinearLayoutManager(
                        applicationContext,
                        LinearLayoutManager.VERTICAL,
                        false
                    )
                binding.listFollowing.setLayoutManager(layoutManager)
                binding.listFollowing.setHasFixedSize(true)
                binding.listFollowing.setAdapter(adapter)
                Log.e("yazan", "Created :)")
            }

            override fun onFailure(call: Call<List<Followers>>, t: Throwable) {
                binding.listFollowing.visibility = View.GONE
                binding.noData.visibility = View.VISIBLE
                Log.e("yazan", t.message!!)
            }
        })
    }

}