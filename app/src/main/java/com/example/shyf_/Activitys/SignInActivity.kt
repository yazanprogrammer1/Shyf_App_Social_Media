package com.example.shyf_.Activitys

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.example.shyf_.apis.RetrofitSignIn
import com.example.shyf_.databinding.ActivitySignInBinding
import com.example.shyf_.model.Result
import com.example.shyf_.model.User
import com.example.shyf_.notification.Constants
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
    var token = ""
    lateinit var auth: FirebaseAuth
    private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    var userIdSignIn: String = ""
    lateinit var uid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //... code sign in

        auth = FirebaseAuth.getInstance()
        rootView = binding.rootConst
        onClickItem()
    }

    override fun onStart() {
        super.onStart()
//        Log.d("YZ", auth.uid!!)
        getUserToken()
        Log.d("YZ", getUserIdFb())
    }

    private fun onClickItem() {
        binding.txtGoToSignUp.setOnClickListener {
            val i = Intent(applicationContext, SignUpActivity::class.java)
            startActivity(i)
        }
        binding.btnLogin.setOnClickListener {
            if (checkInput()) {
                loginFirebase()
            }
        }
    }

    private fun getUserIdFb(): String {
        val currentUser = auth.uid
        if (currentUser != null) {
            // استخراج معرف المستخدم (UID)
            uid = currentUser
            // يمكنك استخدام مُعرف المستخدم (uid) هنا في نشاطك
            // على سبيل المثال، يمكنك تخزينه أو استخدامه كمعرف فريد للمستخدم
        } else {
            // المستخدم ليس مسجل الدخول، يمكنك تنفيذ الإجراءات اللازمة في هذا الحالة
            uid = "null"
        }
        return uid!!
    }

    private fun loginFirebase() {
        binding.progressSignIn.visibility = View.VISIBLE
        val email: String = binding.emailSignIn.editText!!.text.toString().trim()
        val password = binding.passwordSignIn.editText!!.text.toString().trim()
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                userSignIn()
//                updateUserTokenInFirestore(auth.currentUser!!.uid, token)
            } else {
                showCenteredMessage("Error To Login")
                binding.progressSignIn.visibility = View.GONE
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

    private fun updateUserTokenInFirestore(userId: String, newToken: String) {
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection(Constants.KEY_COLLECTION_USER).document(userId)

        val tokenData = hashMapOf<String, Any>(
            "userToken" to newToken
        )
        userRef.update(tokenData)
            .addOnSuccessListener {
                // تم تحديث التوكن بنجاح
                // يمكنك أيضًا إجراء أي عمليات إضافية هنا بعد التحديث
                Toast.makeText(applicationContext, "token updated", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                // حدث خطأ أثناء تحديث التوكن
                Log.e("Firestore Update Error", e.message, e)
            }
    }

    fun getUserToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                token = task.result.toString()
                showCenteredMessage("Token Done")
            } else {
                showCenteredMessage("No token")
            }
            //this is the token retrieved
        }
    }

    private fun updateToken(token: String) {
        val shared = getSharedPreferences("user_data", MODE_PRIVATE)
        val editor = shared.edit()
        editor.putString(
            "userToken", token
        )
        val database = FirebaseFirestore.getInstance()
        database.collection(Constants.KEY_COLLECTION_USER)
            .document(auth.currentUser!!.uid).update("userToken", token)
            .addOnSuccessListener {
                Log.d("TOKEN UPDATE STATUS", "Token Updated Successfully")
            }.addOnFailureListener {
                Log.d("TOKEN UPDATE STATUS", "Failed to Update The Token")
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
        } else if (binding.passwordSignIn.editText!!.text.toString().length > 8) {
            binding.passwordSignIn.error = "Password can't >8!"
            return false
        }
        return true
    }

}