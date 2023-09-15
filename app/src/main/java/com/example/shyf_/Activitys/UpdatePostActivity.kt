package com.example.shyf_.Activitys

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Base64
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.shyf_.R
import com.example.shyf_.apis.RetrofitUpdatePost
import com.example.shyf_.databinding.ActivityUpdatePostBinding
import com.example.shyf_.model.Posts
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.IOException


class UpdatePostActivity : AppCompatActivity() {

    lateinit var binding: ActivityUpdatePostBinding
    lateinit var launcher: ActivityResultLauncher<Intent>
    var bitmap: Bitmap? = null
    lateinit var Country_Post: String
    lateinit var descriptionAddPost: String
    lateinit var City_Post: String
    lateinit var street_Post: String
    lateinit var image_Post: String
    lateinit var rootView: ConstraintLayout // تعريف المتغير هنا
    private val coroutineScope = CoroutineScope(Dispatchers.IO)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdatePostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //.. code update post
        launcher = registerForActivityResult<Intent, ActivityResult>(
            ActivityResultContracts.StartActivityForResult(),
            object : ActivityResultCallback<ActivityResult?> {
                override fun onActivityResult(result: ActivityResult?) {
                    if (result!!.resultCode == Activity.RESULT_OK) {
                        val ii = result.data
                        val uri = ii!!.data
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(
                                this@UpdatePostActivity.contentResolver,
                                uri
                            )
                            binding.imgFoodPost.setImageBitmap(bitmap)
                        } catch (e: IOException) {
                            throw RuntimeException(e)
                        }
                    }
                }
            })
        //... code update
        rootView = binding.rootConst
        init()
        setUpAppName()
        onClickItem()
    }

    private fun setUpAppName() {
        val spannableString = SpannableString(getString(R.string.update_post))
        val colorSpan =
            ForegroundColorSpan(ContextCompat.getColor(applicationContext, R.color.prim1))
        spannableString.setSpan(colorSpan, 0, 6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.title.text = spannableString
    }

    fun init() {
        descriptionAddPost = intent.getStringExtra("descriptionUpdate").toString()
        Country_Post = intent.getStringExtra("countryUpdate").toString()
        City_Post = intent.getStringExtra("cityUpdate").toString()
        street_Post = intent.getStringExtra("streetUpdate").toString()
        image_Post = intent.getStringExtra("imagePostUpdate").toString()
        binding.CountryPost.editText!!.setText(Country_Post)
        binding.CityPost.editText!!.setText(City_Post)
        binding.streetPost.editText!!.setText(street_Post)
        binding.descriptionAddPost.editText!!.setText(descriptionAddPost)
        if (image_Post.isEmpty() || image_Post.equals("null")) {
            binding.cardView2.visibility = View.GONE
            binding.btnAddPhoto.visibility = View.VISIBLE
        } else {
            binding.cardView2.visibility = View.VISIBLE
            binding.btnAddPhoto.visibility = View.GONE
            Glide.with(this)
                .load("http://10.0.2.2/db/shyf/$image_Post.jpg")
                .apply(RequestOptions().override(600, 600))
                .error(R.drawable.img_food)
                .into(binding.imgFoodPost)
            bitmap = (binding.imgFoodPost.getDrawable() as BitmapDrawable).bitmap
        }
    }

    @SuppressLint("MissingInflatedId")
    fun onClickItem() {
        binding.imgFoodPost.setOnClickListener {
            val i = Intent(Intent.ACTION_PICK)
            i.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            launcher.launch(i)
        }
        binding.btnAddPhoto.setOnClickListener {
            binding.cardView2.visibility = View.VISIBLE
            binding.btnAddPhoto.visibility = View.GONE
        }
        binding.btnCancel.setOnClickListener {
            binding.cardView2.visibility = View.GONE
            binding.btnAddPhoto.visibility = View.VISIBLE
        }
        binding.btnUpdatePost.setOnClickListener {
            if (binding.descriptionAddPost.editText!!.text.toString().isNotEmpty()
                && binding.CountryPost.editText!!.text.toString().isNotEmpty()
                && binding.CityPost.editText!!.text.toString().isNotEmpty()
                && binding.streetPost.editText!!.text.toString().isNotEmpty()
            ) {
                coroutineScope.launch {
                    updatePost()
                }
            } else {
                Toast.makeText(applicationContext, "Type the post information", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private suspend fun updatePost() {
        if (binding.cardView2.visibility.equals(View.VISIBLE)) {
            val byteArrayOutputStream = ByteArrayOutputStream()
            val base: String? = null
            if (bitmap == null) {
//                bitmap = binding.imgFoodPost.getDrawable().toBitmap()
//                binding.imgFoodPost.setImageBitmap(bitmap)
                Toast.makeText(applicationContext, "Select Photo", Toast.LENGTH_SHORT).show()
            } else {
                bitmap!!.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
                val bytes = byteArrayOutputStream.toByteArray()
                val base46Image = Base64.encodeToString(bytes, Base64.DEFAULT)
                var id = intent.getIntExtra("idPostUpdate", 0)
                //retrieve data from Edit texts
                //Here we will handle the http request to insert user to mysql db
                val call: Call<Posts> =
                    RetrofitUpdatePost.getInstance().getMyApi().updatePost(
                        id,
                        binding.CountryPost.editText!!.text.toString().trim(),
                        binding.CityPost.editText!!.text.toString().trim(),
                        binding.streetPost.editText!!.text.toString().trim(),
                        binding.descriptionAddPost.editText!!.text.toString().trim(),
                        base46Image
                    )
                call.enqueue(object : Callback<Posts> {
                    override fun onResponse(
                        call: Call<Posts>,
                        response: Response<Posts>
                    ) {
                        Log.d("Response ---> ", "Update Post successfully")
                        if (response.body()!!.error_post != true) {
                            val shared = getSharedPreferences(
                                "user_data",
                                AppCompatActivity.MODE_PRIVATE
                            )
                            val editor = shared.edit()
                            editor.putString(
                                "descriptionAddPost",
                                binding.descriptionAddPost.editText!!.text.toString().trim()
                            )
                            editor.putString(
                                "CountryPost",
                                binding.CountryPost.editText!!.text.toString().trim()
                            )
                            editor.putString(
                                "CityPost",
                                binding.CityPost.editText!!.text.toString().trim()
                            )
                            editor.putString(
                                "streetPost",
                                binding.streetPost.editText!!.text.toString().trim()
                            )
                                .apply()
                            finish()
                            showCenteredMessage("Updated")
                        }
                    }

                    override fun onFailure(call: Call<Posts>, t: Throwable) {
                        Log.e("Failed to Register Post ---> ", t.message!!)
                        showCenteredMessage("Not updated")
                    }
                })
            }
        } else if (binding.cardView2.visibility.equals(View.GONE)) {
            //retrieve data from Edit texts
            //Here we will handle the http request to insert user to mysql db
            var id = intent.getIntExtra("idPostUpdate", 0)
            val call: Call<Posts> =
                RetrofitUpdatePost.getInstance().getMyApi().updatePost(
                    id,
                    binding.CountryPost.editText!!.text.toString().trim(),
                    binding.CityPost.editText!!.text.toString().trim(),
                    binding.streetPost.editText!!.text.toString().trim(),
                    binding.descriptionAddPost.editText!!.text.toString().trim(),
                    "null"
                )
            call.enqueue(object : Callback<Posts> {
                override fun onResponse(
                    call: Call<Posts>,
                    response: Response<Posts>
                ) {
                    Log.d("Response ---> ", "Add Post successfully")
                    if (response.body()!!.error_post != true) {
                        val shared = getSharedPreferences(
                            "user_data",
                            AppCompatActivity.MODE_PRIVATE
                        )
                        val editor = shared.edit()
                        editor.putString(
                            "descriptionAddPost",
                            binding.descriptionAddPost.editText!!.text.toString().trim()
                        )
                        editor.putString(
                            "CountryPost",
                            binding.CountryPost.editText!!.text.toString().trim()
                        )
                        editor.putString(
                            "CityPost",
                            binding.CityPost.editText!!.text.toString().trim()
                        )
                        editor.putString(
                            "streetPost",
                            binding.streetPost.editText!!.text.toString().trim()
                        )
                            .apply()
                        finish()
                        showCenteredMessage("Updated")
                    }
                }

                override fun onFailure(call: Call<Posts>, t: Throwable) {
                    Log.e("Failed to Register Post ---> ", t.message!!)
                    showCenteredMessage("Not updated")
                }
            })
        }
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

}