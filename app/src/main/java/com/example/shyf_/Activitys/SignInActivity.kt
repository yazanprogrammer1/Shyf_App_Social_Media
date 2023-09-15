package com.example.shyf_.Activitys

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.example.shyf_.apis.RetrofitSignIn
import com.example.shyf_.apis.RetrofitUpdateToken
import com.example.shyf_.databinding.ActivitySignInBinding
import com.example.shyf_.model.Result
import com.example.shyf_.model.User
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignInActivity : AppCompatActivity() {

    lateinit var binding: ActivitySignInBinding
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    lateinit var rootView: ConstraintLayout // تعريف المتغير هنا
    var Token = ""
    lateinit var auth: FirebaseAuth
    private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //... code sign in
        auth = Firebase.auth
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Token = task.result.toString()
//                showCenteredMessage("Token Done")
            } else {
//                showCenteredMessage("No token")
            }
            //this is the token retrieved
        }
        rootView = binding.rootConst
        onClickItem()

    }

    private fun onClickItem() {
        binding.txtGoToSignUp.setOnClickListener {
            val i = Intent(applicationContext, SignUpActivity::class.java)
            startActivity(i)
        }
        binding.btnLogin.setOnClickListener {
            if (checkInput()) {
                loginFirebase()
                updateToken()
            }
        }
    }

    private fun loginFirebase() {
        binding.progressSignIn.setVisibility(View.VISIBLE)
        val email: String = binding.emailSignIn.editText!!.text.toString().trim()
        val password = binding.passwordSignIn.editText!!.text.toString().trim()
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                userSignIn()
            } else {
                showCenteredMessage("Error To Login")
                binding.progressSignIn.setVisibility(View.GONE)
            }
        }
    }


    private fun userSignIn() {
        val email: String = binding.emailSignIn.editText!!.text.toString().trim()
        val password = binding.passwordSignIn.editText!!.text.toString().trim()
        val call: Call<Result> = RetrofitSignIn.getInstance().getMyApi().SignIn(email, password)
        call.enqueue(object : Callback<Result> {
            override fun onResponse(call: Call<Result>, response: Response<Result>) {
                if (!response.body()!!.error!!) {
                    binding.progressSignIn.setVisibility(View.INVISIBLE)
                    binding.txtError.visibility = View.GONE
                    val user = User(
                        response.body()!!.user!!.id.toString().toInt(),
                        response.body()!!.user!!.email.toString(),
                        response.body()!!.user!!.password.toString(),
                        response.body()!!.user!!.whatsappLink.toString(),
                        response.body()!!.user!!.facebookLink.toString(),
                        response.body()!!.user!!.name.toString(),
                        response.body()!!.user!!.information.toString(),
                        response.body()!!.user!!.backgroundImage.toString(),
                        response.body()!!.user!!.userImage.toString(),
                        response.body()!!.user!!.numFollowers,
                        response.body()!!.user!!.numFollowing,
                        response.body()!!.user!!.userToken
                    )
                    val imagePath_User = "${response.body()!!.user!!.userImage.toString()}"
                    val imagePath_Background =
                        "${response.body()!!.user!!.backgroundImage.toString()}"
                    val extractedTextUser = imagePath_User.substringBefore(".jpg")
                    val extractedTextBackground = imagePath_Background.substringBefore(".jpg")

                    val shared = getSharedPreferences("user_data", MODE_PRIVATE)
                    val editor = shared.edit()
                    editor.putBoolean("isSign", true)
                    editor.putString(
                        "userToken",
                        response.body()!!.user!!.userToken.toString()
                    )
                    editor.putInt("id", response.body()!!.user!!.id.toString().toInt())
                    editor.putString("email", response.body()!!.user!!.email.toString())
                    editor.putString("password", response.body()!!.user!!.password.toString())
                    editor.putString(
                        "whatsappLink",
                        response.body()!!.user!!.whatsappLink.toString()
                    )
                    editor.putString(
                        "facebookLink",
                        response.body()!!.user!!.facebookLink.toString()
                    )
                    editor.putString("name", response.body()!!.user!!.name.toString())
                    editor.putString("information", response.body()!!.user!!.information.toString())
//                    editor.putInt(
//                        "numFollowers",
//                        response.body()!!.user!!.numFollowers!!
//                    )
//                    editor.putInt(
//                        "numFollowing",
//                        response.body()!!.user!!.numFollowing!!
//                    )
                    editor.putString(
                        "backgroundImage",
                        "$extractedTextBackground.jpg"
                    )
                    editor.putString("userImage", "$extractedTextUser.jpg")
                        .apply()
                    val i = Intent(this@SignInActivity, MainActivity::class.java)
                    i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(i)
                    finish()
                    showCenteredMessage("Done")
                } else {
                    showCenteredMessage("Invalid email or password")
                    binding.progressSignIn.setVisibility(View.INVISIBLE)
                }
            }

            override fun onFailure(call: Call<Result>, t: Throwable) {
                binding.txtError.visibility = View.VISIBLE
                Log.d("Retrofit ERROR -->", t.message!!)
                binding.progressSignIn.setVisibility(View.INVISIBLE)
            }
        })
    }

    private fun updateToken() {
        val email: String = binding.emailSignIn.editText!!.text.toString().trim()
        val password = binding.passwordSignIn.editText!!.text.toString().trim()
        val call: Call<Result> =
            RetrofitUpdateToken.getInstance().getMyApi()
                .UpdateToken(email, password, Token)
        call.enqueue(object : Callback<Result> {
            override fun onResponse(call: Call<Result>, response: Response<Result>) {
                if (!response.body()!!.error!!) {
                    val shared = getSharedPreferences("user_data", MODE_PRIVATE)
                    val editor = shared.edit()
                    editor.putString(
                        "userToken",
                        response.body()!!.user!!.userToken.toString()
                    )
                        .apply()
//                    showCenteredMessage("Update Token \n ${response.body()!!.user!!.userToken.toString()}")
                }
            }

            override fun onFailure(call: Call<Result>, t: Throwable) {
                Log.d("Token Not Updated -->", t.message!!)
            }
        })
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


    private fun checkInput(): Boolean {
        if (binding.emailSignIn.editText!!.text.isEmpty()) {
            binding.emailSignIn.error = "Email can't empty!"
            return false
        } else if (!binding.emailSignIn.editText!!.text.matches(emailPattern.toRegex())) {
            binding.emailSignIn.error = "Enter Valid Email"
            return false
        } else if (binding.passwordSignIn.editText!!.text.isEmpty()) {
            binding.passwordSignIn.error = "Password can't empty!"
            return false
        } else if (binding.passwordSignIn.editText!!.text.toString().length>8) {
            binding.passwordSignIn.error = "Password can't >8!"
            return false
        }
        return true
    }

}