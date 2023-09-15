package com.example.shyf_.ui.profile

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.shyf_.Activitys.FollowersActivity
import com.example.shyf_.Activitys.FollowingActivity
import com.example.shyf_.Activitys.SignInActivity
import com.example.shyf_.Adapter.ViewPagerProfileAdapter
import com.example.shyf_.R
import com.example.shyf_.apis.RetrofitGetNumberFollow
import com.example.shyf_.apis.RetrofitUpdateUser
import com.example.shyf_.apis.RetrofitUpdateUserImage
import com.example.shyf_.apis.RetrofitUpdateUserImageBackground
import com.example.shyf_.databinding.FragmentProfileBinding
import com.example.shyf_.model.Result
import com.example.shyf_.notification.Constants
import com.github.chrisbanes.photoview.PhotoView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.thekhaeng.pushdownanim.PushDownAnim
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.IOException

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var email: String
    lateinit var password: String
    lateinit var whatsappLink: String
    lateinit var facebookLink: String
    lateinit var name: String
    lateinit var information: String
    lateinit var backgroundImage: String
    lateinit var userImage: String
    lateinit var bottomSheetView: View
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    lateinit var rootView: ConstraintLayout // تعريف المتغير هنا

    lateinit var user_id: String
    var selectedImageUri: Uri? = null  // هنا يجب أن تكون Uri للصورة المختارة
    lateinit var storageReference: StorageReference
    lateinit var firebaseUser: FirebaseUser
    lateinit var auth: FirebaseAuth
    lateinit var databaseReference: DatabaseReference


    var bitmap: Bitmap? = null
    var bitmap2: Bitmap? = null
    lateinit var launcher: ActivityResultLauncher<Intent>
    lateinit var launcher2: ActivityResultLauncher<Intent>
    var pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            // Image Selection
            bitmap2 = MediaStore.Images.Media.getBitmap(
                requireActivity().contentResolver, uri
            )
            selectedImageUri = uri
            binding.imageUserSignUp.setImageBitmap(bitmap2)
            Log.d("YZ", selectedImageUri.toString())
        } else {
            // No image

        }
    }
    var pickMedia2 = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            // Image Selection
            bitmap = MediaStore.Images.Media.getBitmap(
                requireActivity().contentResolver, uri
            )
            binding.imageBackgroundUserSignUp.setImageBitmap(bitmap)
        } else {
            // No image

        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //................
        auth = Firebase.auth
        storageReference = FirebaseStorage.getInstance().getReference("uploads")
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        val currentUser = auth.currentUser
        user_id = currentUser?.uid!!
        launcher =
            registerForActivityResult<Intent, ActivityResult>(ActivityResultContracts.StartActivityForResult(),
                object : ActivityResultCallback<ActivityResult?> {
                    override fun onActivityResult(result: ActivityResult?) {
                        if (result!!.resultCode == Activity.RESULT_OK) {
                            val ii = result.data
                            val uri = ii!!.data
                            try {
                                bitmap2 = MediaStore.Images.Media.getBitmap(
                                    requireActivity().contentResolver, uri
                                )
                                binding.imageUserSignUp.setImageBitmap(bitmap2)
                            } catch (e: IOException) {
                                throw RuntimeException(e)
                            }
                        }
                    }
                })

        launcher2 =
            registerForActivityResult<Intent, ActivityResult>(ActivityResultContracts.StartActivityForResult(),
                object : ActivityResultCallback<ActivityResult?> {
                    override fun onActivityResult(result: ActivityResult?) {
                        if (result!!.resultCode == Activity.RESULT_OK) {
                            val ii = result.data
                            val uri = ii!!.data
                            try {
                                bitmap = MediaStore.Images.Media.getBitmap(
                                    requireActivity().contentResolver, uri
                                )
                                binding.imageBackgroundUserSignUp.setImageBitmap(bitmap)
                            } catch (e: IOException) {
                                throw RuntimeException(e)
                            }
                        }
                    }
                })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //.. code
        rootView =
            requireActivity().findViewById(R.id.rootLayout)   // تغيير R.id.rootLayout إلى ID اللايوت الرئيسي لديك
        PushDownAnim.setPushDownAnimTo(
            binding.btnUpdateProfile,
            binding.numFollowing,
            binding.numFollowers,
            binding.cardView,
            binding.imageEditImageBackground,
            binding.imageLogout
        ).setScale(PushDownAnim.MODE_SCALE, 0.90f)
        setupViewPager()
        coroutineScope.launch {
            getNumberFollow()
        }
        init()
        onClickItem()
    }


    private fun setupViewPager() {
        // إعداد ViewPager2 مع أداة تحكم TabLayout
        binding.viewPager.adapter = ViewPagerProfileAdapter(requireActivity())
        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.your_posts)
                1 -> tab.text = getString(R.string.save_posts)
            }
        }.attach()
    }

    fun init() {
        val shared =
            requireActivity().getSharedPreferences("user_data", AppCompatActivity.MODE_PRIVATE)
        var id = shared.getInt("id", 0).toString().toInt()
        email = shared.getString("email", "").toString()
        password = shared.getString("password", "").toString()
        whatsappLink = shared.getString("whatsappLink", "").toString()
        facebookLink = shared.getString("facebookLink", "").toString()
        name = shared.getString("name", "").toString()
        information = shared.getString("information", "").toString()
        backgroundImage = shared.getString("backgroundImage", "").toString()
        userImage = shared.getString("userImage", "").toString()

        binding.nameUser.text = name.toString()
        binding.infoUser.text = information.toString()
        Glide.with(requireActivity()).load("http://10.0.2.2/db/shyf/${userImage}")
            .apply(RequestOptions().override(600, 600)).error(R.drawable.profile_sh)
            .into(binding.imageUserSignUp)
        Glide.with(requireActivity()).load("http://10.0.2.2/db/shyf/${backgroundImage}")
            .apply(RequestOptions().override(600, 600)).error(R.drawable.backgroud_img)
            .into(binding.imageBackgroundUserSignUp)
    }

    @SuppressLint("MissingInflatedId")
    fun onClickItem() {
        binding.imageUserSignUp.setOnClickListener {
            val bottomSheetDialog =
                BottomSheetDialog(requireActivity(), R.style.BottomSheetDialogTheme)
            val bottomSheetView = LayoutInflater.from(requireActivity()).inflate(
                R.layout.buttom_sheet_layout,
                requireActivity().findViewById<LinearLayout>(R.id.layout_dialog)
            )
            bottomSheetView.findViewById<Button>(R.id.btn_store).setOnClickListener {
//                val i = Intent(Intent.ACTION_PICK)
//                i.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//                launcher.launch(i)
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
            bottomSheetView.findViewById<Button>(R.id.btn_Update_image).setOnClickListener {
                coroutineScope.launch {
                    updateImageUser()
                }
                if (selectedImageUri != null) {
                    uploadAndSaveImage(user_id, selectedImageUri!!)
                }
                bottomSheetDialog.dismiss()
            }
            bottomSheetDialog.setContentView(bottomSheetView)
            bottomSheetDialog.show()
        }
        binding.btnUpdateProfile.setOnClickListener {
            val bottomSheetDialog =
                BottomSheetDialog(requireActivity(), R.style.BottomSheetDialogTheme)
            bottomSheetView = LayoutInflater.from(requireActivity()).inflate(
                R.layout.buttom_sheet_layout_info,
                requireActivity().findViewById<LinearLayout>(R.id.layout_dialog)
            )
            bottomSheetView.findViewById<Button>(R.id.btn_Update).setOnClickListener {
                coroutineScope.launch {
                    userUpdate()
                }
                bottomSheetDialog.dismiss()
            }
            bottomSheetView.findViewById<TextInputLayout>(R.id.emailUpdate).editText!!.setText(email)
            bottomSheetView.findViewById<TextInputLayout>(R.id.Whatsapp_Link_Update).editText!!.setText(
                whatsappLink
            )
            bottomSheetView.findViewById<TextInputLayout>(R.id.Facebook_Link_Update).editText!!.setText(
                facebookLink
            )
            bottomSheetView.findViewById<TextInputLayout>(R.id.name_Update).editText!!.setText(
                name
            )
            bottomSheetView.findViewById<TextInputLayout>(R.id.description_Update).editText!!.setText(
                information
            )
            bottomSheetDialog.setContentView(bottomSheetView)
            bottomSheetDialog.show()
        }
        binding.imageEditImageBackground.setOnClickListener {
            val bottomSheetDialog =
                BottomSheetDialog(requireActivity(), R.style.BottomSheetDialogTheme)
            val bottomSheetView = LayoutInflater.from(requireActivity()).inflate(
                R.layout.buttom_sheet_layout,
                requireActivity().findViewById<LinearLayout>(R.id.layout_dialog)
            )
            bottomSheetView.findViewById<Button>(R.id.btn_store).setOnClickListener {
//                val i = Intent(Intent.ACTION_PICK)
//                i.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//                launcher2.launch(i)
                pickMedia2.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
            bottomSheetView.findViewById<Button>(R.id.btn_Update_image).setOnClickListener {
                coroutineScope.launch {
                    updateImageUserBackground()
                }
                bottomSheetDialog.dismiss()
            }
            bottomSheetDialog.setContentView(bottomSheetView)
            bottomSheetDialog.show()
        }
        binding.imageLogout.setOnClickListener {
            val shared =
                requireActivity().getSharedPreferences("user_data", AppCompatActivity.MODE_PRIVATE)
            val editor = shared.edit()
            editor.clear()
            editor.apply()
            startActivity(Intent(requireContext(), SignInActivity::class.java))
            requireActivity().finish()
            requireActivity().overridePendingTransition(
                R.anim.slide_in_right, R.anim.slide_out_left
            )
        }

        binding.numFollowers.setOnClickListener {
            val i = Intent(requireActivity(), FollowersActivity::class.java)
            val shared =
                requireActivity().getSharedPreferences("user_data", AppCompatActivity.MODE_PRIVATE)
            var id = shared.getInt("id", 0).toString().toInt()
            i.putExtra("idUser2", id)

            startActivity(i)
            requireActivity().overridePendingTransition(
                R.anim.slide_in_right, R.anim.slide_out_left
            )
        }
        binding.numFollowing.setOnClickListener {
            val i = Intent(requireActivity(), FollowingActivity::class.java)
            val shared =
                requireActivity().getSharedPreferences("user_data", AppCompatActivity.MODE_PRIVATE)
            var id = shared.getInt("id", 0).toString().toInt()
            i.putExtra("idUser2", id)

            startActivity(i)
            requireActivity().overridePendingTransition(
                R.anim.slide_in_right, R.anim.slide_out_left
            )
        }
        // إضافة مستمع للنقر الطويل على صورة المنشور
        binding.imageUserSignUp.setOnLongClickListener {
            showZoomableImage(binding.imageUserSignUp.drawable)
            true // تم التعامل مع الحدث بنجاح
        }
        binding.imageBackgroundUserSignUp.setOnLongClickListener {
            showZoomableImage(binding.imageBackgroundUserSignUp.drawable)
            true // تم التعامل مع الحدث بنجاح
        }
    }


    private fun showZoomableImage(imageResource: Drawable) {
        val dialog = Dialog(requireActivity())
        dialog.setContentView(R.layout.dialog_zoomable_image)

        val zoomableImageView: PhotoView = dialog.findViewById(R.id.zoomableImageView)
        zoomableImageView.setImageDrawable(imageResource)

        dialog.show()
    }


    private suspend fun getNumberFollow() {
        val shared =
            requireActivity().getSharedPreferences("user_data", AppCompatActivity.MODE_PRIVATE)
        var id = shared.getInt("id", 0).toString().toInt()
        val call: Call<Result> =
            RetrofitGetNumberFollow.getInstance().getMyApi().getnumberFollow(id)
        call.enqueue(object : Callback<Result> {
            override fun onResponse(call: Call<Result>, response: Response<Result>) {
                if (!response.body()!!.error!!) {
                    binding.numFollowers.text = response.body()!!.user!!.numFollowers.toString()
                    binding.numFollowing.text = response.body()!!.user!!.numFollowing.toString()
                }
            }

            override fun onFailure(call: Call<Result>, t: Throwable) {
                Log.d("Retrofit ERROR -->", t.message!!)
            }
        })
    }

    private suspend fun userUpdate() {
        //retrieve data from Edit texts
        val shared =
            requireActivity().getSharedPreferences("user_data", AppCompatActivity.MODE_PRIVATE)
        var id = shared.getInt("id", 0).toString().toInt()
        var email =
            bottomSheetView.findViewById<TextInputLayout>(R.id.emailUpdate).editText!!.text.toString()
                .trim()

        var whatsappLink =
            bottomSheetView.findViewById<TextInputLayout>(R.id.Whatsapp_Link_Update).editText!!.text.toString()
                .trim()

        var facebookLink =
            bottomSheetView.findViewById<TextInputLayout>(R.id.Facebook_Link_Update).editText!!.text.toString()
                .trim()

        var name =
            bottomSheetView.findViewById<TextInputLayout>(R.id.name_Update).editText!!.text.toString()
                .trim()

        var description =
            bottomSheetView.findViewById<TextInputLayout>(R.id.description_Update).editText!!.text.toString()
                .trim()

        //Here we will handle the http request to insert user to mysql db
        val call: Call<Result> = RetrofitUpdateUser.getInstance().getMyApi()
            .updateUser(id, whatsappLink, facebookLink, name, description)
        call.enqueue(object : Callback<Result> {
            override fun onResponse(call: Call<Result>, response: Response<Result>) {
                if (response.body()!!.error === true) {
                    bottomSheetView.findViewById<TextView>(R.id.error_message).visibility =
                        View.VISIBLE
                    bottomSheetView.findViewById<TextView>(R.id.error_message)
                        .setText(response.message().toString())
                } else if (response.body()!!.error === false) {
                    Log.d("Response ---> ", "User Updated successfully")
                    val shared = requireActivity().getSharedPreferences(
                        "user_data", AppCompatActivity.MODE_PRIVATE
                    )
                    val editor = shared.edit()
                    editor.putString(
                        "whatsappLink", response.body()!!.user!!.whatsappLink.toString()
                    )
                    editor.putString(
                        "facebookLink", response.body()!!.user!!.facebookLink.toString()
                    )
                    editor.putString("name", response.body()!!.user!!.name.toString())
                    editor.putString("information", response.body()!!.user!!.information.toString())
                        .apply()
                    updateUserInFirebase(whatsappLink, facebookLink, name, description)
                }
            }

            override fun onFailure(call: Call<Result>, t: Throwable) {
                Log.e("Failed to Update Data ---> ", t.message!!)
                showCenteredMessage("لم يتم التعديل")
            }
        })
        //End update method
    }

    private fun updateUserInFirebase(
        whatsappLink: String, facebookLink: String, name: String, description: String
    ) {
        val db = FirebaseFirestore.getInstance()
        // تم إنشاء الحساب بنجاح، الآن سنحصل على معرف المستخدم
        val currentUser = auth.currentUser
        val user_id = currentUser?.uid
        val documentReference = db.collection(Constants.KEY_COLLECTION_USER).document(user_id!!)
        // استبدل "your_collection_name" بالمجموعة التي تحتوي على المستند و "document_id" بمعرف المستند الذي تريد تحديثه
        // البيانات التي تريد تحديثها
        val hashMap = HashMap<String, Any>()
        hashMap["whatsappLink"] = whatsappLink
        hashMap["facebookLink"] = facebookLink
        hashMap["name"] = name
        hashMap["description"] = description
        documentReference.update(hashMap).addOnSuccessListener {
            showCenteredMessage("تم التعديل")
        }.addOnFailureListener { e ->
            showCenteredMessage("لم يتم التعديل")
        }

    }

    private suspend fun updateImageUser() {
        //retrieve data from Edit texts
        val byteArrayOutputStream = ByteArrayOutputStream()
        val base: String? = null
        if (bitmap == null) {
            bitmap = binding.imageUserSignUp.getDrawable().toBitmap()
            binding.imageUserSignUp.setImageBitmap(bitmap)
        }
        bitmap!!.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
        val bytes = byteArrayOutputStream.toByteArray()
        val base46Image = Base64.encodeToString(bytes, Base64.DEFAULT)
        val shared =
            requireActivity().getSharedPreferences("user_data", AppCompatActivity.MODE_PRIVATE)
        var id = shared.getInt("id", 0).toString().toInt()
        var image = shared.getString("userImage", "").toString().trim()
        //retrieve data from Edit texts
        //Here we will handle the http request to insert user to mysql db
        val call: Call<Result> =
            RetrofitUpdateUserImage.getInstance().getMyApi().UpdateUserImage(id, base46Image)
        call.enqueue(object : Callback<Result> {
            override fun onResponse(call: Call<Result>, response: Response<Result>) {
                Log.d("Response ---> ", "User updated successfully")
                if (response.body()!!.error != true) {
                    val shared = requireActivity().getSharedPreferences(
                        "user_data", AppCompatActivity.MODE_PRIVATE
                    )
                    val editor = shared.edit()
                    editor.putString(
                        "userImage", response.body()!!.user!!.userImage.toString()
                    ).apply()
                    showCenteredMessage("تم التعديل")
                }
            }

            override fun onFailure(call: Call<Result>, t: Throwable) {
                Log.e("Failed to Register User ---> ", t.message!!)
                showCenteredMessage("لم يتم التعديل")
            }
        })
    }

    private suspend fun updateImageUserBackground() {
        //retrieve data from Edit texts
        val byteArrayOutputStream = ByteArrayOutputStream()
        val base: String? = null
        if (bitmap2 == null) {
            bitmap2 = binding.imageBackgroundUserSignUp.getDrawable().toBitmap()
            binding.imageBackgroundUserSignUp.setImageBitmap(bitmap2)
        }
        bitmap2!!.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
        val bytes = byteArrayOutputStream.toByteArray()
        val base46Image = Base64.encodeToString(bytes, Base64.DEFAULT)
        val shared =
            requireActivity().getSharedPreferences("user_data", AppCompatActivity.MODE_PRIVATE)
        var id = shared.getInt("id", 0).toString().toInt()
        var image = shared.getString("backgroundImage", "").toString().trim()
        //retrieve data from Edit texts
        //Here we will handle the http request to insert user to mysql db
        val call: Call<Result> = RetrofitUpdateUserImageBackground.getInstance().getMyApi()
            .UpdateUserImageBackground(id, base46Image)
        call.enqueue(object : Callback<Result> {
            override fun onResponse(call: Call<Result>, response: Response<Result>) {
                Log.d("Response ---> ", "User Register successfully")
                if (response.body()!!.error != true) {
                    val shared = requireActivity().getSharedPreferences(
                        "user_data", AppCompatActivity.MODE_PRIVATE
                    )
                    val editor = shared.edit()
                    editor.putString(
                        "backgroundImage", response.body()!!.user!!.backgroundImage.toString()
                    ).apply()
                    showCenteredMessage("تم التعديل")
                }
            }

            override fun onFailure(call: Call<Result>, t: Throwable) {
                Log.e("Failed to Register User ---> ", t.message!!)
                showCenteredMessage("لم يتم التعديل")
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        // قم بإلغاء الـ CoroutineScope عندما تتم الإنتهاء من الـ Activity
        coroutineScope.cancel()
    }


    private fun uploadAndSaveImage(userId: String, imageUri: Uri) {
            val storageReference =
                FirebaseStorage.getInstance().reference.child("profile_images").child(userId)
            val imageUploadTask = storageReference.putFile(imageUri)

            imageUploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                storageReference.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result

                    // تحديث رابط الصورة في Firestore بدلاً من Realtime Database
                    val db = FirebaseFirestore.getInstance()
                    val userReference = db.collection("users").document(userId)

                    val updatedData = HashMap<String, Any>()
                    updatedData["userImage"] = downloadUri.toString()

                    userReference.update(updatedData)
                        .addOnSuccessListener {
                            // تم تحديث الصورة بنجاح في Firestore
                            showCenteredMessage("تم تحديث الصورة بنجاح")
                        }
                        .addOnFailureListener { e ->
                            // حدث خطأ في تحديث الصورة في Firestore
                            showCenteredMessage("حدث خطأ في تحديث الصورة")
                        }
                } else {
                    // حدث خطأ أثناء تحميل الصورة
                    showCenteredMessage("حدث خطأ أثناء تحميل الصورة")
                }
            }
        }


}