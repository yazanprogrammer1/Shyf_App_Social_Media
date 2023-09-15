package com.example.shyf_.Activitys

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.chaos.view.PinView
import com.example.shyf_.apis.RetrofitSignUp
import com.example.shyf_.databinding.ActivitySignUpBinding
import com.example.shyf_.model.Result
import com.example.shyf_.model.UserChat
import com.example.shyf_.model.UserChats
import com.example.shyf_.notification.Constants
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream


class SignUpActivity : AppCompatActivity() {

    lateinit var binding: ActivitySignUpBinding

    // This property is only valid between onCreateView and
    // onDestroyView.
    lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    lateinit var rootView: ConstraintLayout // تعريف المتغير هنا
    var token = "null"
    private val IMAGE_REQUEST = 1
    var imageUri: Uri? = null
    lateinit var user_id: String

    var code = 0
    lateinit var pinView: PinView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //..... code
        rootView = binding.rootConst
        initItem()
        onClickItem()
    }

    private fun initItem() {
        user_id = FirebaseAuth.getInstance().uid.toString()
        Log.d("YZ",user_id)
    }

    private fun onClickItem() {
        binding.btnSignUp.setOnClickListener {
            var email = binding.emailSignUp.editText!!.text.toString()
            var password = binding.passwordSignUp.editText!!.text.toString()
            var whatsappLink = binding.numberPhone.editText!!.text.toString()
            var facebookLink = binding.FacebookLink.editText!!.text.toString()
            var name = binding.nameUserSignUp.editText!!.text.toString()
            var information = binding.informationAboutUserSignUp.editText!!.text.toString()
            if (!emailValidate() || !passwordValidate() || !whatsappLinkValidate() || !facebookLinkValidate() || !nameValidate() || !informationValidate()) {
                return@setOnClickListener
            } else {
                binding.progressSignup.visibility = View.VISIBLE
                binding.btnSignUp.visibility = View.GONE
                signUpUser(email, password, whatsappLink, facebookLink, name, information)
            }
        }
    }

    private fun signUpUser(
        email: String,
        password: String,
        whatsappLink: String,
        facebookLink: String,
        nameUser: String,
        information: String
    ) {
        //retrieve data from Edit texts
        val byteArrayOutputStream = ByteArrayOutputStream()
        val base: String? = null
        val call: Call<Result> = RetrofitSignUp.getInstance().getMyApi().SignUp(
            email.toString(),
            password.toString(),
            whatsappLink.toString(),
            facebookLink.toString(),
            nameUser.toString(),
            information.toString(),
            "null",
            token
        )
        call.enqueue(object : Callback<Result> {
            override fun onResponse(call: Call<Result>, response: Response<Result>) {
                Log.d("Response --->", "User Register successfully")
                if (response.body()!!.error != true) {
                    coroutineScope.launch {
                        var userChat = UserChat(
                            user_id,
                            email,
                            password,
                            whatsappLink,
                            facebookLink,
                            nameUser,
                            information,
                            "",
                            "",
                            false,
                            "",
                            "",
                            ""
                        )
                        register(
                            userChat
                        )
                    }
                } else {
                    showCenteredMessage("Error To Sign Up!")
                    binding.progressSignup.visibility = View.GONE
                    binding.btnSignUp.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<Result>, t: Throwable) {
                Log.e("Failed to Register User --->", t.message!!)
                binding.progressSignup.visibility = View.GONE
                binding.btnSignUp.visibility = View.VISIBLE
                showCenteredMessage("Error To Sign Up!")
            }
        })
    }

    fun whatsappLinkValidate(): Boolean {
        val usernameStr: String = binding.numberPhone.getEditText()!!.getText().toString().trim()
        return if (usernameStr.isEmpty()) {
            binding.numberPhone.setError("WhatsappLink cannot be empty")
            false
        } else {
            true
        }
    }

    fun facebookLinkValidate(): Boolean {
        val usernameStr: String = binding.FacebookLink.getEditText()!!.getText().toString().trim()
        return if (usernameStr.isEmpty()) {
            binding.FacebookLink.setError("FacebookLink cannot be empty")
            false
        } else {
            true
        }
    }

    fun nameValidate(): Boolean {
        val usernameStr: String = binding.nameUserSignUp.getEditText()!!.getText().toString().trim()
        return if (usernameStr.isEmpty()) {
            binding.nameUserSignUp.setError("NameUser cannot be empty")
            false
        } else {
            true
        }
    }

    fun informationValidate(): Boolean {
        val usernameStr: String =
            binding.informationAboutUserSignUp.getEditText()!!.getText().toString().trim()
        return if (usernameStr.isEmpty()) {
            binding.informationAboutUserSignUp.setError("Information cannot be empty")
            false
        } else {
            true
        }
    }

    fun emailValidate(): Boolean {
        // username @ servise provider    domain
        val emailStr: String = binding.emailSignUp.getEditText()!!.getText().toString().trim()
        return if (emailStr.isEmpty()) {
            binding.emailSignUp.setError("email cannot be empty")
            false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailStr).matches()) {
            binding.emailSignUp.setError("Error the syntax emai")
            false
        } else {
            true
        }
    }

    fun passwordValidate(): Boolean {
        val passwordStr = binding.passwordSignUp.editText!!.text.toString().trim()
        return if (passwordStr.isEmpty()) {
            binding.passwordSignUp.error = "password cannot be empty"
            false
        } else if (passwordStr.length != 8) {
            binding.passwordSignUp.error = "A password consisting of 8 characters"
            false
        } else {
            true
        }
    }



    private suspend fun register(userChat: UserChat) {
        auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(userChat.email!!, userChat.password!!)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // تم إنشاء الحساب بنجاح، الآن سنحصل على معرف المستخدم
                    val currentUser = auth.currentUser
                    val user_id = currentUser?.uid

                    if (user_id != null) {
                        userChat.id = user_id // تعيين معرف المستخدم في الكائن UserChat
                        database = FirebaseFirestore.getInstance()
                        database.collection(Constants.KEY_COLLECTION_USER).document(user_id)
                            .set(userChat, SetOptions.merge()).addOnSuccessListener {
                                showCenteredMessage("Registered Successfully")
                                finish()
                            }.addOnFailureListener {
                                showCenteredMessage("Failed to Add Data!!")
                            }
                    } else {
                        showCenteredMessage("Failed to get User ID!!")
                    }
                } else {
                    showCenteredMessage("Registration Failed!!")
                }
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