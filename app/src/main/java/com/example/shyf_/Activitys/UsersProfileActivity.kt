package com.example.shyf_.Activitys

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.finalproject_kotlin.Data_Base.Data_Base_Holder
import com.example.shyf_.Adapter.Posts_Home_Adapter
import com.example.shyf_.R
import com.example.shyf_.apis.RetrofitAddFollow
import com.example.shyf_.apis.RetrofitDeleteFollow
import com.example.shyf_.apis.RetrofitGetPostsForUser
import com.example.shyf_.apis.RetrofitIfFollow
import com.example.shyf_.databinding.ActivityUsersProfileBinding
import com.example.shyf_.inite.GetIfUserOnline
import com.example.shyf_.inite.GetUserData
import com.example.shyf_.model.Posts
import com.example.shyf_.model.ResultFollow
import com.example.shyf_.model.ResultFollowers
import com.example.shyf_.model.UserChats
import com.example.shyf_.notification.Cons
import com.github.chrisbanes.photoview.PhotoView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class UsersProfileActivity : AppCompatActivity() {

    lateinit var binding: ActivityUsersProfileBinding
    lateinit var data_base: Data_Base_Holder
    lateinit var rootView: ConstraintLayout // تعريف المتغير هنا
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    var user: UserChats = UserChats()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsersProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //.... code
        rootView = binding.rootConst
        data_base = Data_Base_Holder(this)
        setUpAppName()
        init()
        coroutineScope.launch {
            getPostsUser()
            getIfFollow()
        }
    }

    @SuppressLint("SetTextI18n", "MissingInflatedId")
    private fun init() {
        var imageUser = intent.getStringExtra("imageUser")
        var imageBackground = intent.getStringExtra("imageBackground")
        binding.numFollowers.text = intent.getIntExtra("numFollowers", 0).toString()
        binding.numFollowing.text = intent.getIntExtra("numFollowing", 0).toString()
        Glide.with(applicationContext).load("http://10.0.2.2/db/shyf/$imageUser.jpg")
            .apply(RequestOptions().override(600, 600)).error(R.drawable.img_food)
            .into(binding.imageUserDetails)

        Glide.with(this).load("http://10.0.2.2/db/shyf/$imageBackground.jpg")
            .apply(RequestOptions().override(600, 600)).error(R.drawable.backgroud_img)
            .into(binding.imageBackgroundUserDetails)
        var whatsapp = intent.getStringExtra("WhatsappLink")
        var facebook = intent.getStringExtra("FacebookLink")
        var email = intent.getStringExtra("emailUser")
        var desc = intent.getStringExtra("description")
        var numFollwers = intent.getIntExtra("numFollowers", 0).toString()
        var numFollwing = intent.getIntExtra("numFollowing", 0).toString()

        binding.imgWhatsApp.setOnClickListener {

        }
        binding.imgFacebook.setOnClickListener {

        }
        binding.imgEmail.setOnClickListener {

        }
        binding.imageBack.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
        binding.infoUser.text = intent.getStringExtra("userInfo")
        binding.nameUser.text = intent.getStringExtra("nameUser")

        binding.numFollowers.setOnClickListener {
            val i = Intent(this, FollowersActivity::class.java)
            i.putExtra("idUser2", intent.getIntExtra("idUser", 0).toString().toInt())
            startActivity(i)
        }
        binding.numFollowing.setOnClickListener {
            val i = Intent(this, FollowingActivity::class.java)
            i.putExtra("idUser2", intent.getIntExtra("idUser", 0).toString().toInt())
            startActivity(i)
        }
        binding.btnFollowUserr.setOnClickListener {
            var idUserfollow = intent.getIntExtra("idUser", 0).toString().toInt()
            var nameUser = intent.getStringExtra("nameUser").toString()
            val shared = getSharedPreferences("user_data", AppCompatActivity.MODE_PRIVATE)
            var iduser = shared.getInt("id", 0).toString().toInt()
            coroutineScope.launch {
                addFollow(iduser, idUserfollow, nameUser)
            }
        }
        binding.btnUnfollowUser.setOnClickListener {
            var idUserfollow = intent.getIntExtra("idUser", 0).toString().toInt()
            val shared = getSharedPreferences("user_data", AppCompatActivity.MODE_PRIVATE)
            var iduser = shared.getInt("id", 0).toString().toInt()
            coroutineScope.launch {
                deleteFollow(iduser, idUserfollow)
            }
        }
        binding.imageUserDetails.setOnLongClickListener {
            showZoomableImage(binding.imageUserDetails.drawable)
            true // تم التعامل مع الحدث بنجاح
        }
        binding.imageBackgroundUserDetails.setOnLongClickListener {
            showZoomableImage(binding.imageBackgroundUserDetails.drawable)
            true // تم التعامل مع الحدث بنجاح
        }
    }


    private fun showZoomableImage(imageResource: Drawable) {
        val dialog = Dialog(this@UsersProfileActivity)
        dialog.setContentView(R.layout.dialog_zoomable_image)

        val zoomableImageView: PhotoView = dialog.findViewById(R.id.zoomableImageView)
        zoomableImageView.setImageDrawable(imageResource)

        dialog.show()
    }

    private fun showCenteredMessage(message: String) {
        val snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_LONG)
        val snackbarView = snackbar.view
        // تعديل حجم الـ snackbarView ليكون حسب حجم الكلمة الموجودة فيه
        snackbarView.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
        snackbarView.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        // تعديل تخطيط الرسالة لتوسيطها في وسط الشاشة
        val params = snackbarView.layoutParams as CoordinatorLayout.LayoutParams
        params.gravity = Gravity.CENTER
        snackbarView.layoutParams = params
        // تعيين مدة ظهور الـ Snackbar إلى ثانيتين
//        snackbar.duration = 2000 // بالمللي ثانية
        snackbar.show()
    }

    private suspend fun getIfFollow() {
        var idUserfollow = intent.getIntExtra("idUser", 0).toString().toInt()
        val shared = getSharedPreferences("user_data", AppCompatActivity.MODE_PRIVATE)
        var iduser = shared.getInt("id", 0).toString().toInt()
        val call: Call<ResultFollow> =
            RetrofitIfFollow.getInstance().getMyApi().getIfFollow(iduser, idUserfollow)
        call.enqueue(object : Callback<ResultFollow> {
            override fun onResponse(call: Call<ResultFollow>, response: Response<ResultFollow>) {
                if (!response.body()!!.error!!) {
                    binding.btnUnfollowUser.visibility = View.VISIBLE
                    binding.btnFollowUserr.visibility = View.GONE
                } else {
                    binding.btnUnfollowUser.visibility = View.GONE
                    binding.btnFollowUserr.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<ResultFollow>, t: Throwable) {
                Log.d("Retrofit ERROR -->", t.message!!)
            }
        })
    }

    private suspend fun addFollow(iduser: Int, idUserfollow: Int, nameUser: String) {
        val call: Call<ResultFollowers> =
            RetrofitAddFollow.getInstance().getMyApi().Insertfollowers(iduser, idUserfollow)
        call.enqueue(object : Callback<ResultFollowers> {
            override fun onResponse(
                call: Call<ResultFollowers>, response: Response<ResultFollowers>
            ) {
                if (response.body()!!.error === false) {
                    showCenteredMessage(getString(R.string.follow))
                    binding.btnUnfollowUser.visibility = View.VISIBLE
                    binding.btnFollowUserr.visibility = View.GONE
                    val shared = getSharedPreferences("user_data", AppCompatActivity.MODE_PRIVATE)
                    var nameUser = shared.getString("name", "").toString()
                    Log.d("yazan", intent.getStringExtra("token").toString())

                }
                Log.e("yazan", "Created :)")
            }

            override fun onFailure(call: Call<ResultFollowers>, t: Throwable) {
                Log.e("yazan", t.message!!)
            }
        })
    }

    private suspend fun deleteFollow(iduser: Int, idUserfollow: Int) {
        val call: Call<ResultFollowers> =
            RetrofitDeleteFollow.getInstance().getMyApi().Deletefollowers(iduser, idUserfollow)
        call.enqueue(object : Callback<ResultFollowers> {
            override fun onResponse(
                call: Call<ResultFollowers>, response: Response<ResultFollowers>
            ) {
                if (response.body()!!.error === false) {
                    binding.btnUnfollowUser.visibility = View.GONE
                    binding.btnFollowUserr.visibility = View.VISIBLE
                    val isDelete = data_base.delete_follower(
                        iduser, idUserfollow
                    )
                    if (isDelete) {
                        showCenteredMessage(getString(R.string.unfollow))
                        binding.btnUnfollowUser.visibility = View.GONE
                        binding.btnFollowUserr.visibility = View.VISIBLE
                    } else {
                        showCenteredMessage(getString(R.string.unfollow))
                    }
                }
                Log.e("yazan", "Created :)")
            }

            override fun onFailure(call: Call<ResultFollowers>, t: Throwable) {
                Log.e("yazan", t.message!!)
            }
        })
    }

    private suspend fun getPostsUser() {
        var id_user = intent.getIntExtra("idUser", 0)
        val call: Call<List<Posts>> =
            RetrofitGetPostsForUser.getInstance().myApi.getPostsOfUser(id_user)
        call.enqueue(object : Callback<List<Posts>> {
            override fun onResponse(call: Call<List<Posts>>, response: Response<List<Posts>>) {
                val productsList: ArrayList<Posts> = response.body() as ArrayList
                val adapter =
                    Posts_Home_Adapter(this@UsersProfileActivity, productsList, binding.rootConst)
                val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(
                    this@UsersProfileActivity, LinearLayoutManager.VERTICAL, false
                )
                binding.listPopularPosts.setLayoutManager(layoutManager)
                binding.listPopularPosts.setHasFixedSize(true)
                binding.listPopularPosts.setAdapter(adapter)
                binding.shimmer.stopShimmer()
                binding.shimmer.setVisibility(View.GONE)
                binding.listPopularPosts.setVisibility(View.VISIBLE)
                binding.noData.visibility = View.GONE
                Log.e("yazan", "Created :)")
            }

            override fun onFailure(call: Call<List<Posts>>, t: Throwable) {
                binding.noData.visibility = View.VISIBLE
                Log.e("yazan", t.message!!)
                binding.shimmer.stopShimmer()
                binding.shimmer.setVisibility(View.GONE)
                binding.listPopularPosts.setVisibility(View.VISIBLE)
            }
        })
    }

    private fun setUpAppName() {
        val spannableString = SpannableString(getString(R.string.posts))
        val colorSpan =
            ForegroundColorSpan(ContextCompat.getColor(applicationContext, R.color.prim1))
        spannableString.setSpan(
            colorSpan, 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.textView6.text = spannableString
    }




}