package com.example.shyf_.Adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context.VIBRATOR_SERVICE
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.shyf_.Activitys.UpdatePostActivity
import com.example.shyf_.Activitys.UsersProfileActivity
import com.example.shyf_.R
import com.example.shyf_.apis.RetrofitAddNewRequest
import com.example.shyf_.apis.RetrofitDeleteLike
import com.example.shyf_.apis.RetrofitDeletePosts
import com.example.shyf_.apis.RetrofitDeleteSavePost
import com.example.shyf_.apis.RetrofitGetComments
import com.example.shyf_.apis.RetrofitGetIfSaved
import com.example.shyf_.apis.RetrofitIfLikeThePost
import com.example.shyf_.apis.RetrofitInsertComments
import com.example.shyf_.apis.RetrofitInsertLike
import com.example.shyf_.apis.RetrofitSavedPost
import com.example.shyf_.databinding.PostLayoutBinding
import com.example.shyf_.inite.GetUserData
import com.example.shyf_.model.Comments
import com.example.shyf_.model.Posts
import com.example.shyf_.model.ResultComments
import com.example.shyf_.model.ResultFollowers
import com.example.shyf_.model.ResultLikes
import com.example.shyf_.model.Result_AddRequest
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.thekhaeng.pushdownanim.PushDownAnim
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class Posts_Home_Adapter(
    var activity: Activity,
    var arrayList: ArrayList<Posts>,
    var rootView: ConstraintLayout,
) :
    RecyclerView.Adapter<Posts_Home_Adapter.Holder>() {

    class Holder(var binding: PostLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        var root = PostLayoutBinding.inflate(activity.layoutInflater, parent, false)
        return Holder(root)
    }

//    lateinit var rootView: ConstraintLayout // تعريف المتغير هنا

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("ResourceAsColor", "MissingInflatedId", "SetTextI18n")
    override fun onBindViewHolder(holder: Holder, position: Int) {

        // ===================================================================== init data post

        val imagePath_User = arrayList.get(position).imageUser.toString()
        val imagePath_Background = arrayList.get(position).BackgroundImage.toString()
        val imagePath_Food = arrayList.get(position).image.toString()

        val extractedTextUser = imagePath_User.substringBefore(".jpg")
        val extractedTextBackground = imagePath_Background.substringBefore(".jpg")
        val extractedTextFood = imagePath_Food.substringBefore(".jpg")
        var lastClickTime: Long = 0
        var heartAnimation: Animation =
            AnimationUtils.loadAnimation(activity, R.anim.heart_animation)

        holder.binding.nameUserPost.setText(arrayList.get(position).nameUser)
        holder.binding.txtprice.setText(arrayList.get(position).price.toString())
        // استخدام الوقت المحسوب بشكل مناسب
        val timeAgo = getTimeAgoFromString(arrayList[position].TimeAdd.toString())
        // عرض الوقت في الواجهة
        holder.binding.timePost.text = timeAgo
        Log.d("YZ", timeAgo)
        Log.d("YZ", arrayList[position].TimeAdd.toString())


        holder.binding.LocationFood.setOnClickListener {
            val bottomSheetDialog =
                BottomSheetDialog(activity, R.style.BottomSheetDialogTheme)
            val bottomSheetView = LayoutInflater.from(activity).inflate(
                R.layout.location_sheet,
                activity.findViewById<LinearLayout>(R.id.layout_dialog)
            )

            bottomSheetView.findViewById<TextView>(R.id.countryTxt)
                .setText(arrayList.get(position).country)
            bottomSheetView.findViewById<TextView>(R.id.cityTxt)
                .setText(arrayList.get(position).city)
            bottomSheetView.findViewById<TextView>(R.id.streetTxt)
                .setText(arrayList.get(position).street)

            bottomSheetDialog.setContentView(bottomSheetView)
            bottomSheetDialog.show()
        }
        holder.binding.nameDescriptionPost.setText(arrayList.get(position).description)
        holder.binding.numLikes.setText(arrayList.get(position).numLikes.toString().trim())
        holder.binding.numComments.setText(arrayList.get(position).numComments.toString().trim())
        PushDownAnim.setPushDownAnimTo(holder.binding.cardView2)
            .setScale(PushDownAnim.MODE_SCALE, 0.95f)
        PushDownAnim.setPushDownAnimTo(
            holder.binding.imageMenuPost,
            holder.binding.imgLikePost,
            holder.binding.imgCommentsPost,
            holder.binding.imgSavePost
        ).setScale(PushDownAnim.MODE_SCALE, 0.80f)

        //======================================================================================== Card image posts code

        holder.binding.cardView2.setOnClickListener {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime < 1000) { // فاصل زمني 1000 مللي ثانية (واحد ثانية)
                holder.binding.imgLikeAnimation.visibility = ImageView.VISIBLE
                holder.binding.imgLikeAnimation.startAnimation(heartAnimation)
                // انتظر لبضعة ثواني وأخفِ القلب من الشاشة
                holder.binding.imgLikeAnimation.postDelayed({
                    holder.binding.imgLikeAnimation.visibility = ImageView.INVISIBLE
                }, 2000) // فاصل زمني لعرض القلب على الشاشة (2000 مللي ثانية = 2 ثانية)
                var isLikedAnim: Boolean = false
                val shared =
                    activity.getSharedPreferences("user_data", AppCompatActivity.MODE_PRIVATE)
                var id = shared.getInt("id", 0).toString().toInt()
                val call2: Call<ResultLikes> = RetrofitIfLikeThePost.getInstance().getMyApi()
                    .getIfLike(id, arrayList.get(position).id, arrayList.get(position).userId)
                call2.enqueue(object : Callback<ResultLikes> {
                    override fun onResponse(
                        call: Call<ResultLikes>, response: Response<ResultLikes>,
                    ) {
                        if (!response.body()!!.error!!) {
                            holder.binding.imgLikePost.setImageResource(R.drawable.like_sh2)
                            isLikedAnim = true
                        } else {
                            isLikedAnim = false
                        }
                    }

                    override fun onFailure(call: Call<ResultLikes>, t: Throwable) {
                        Log.d("Retrofit ERROR -->", t.message!!)
                    }
                })
                if (isLikedAnim) {
                    holder.binding.imgLikeAnimation.visibility = ImageView.VISIBLE
                    holder.binding.imgLikeAnimation.startAnimation(heartAnimation)
                    // انتظر لبضعة ثواني وأخفِ القلب من الشاشة
                    holder.binding.imgLikeAnimation.postDelayed({
                        holder.binding.imgLikeAnimation.visibility = ImageView.INVISIBLE
                    }, 2000) // فاصل زمني لعرض القلب على الشاشة (2000 مللي ثانية = 2 ثانية)
                } else {
                    val shared =
                        activity.getSharedPreferences("user_data", AppCompatActivity.MODE_PRIVATE)
                    var id = shared.getInt("id", 0).toString().toInt()
                    val call: Call<ResultLikes> = RetrofitInsertLike.getInstance().getMyApi()
                        .InsertLikes(id, arrayList.get(position).id, arrayList.get(position).userId)
                    call.enqueue(object : Callback<ResultLikes> {
                        override fun onResponse(
                            call: Call<ResultLikes>, response: Response<ResultLikes>,
                        ) {
                            if (response.body()!!.error === false) {
                                holder.binding.imgLikeAnimation.visibility = ImageView.VISIBLE
                                holder.binding.imgLikeAnimation.startAnimation(heartAnimation)
                                // انتظر لبضعة ثواني وأخفِ القلب من الشاشة
                                holder.binding.imgLikeAnimation.postDelayed(
                                    {
                                        holder.binding.imgLikeAnimation.visibility =
                                            ImageView.INVISIBLE
                                    }, 2000
                                ) // فاصل زمني لعرض القلب على الشاشة (2000 مللي ثانية = 2 ثانية)
                                holder.binding.imgLikePost.setImageResource(R.drawable.like_sh2)
                            } else {
                                Log.e("yazan", response.body()!!.message.toString())
                            }
                        }

                        override fun onFailure(call: Call<ResultLikes>, t: Throwable) {
                            Log.e("yazan", t.message!!)
                        }
                    })
                }
            }
            lastClickTime = currentTime
        }

        //===========================================================================    .. image post long click
        holder.binding.cardView2.setOnLongClickListener {
            val popupMenu = PopupMenu(activity, it)
            popupMenu.menuInflater.inflate(R.menu.emoji_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.saveImage -> {
                        // اضف أكواد التفاعل عند اختيار الابتسامة الضاحكة
                        saveImageToGallery(holder.binding.imgFoodPost.drawable) // تمرير مصدر الصورة للدالة
                        showCenteredMessage("تم التحميل")
                        true
                    }
                    // يمكنك إضافة المزيد من الرموز التعبيرية هنا
                    else -> false
                }
            }
            popupMenu.show()
            true // يجب أن يكون true للتأكيد أن الحدث معالج ولا يتم تشغيل النقرة العادية
        }

        //========================================================================================
        if (arrayList.get(position).image.toString()
                .isEmpty() || arrayList.get(position).image.toString().equals("null")
        ) {
            holder.binding.cardView2.visibility = View.GONE
        } else {
            Glide.with(activity).load("http://10.0.2.2/db/shyf/$extractedTextFood.jpg")
                .apply(RequestOptions().override(600, 600)).error(R.drawable.img_food)
                .into(holder.binding.imgFoodPost)
        }
        val shared = activity.getSharedPreferences("user_data", AppCompatActivity.MODE_PRIVATE)
        var id = shared.getInt("id", 0).toString().toInt()
        var image = shared.getString("userImage", "").toString()

        //===================================================================== MORE CODE
        holder.binding.imageMenuPost.setOnClickListener {
            //initMediaPlayer
            var mediaPlayerClick: MediaPlayer = MediaPlayer.create(activity, R.raw.click_menu)
            mediaPlayerClick.isLooping = false
            mediaPlayerClick.start()

            if (arrayList.get(position).userId == id) {
                val bottomSheetDialog = BottomSheetDialog(activity, R.style.BottomSheetDialogTheme)
                val bottomSheetView = LayoutInflater.from(activity).inflate(
                    R.layout.buttom_sheet_layout_location_info2,
                    activity.findViewById<LinearLayout>(R.id.layout_dialog)
                )
                bottomSheetView.findViewById<Button>(R.id.btn_Open_Update).setOnClickListener {
                    val i = Intent(activity, UpdatePostActivity::class.java)
                    i.putExtra("countryUpdate", arrayList.get(position).country)
                    i.putExtra("cityUpdate", arrayList.get(position).city)
                    i.putExtra("streetUpdate", arrayList.get(position).street)
                    i.putExtra("descriptionUpdate", arrayList.get(position).description)
                    i.putExtra("imagePostUpdate", extractedTextFood)
                    i.putExtra("idPostUpdate", arrayList.get(position).id)
                    activity.startActivity(i)
                    bottomSheetDialog.dismiss()
                    notifyDataSetChanged()
                }
                bottomSheetView.findViewById<Button>(R.id.btn_deletePost).setOnClickListener {
                    //initMediaPlayer
                    var mediaPlayerClick: MediaPlayer =
                        MediaPlayer.create(activity, R.raw.delete_post)
                    mediaPlayerClick.isLooping = false
                    mediaPlayerClick.start()
                    val alertDialog = AlertDialog.Builder(activity)
                    alertDialog.setIcon(R.drawable.baseline_delete_24)
                    alertDialog.setMessage("Are you sure to delete ?")
                    alertDialog.setTitle(activity.getString(R.string.delete))
                    alertDialog.setCancelable(true)
                    alertDialog.setPositiveButton(activity.getString(R.string.delete)) { p, d ->
                        val idPost: Int = arrayList.get(position).id!!
                        val call: Call<Posts> =
                            RetrofitDeletePosts.getInstance().getMyApi().deletePostById(idPost)
                        call.enqueue(object : Callback<Posts> {
                            override fun onResponse(call: Call<Posts>, response: Response<Posts>) {
                                if (response.body()!!.error_post === false) {
                                    arrayList.remove(arrayList.get(position))
                                    showCenteredMessageDelete("Deleted")
                                    notifyDataSetChanged()
                                }
                                Log.e("yazan", "Created :)")
                            }

                            override fun onFailure(call: Call<Posts>, t: Throwable) {
                                Log.e("yazan", t.message!!)
                                showCenteredMessageDelete("Not deleted")
                            }
                        })
                        notifyDataSetChanged()
                        bottomSheetDialog.dismiss()
                    }
                    alertDialog.setNegativeButton("No") { n, d ->
                    }
                    alertDialog.create().show()
                }
                bottomSheetDialog.setContentView(bottomSheetView)
                bottomSheetDialog.show()
            } else {
                val bottomSheetDialog = BottomSheetDialog(activity, R.style.BottomSheetDialogTheme)
                val bottomSheetView = LayoutInflater.from(activity).inflate(
                    R.layout.buttom_sheet_layout_location_info,
                    activity.findViewById<LinearLayout>(R.id.layout_dialog)
                )
                bottomSheetView.findViewById<Button>(R.id.btn_Open_Profile).setOnClickListener {
//                    if (arrayList.get(position).userId == arrayList.get(position).&&) {
//
//                    }
                    val i = Intent(activity, UsersProfileActivity::class.java)
                    i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    i.putExtra("idUser", arrayList.get(position).userId)
                    i.putExtra("token", arrayList.get(position).userToken.toString())
                    i.putExtra("nameUser", arrayList.get(position).nameUser)
                    i.putExtra("imageUser", "$extractedTextUser")
                    i.putExtra("imageBackground", "$extractedTextBackground")
                    i.putExtra(
                        "WhatsappLink", arrayList.get(position).WhatsappLink.toString()
                    )
                    i.putExtra(
                        "FacebookLink", arrayList.get(position).FacebookLink.toString()
                    )
                    i.putExtra(
                        "emailUser", arrayList.get(position).emailUser.toString()
                    )
                    i.putExtra(
                        "userInfo", arrayList.get(position).Information.toString()
                    )
                    i.putExtra(
                        "numFollowers", arrayList.get(position).numFollowers
                    )
                    i.putExtra(
                        "numFollowing", arrayList.get(position).numFollowing
                    )
                    activity.startActivity(i)
                    activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    bottomSheetDialog.dismiss()
                }
                bottomSheetView.findViewById<Button>(R.id.btn_request_for_food).setOnClickListener {
                    val bottomSheetDialog =
                        BottomSheetDialog(activity, R.style.BottomSheetDialogTheme)
                    val bottomSheetView = LayoutInflater.from(activity).inflate(
                        R.layout.buttom_sheet_layout_request_food,
                        activity.findViewById<LinearLayout>(R.id.layout_dialog)
                    )
                    val vibrator: Vibrator by lazy {
                        activity.getSystemService(VIBRATOR_SERVICE) as Vibrator
                    }
                    val vibrationDuration = 100 // مدة الاهتزاز بالميلي ثانية (مثلاً 100 ميلي ثانية)

                    var count = 1
                    val basePrice = arrayList.get(position).price
                    var totalPrice = basePrice
                    var textViewCount = bottomSheetView.findViewById<TextView>(R.id.numRequest)
                    var textViewPrice = bottomSheetView.findViewById<TextView>(R.id.totalPrice)
                    textViewCount.text = count.toString()
                    textViewPrice.text = (basePrice!! * count).toString()

                    bottomSheetView.findViewById<ImageView>(R.id.image_increment)
                        .setOnClickListener {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                vibrator.vibrate(
                                    VibrationEffect.createOneShot(
                                        vibrationDuration.toLong(),
                                        VibrationEffect.DEFAULT_AMPLITUDE
                                    )
                                )
                            } else {
                                vibrator.vibrate(vibrationDuration.toLong())
                            }
                            count++
                            textViewCount = bottomSheetView.findViewById<TextView>(R.id.numRequest)
                            textViewPrice = bottomSheetView.findViewById<TextView>(R.id.totalPrice)
                            textViewCount.text = count.toString()
                            textViewPrice.text = (basePrice!! * count).toString()
                        }
                    bottomSheetView.findViewById<ImageView>(R.id.image_neg).setOnClickListener {
                        if (count > 1) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                vibrator.vibrate(
                                    VibrationEffect.createOneShot(
                                        vibrationDuration.toLong(),
                                        VibrationEffect.DEFAULT_AMPLITUDE
                                    )
                                )
                            } else {
                                vibrator.vibrate(vibrationDuration.toLong())
                            }
                            count--
                            textViewCount = bottomSheetView.findViewById<TextView>(R.id.numRequest)
                            textViewPrice = bottomSheetView.findViewById<TextView>(R.id.totalPrice)
                            textViewCount.text = count.toString()
                            textViewPrice.text = (basePrice!! * count).toString()
                        }
                    }
                    bottomSheetView.findViewById<AppCompatButton>(R.id.btn_request)
                        .setOnClickListener {
                            val shared = activity.getSharedPreferences(
                                "user_data",
                                AppCompatActivity.MODE_PRIVATE
                            )
                            var id = shared.getInt("id", 0).toString().toInt()
                            val call: Call<Result_AddRequest> =
                                RetrofitAddNewRequest.getInstance().getMyApi().InsertNewRequest(
                                    id,
                                    arrayList.get(position).id,
                                    textViewCount.text.toString().toInt(),
                                    textViewPrice.text.toString().toDouble()
                                )
                            call.enqueue(object : Callback<Result_AddRequest> {
                                override fun onResponse(
                                    call: Call<Result_AddRequest>,
                                    response: Response<Result_AddRequest>,
                                ) {
                                    if (response.body()!!.error != true) {
                                        Log.d("Response ---> ", "Add Request successfully")
                                        showCenteredMessage("Request sent ✔")
                                        bottomSheetDialog.dismiss()
                                    } else {
                                        showCenteredMessage("Request not sent ❌")
                                        bottomSheetDialog.dismiss()
                                    }
                                }

                                override fun onFailure(
                                    call: Call<Result_AddRequest>,
                                    t: Throwable,
                                ) {
                                    showCenteredMessage("Request not sent ❌")
                                    bottomSheetDialog.dismiss()
                                }
                            })
                        }

                    bottomSheetDialog.setContentView(bottomSheetView)
                    bottomSheetDialog.show()
                }
                bottomSheetView.findViewById<Button>(R.id.btn_Report).setOnClickListener {
                    showCenteredMessage("Report")
                }
                bottomSheetDialog.setContentView(bottomSheetView)
                bottomSheetDialog.show()
            }
        }

        if (arrayList.get(position).userId == id) {
            Glide.with(activity).load("http://10.0.2.2/db/shyf/${image}")
                .apply(RequestOptions().override(600, 600)).error(R.drawable.profile_sh)
                .into(holder.binding.imageUserPost)
        } else {
            Glide.with(activity).load("http://10.0.2.2/db/shyf/$extractedTextUser.jpg")
                .apply(RequestOptions().override(600, 600)).error(R.drawable.profile_sh)
                .into(holder.binding.imageUserPost)
        }

        //======================================================================= SAVED POSTS CODE
        //.. save post user
        var isSaved: Boolean = false
        val shared2 = activity.getSharedPreferences("user_data", AppCompatActivity.MODE_PRIVATE)
        var idUS = shared.getInt("id", 0).toString().toInt()
        val call: Call<ResultFollowers> = RetrofitGetIfSaved.getInstance().getMyApi()
            .getIfSaved(id, arrayList.get(position).id, arrayList.get(position).userId)
        call.enqueue(object : Callback<ResultFollowers> {
            override fun onResponse(
                call: Call<ResultFollowers>, response: Response<ResultFollowers>,
            ) {
                if (!response.body()!!.error!!) {
                    holder.binding.imgSavePost.setImageResource(R.drawable.save_sh2)
                    isSaved = true
                } else {
                    isSaved = false
                }
            }

            override fun onFailure(call: Call<ResultFollowers>, t: Throwable) {
                Log.d("Retrofit ERROR -->", t.message!!)
            }
        })

        holder.binding.imgSavePost.setOnClickListener {
            if (isSaved) {
                holder.binding.imgSavePost.setImageResource(R.drawable.save_sh)
                val shared =
                    activity.getSharedPreferences("user_data", AppCompatActivity.MODE_PRIVATE)
                var id = shared.getInt("id", 0).toString().toInt()
                val call: Call<ResultFollowers> =
                    RetrofitDeleteSavePost.getInstance().getMyApi().DeleteSavedPost(
                        id, arrayList.get(position).id, arrayList.get(position).userId
                    )
                call.enqueue(object : Callback<ResultFollowers> {
                    override fun onResponse(
                        call: Call<ResultFollowers>, response: Response<ResultFollowers>,
                    ) {
                        if (response.body()!!.error === false) {
                            holder.binding.imgSavePost.setImageResource(R.drawable.save_sh)
                            isSaved = false
                            notifyDataSetChanged()
                        } else {
                            Log.e("yazan", response.body()!!.message.toString())
                        }
                    }

                    override fun onFailure(call: Call<ResultFollowers>, t: Throwable) {
                        Log.e("yazan", t.message!!)
                    }
                })
            } else if (!isSaved) {
                val shared =
                    activity.getSharedPreferences("user_data", AppCompatActivity.MODE_PRIVATE)
                var id = shared.getInt("id", 0).toString().toInt()
                val call: Call<ResultFollowers> =
                    RetrofitSavedPost.getInstance().getMyApi().InsertSavedPost(
                        id, arrayList.get(position).id, arrayList.get(position).userId
                    )
                call.enqueue(object : Callback<ResultFollowers> {
                    override fun onResponse(
                        call: Call<ResultFollowers>, response: Response<ResultFollowers>,
                    ) {
                        if (response.body()!!.error === false) {
                            showCenteredMessage("Saved")
                            holder.binding.imgSavePost.setImageResource(R.drawable.save_sh2)
                        } else {
                            Log.e("yazan", response.body()!!.message.toString())
                        }
                    }

                    override fun onFailure(call: Call<ResultFollowers>, t: Throwable) {
                        Log.e("yazan", t.message!!)
                    }
                })
            }
        }


        //======================================================================= Like POSTS CODE
        //.. like the post user
        var isLiked: Boolean = false

        val call2: Call<ResultLikes> = RetrofitIfLikeThePost.getInstance().getMyApi()
            .getIfLike(id, arrayList.get(position).id, arrayList.get(position).userId)
        call2.enqueue(object : Callback<ResultLikes> {
            override fun onResponse(
                call: Call<ResultLikes>, response: Response<ResultLikes>,
            ) {
                if (!response.body()!!.error!!) {
                    holder.binding.imgLikePost.setImageResource(R.drawable.like_sh2)
                    isLiked = true
                } else {
                    isLiked = false
                }
            }

            override fun onFailure(call: Call<ResultLikes>, t: Throwable) {
                Log.d("Retrofit ERROR -->", t.message!!)
            }
        })

        holder.binding.imgLikePost.setOnClickListener {
            if (isLiked) {
                holder.binding.imgLikePost.setImageResource(R.drawable.like_sh1)
                val shared =
                    activity.getSharedPreferences("user_data", AppCompatActivity.MODE_PRIVATE)
                var id = shared.getInt("id", 0).toString().toInt()
                val call: Call<ResultLikes> =
                    RetrofitDeleteLike.getInstance().getMyApi().DeleteLikedPost(
                        id, arrayList.get(position).id, arrayList.get(position).userId
                    )
                call.enqueue(object : Callback<ResultLikes> {
                    override fun onResponse(
                        call: Call<ResultLikes>, response: Response<ResultLikes>,
                    ) {
                        if (response.body()!!.error === false) {
                            holder.binding.imgLikePost.setImageResource(R.drawable.like_sh1)
                            isLiked = false
                            arrayList[position].numLikes = arrayList[position].numLikes!! - 1
                            notifyDataSetChanged()
                        } else {
                            Log.e("yazan", response.body()!!.message.toString())
                        }
                    }

                    override fun onFailure(call: Call<ResultLikes>, t: Throwable) {
                        Log.e("yazan", t.message!!)
                    }
                })
            } else if (!isLiked) {
                val shared =
                    activity.getSharedPreferences("user_data", AppCompatActivity.MODE_PRIVATE)
                var id = shared.getInt("id", 0).toString().toInt()
                val call: Call<ResultLikes> = RetrofitInsertLike.getInstance().getMyApi()
                    .InsertLikes(id, arrayList.get(position).id, arrayList.get(position).userId)
                call.enqueue(object : Callback<ResultLikes> {
                    override fun onResponse(
                        call: Call<ResultLikes>, response: Response<ResultLikes>,
                    ) {
                        if (response.body()!!.error === false) {
                            showCenteredMessage("like")
                            holder.binding.imgLikePost.setImageResource(R.drawable.like_sh2)
                            arrayList[position].numLikes = arrayList[position].numLikes!! + 1
                            notifyDataSetChanged()
                        } else {
                            Log.e("yazan", response.body()!!.message.toString())
                        }
                    }

                    override fun onFailure(call: Call<ResultLikes>, t: Throwable) {
                        Log.e("yazan", t.message!!)
                    }
                })
            }
        }

        //======================================================================= Comments POSTS CODE
        holder.binding.imgCommentsPost.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(activity, R.style.BottomSheetDialogTheme)
            var bottomSheetView = LayoutInflater.from(activity).inflate(
                R.layout.buttom_sheet_comments_layout,
                activity.findViewById<LinearLayout>(R.id.layout_dialog)
            )
            getAllComments(position, bottomSheetView)
            bottomSheetView.findViewById<FrameLayout>(R.id.chatscreen_btn_send).setOnClickListener {
                var id = GetUserData(activity).getId()
                var comment =
                    bottomSheetView.findViewById<EditText>(R.id.chatsreen_et_writemessage).text.toString()
                        .trim()
                AddComment(
                    bottomSheetView.findViewById<EditText>(R.id.chatsreen_et_writemessage),
                    comment, getCurrentDateTime(), id, arrayList[position].id!!.toInt(), position
                )
                notifyDataSetChanged()
            }
            bottomSheetView.findViewById<AppCompatImageView>(R.id.chatscreen_iv_emoji)
                .setOnClickListener {

                }

            bottomSheetDialog.setContentView(bottomSheetView)
            bottomSheetDialog.show()
        }

    }
    private fun AddComment(
        field: EditText,
        comment: String,
        timeAdd: String,
        userId: Int,
        postId: Int,
        position: Int,
    ) {
        val call: Call<ResultComments> = RetrofitInsertComments.getInstance().myApi.insertComments(
            comment,
            timeAdd,
            userId,
            postId
        )
        call.enqueue(object : Callback<ResultComments> {
            override fun onResponse(
                call: Call<ResultComments>, response: Response<ResultComments>,
            ) {
                Log.d("Response ---> ", "Add Comment successfully")
                if (response.body()!!.error != true) {
                    showCenteredMessage("Added")
                    Toast.makeText(activity, "Added", Toast.LENGTH_SHORT).show()
                    field.setText("")
                    arrayList[position].numComments = arrayList[position].numComments!! + 1
                    notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<ResultComments>, t: Throwable) {
                showCenteredMessage("Not add")
                Toast.makeText(activity, "Not add", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // تحديث البيانات بإضافة التعليق الجديد إلى القائمة (arrayList)

    private fun getAllComments(position: Int, bottomSheetView: View) {
        val call: Call<ArrayList<Comments>> =
            RetrofitGetComments.getInstance().myApi.getCommentsPost(arrayList[position].id!!)
        call.enqueue(object : Callback<ArrayList<Comments>> {
            override fun onResponse(
                call: Call<ArrayList<Comments>>,
                response: Response<ArrayList<Comments>>,
            ) {
                val productsList: ArrayList<Comments> = response.body()!!
                val adapter = CommentsAdapter(
                    activity, productsList
                )
                val layoutManager: RecyclerView.LayoutManager =
                    LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
                bottomSheetView.findViewById<RecyclerView>(R.id.listComments).layoutManager =
                    layoutManager
                bottomSheetView.findViewById<RecyclerView>(R.id.listComments).visibility =
                    View.VISIBLE
                bottomSheetView.findViewById<RecyclerView>(R.id.listComments).setHasFixedSize(true)
                bottomSheetView.findViewById<RecyclerView>(R.id.listComments).adapter = adapter
                bottomSheetView.findViewById<ShimmerFrameLayout>(R.id.shimmerComments).visibility =
                    View.GONE
                Log.e("yazan", "Created comments:)")
            }

            override fun onFailure(call: Call<ArrayList<Comments>>, t: Throwable) {
                Log.e("yazan", t.message!!)
                bottomSheetView.findViewById<ConstraintLayout>(R.id.NoDataRvComments).visibility =
                    View.VISIBLE
                bottomSheetView.findViewById<ShimmerFrameLayout>(R.id.shimmerComments).visibility =
                    View.GONE
            }
        })
    }

    private fun getCurrentDateTime(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val currentDate = Date()
        return dateFormat.format(currentDate)
    }

    // تحويل الوقت من النص إلى الشكل المطلوب
    fun getTimeAgoFromString(timeString: String): String {
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

    // دالة حفظ الصورة في معرض الصور
    private fun saveImageToGallery(imageResource: Drawable) {
//        val imageDrawable = ContextCompat.getDrawable(activity, imageResource)
        lateinit var progressDialog: ProgressDialog
        progressDialog = ProgressDialog(activity)
        progressDialog.setMessage("Saving Image...")
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        progressDialog.setCancelable(false)

        if (imageResource is BitmapDrawable) {
            val bitmap = imageResource.bitmap
            val contentValues = ContentValues().apply {
                put(
                    MediaStore.Images.Media.DISPLAY_NAME,
                    "MyImage_${System.currentTimeMillis()}.jpg"
                )
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/MyApp")
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }
            }

            val resolver = activity.contentResolver
            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

            uri?.let { it ->
                progressDialog.show()
                val outputStream: OutputStream? = resolver.openOutputStream(it)
                outputStream?.use {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                }

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    contentValues.clear()
                    contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                    resolver.update(uri, contentValues, null, null)
                }
                progressDialog.dismiss()
            }
        }
    }

    private fun showCenteredMessage(message: String) {
        // تغيير R.id.rootLayout إلى ID اللايوت الرئيسي لديك
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

    private fun showCenteredMessageDelete(message: String) {
        rootView =
            activity.findViewById(R.id.rootLayout)
        // تغيير R.id.rootLayout إلى ID اللايوت الرئيسي لديك
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

        // تغيير لون النص إلى الأحمر
        val redColor = ContextCompat.getColor(activity, android.R.color.holo_red_dark)
        snackbar.setActionTextColor(redColor)

        snackbar.show()
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }


}