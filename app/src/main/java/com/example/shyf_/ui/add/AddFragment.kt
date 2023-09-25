package com.example.shyf_.ui.add

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Base64
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.shyf_.R
import com.example.shyf_.apis.RetrofitInsertPsot
import com.example.shyf_.databinding.FragmentAddBinding
import com.example.shyf_.model.Result_AddPost
import com.google.android.material.snackbar.Snackbar
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class AddFragment : Fragment(), LocationListener {

    private var _binding: FragmentAddBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var launcher: ActivityResultLauncher<Intent>
    var bitmap: Bitmap? = null
    lateinit var Country_Post: String
    lateinit var descriptionAddPost: String
    lateinit var City_Post: String
    lateinit var street_Post: String
    lateinit var bottomSheetView: View
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    lateinit var rootView: ConstraintLayout // تعريف المتغير هنا

    lateinit var locationManager: LocationManager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val profileViewModel = ViewModelProvider(this).get(AddViewModel::class.java)
        _binding = FragmentAddBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //...................
        launcher =
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
                                binding.imgFoodPost.setImageBitmap(bitmap)
                            } catch (e: IOException) {
                                throw RuntimeException(e)
                            }
                        }
                    }
                })
        getPermission()
        locationEnabled()
        getLocation()


        return root
    }

    var pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            // Image Selection
            bitmap = MediaStore.Images.Media.getBitmap(
                requireActivity().contentResolver, uri
            )
            binding.imgFoodPost.setImageBitmap(bitmap)
        } else {
            // No image
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //.... code add post
        rootView =
            requireActivity().findViewById(R.id.rootLayout)   // تغيير R.id.rootLayout إلى ID اللايوت الرئيسي لديك
        PushDownAnim.setPushDownAnimTo(
            binding.btnAddPost, binding.btnCancel, binding.btnAddPhoto
        ).setScale(PushDownAnim.MODE_SCALE, 0.85f)
        init()
        setUpAppName()
        onClickItem()

    }

    private suspend fun updateGetLocation() {
        lifecycleScope.launch {
            try {
                val handler = Handler()
                val updateTextRunnable = object : Runnable {
                    var count = 1
                    override fun run() {
                        val dots = StringBuilder()
                        repeat(count) {
                            dots.append(".")
                        }
                        binding.getLocationTxt.text = "Get location$dots"
                        count = (count + 1) % 4
                        handler.postDelayed(this, 300)
                    }
                }
                handler.post(updateTextRunnable)
            } catch (e: Exception) {

            }
        }
    }

    private fun getPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), 101
            )
        }
    }

    fun init() {
        val shared =
            requireActivity().getSharedPreferences("user_data", AppCompatActivity.MODE_PRIVATE)
        descriptionAddPost = shared.getString("descriptionAddPost", "").toString()
        Country_Post = shared.getString("CountryPost", "").toString()
        City_Post = shared.getString("CityPost", "").toString()
        street_Post = shared.getString("streetPost", "").toString()
        binding.CountryPost.editText!!.setText(Country_Post)
        binding.CityPost.editText!!.setText(City_Post)
        binding.streetPost.editText!!.setText(street_Post)
        binding.descriptionAddPost.editText!!.setText(descriptionAddPost)
        locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }


    @SuppressLint("MissingInflatedId")
    fun onClickItem() {
        binding.imgFoodPost.setOnClickListener {
//            val i = Intent(Intent.ACTION_PICK)
//            i.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//            launcher.launch(i)
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        binding.btnAddPhoto.setOnClickListener {
            binding.cardView2.visibility = View.VISIBLE
            binding.btnAddPhoto.visibility = View.GONE
        }
        binding.btnCancel.setOnClickListener {
            binding.cardView2.visibility = View.GONE
            binding.btnAddPhoto.visibility = View.VISIBLE
        }
        binding.btnAddPost.setOnClickListener {
            if (binding.descriptionAddPost.editText!!.text.toString()
                    .isNotEmpty() && binding.CountryPost.editText!!.text.toString()
                    .isNotEmpty() && binding.CityPost.editText!!.text.toString()
                    .isNotEmpty() && binding.streetPost.editText!!.text.toString()
                    .isNotEmpty() && binding.pricePost.editText!!.text.toString().isNotEmpty()
            ) {
                // فحص اتصال الإنترنت عند بدء النشاط
                if (!isInternetAvailable()) {
                    showInternetDialog()
                } else {
                    coroutineScope.launch {
                        insertPost()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Type the post information", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun showInternetDialog() {
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val btn: AppCompatButton = dialog.findViewById(R.id.try_again)
        btn.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun isInternetAvailable(): Boolean {
        val connectivityManager =
            requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val networkCapabilities =
                connectivityManager.getNetworkCapabilities(network) ?: return false

            return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) && networkCapabilities.hasCapability(
                NetworkCapabilities.NET_CAPABILITY_VALIDATED
            )
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }
    }

    private fun getTimeAdd(): String? {
        // الحصول على التاريخ والوقت الحاليين من الجهاز
        val currentDateTime = Calendar.getInstance()
        // تنسيق التاريخ والوقت
        // yyyy-MM-dd HH:mm:ss
        // MM/dd/yyyy hh:mm:ss a
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val formattedDateTime = dateFormat.format(currentDateTime.time)
        // إخراج التاريخ والوقت المجتمعين بالتنسيق المطلوب
        return formattedDateTime.toString()
    }
    private fun getCurrentDateTime(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val currentDate = Date()
        return dateFormat.format(currentDate)
    }
    private suspend fun insertPost() {
        if (binding.cardView2.visibility.equals(View.VISIBLE)) {
            val byteArrayOutputStream = ByteArrayOutputStream()
            val base: String? = null
            if (bitmap == null) {
                showCenteredMessage("Select Photo")
            } else {
                bitmap!!.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
                val bytes = byteArrayOutputStream.toByteArray()
                val base46Image = Base64.encodeToString(bytes, Base64.DEFAULT)
                val shared = requireActivity().getSharedPreferences(
                    "user_data", AppCompatActivity.MODE_PRIVATE
                )
                var id = shared.getInt("id", 0).toString()
                //retrieve data from Edit texts
                //Here we will handle the http request to insert user to mysql db
                val call: Call<Result_AddPost> =
                    RetrofitInsertPsot.getInstance().getMyApi().InsertPosts(
                        binding.CountryPost.editText!!.text.toString().trim(),
                        binding.CityPost.editText!!.text.toString().trim(),
                        binding.streetPost.editText!!.text.toString().trim(),
                        binding.descriptionAddPost.editText!!.text.toString().trim(),
                        binding.pricePost.editText!!.text.toString().toDouble(),
                        getCurrentDateTime(),
                        base46Image,
                        id
                    )
                call.enqueue(object : Callback<Result_AddPost> {
                    override fun onResponse(
                        call: Call<Result_AddPost>, response: Response<Result_AddPost>
                    ) {
                        Log.d("Response ---> ", "Add Post successfully")
                        if (response.body()!!.error != true) {
                            val shared = requireActivity().getSharedPreferences(
                                "user_data", AppCompatActivity.MODE_PRIVATE
                            )
                            val editor = shared.edit()
                            editor.putString(
                                "CountryPost", binding.CountryPost.editText!!.text.toString().trim()
                            )
                            editor.putString(
                                "CityPost", binding.CityPost.editText!!.text.toString().trim()
                            )
                            editor.putString(
                                "streetPost", binding.streetPost.editText!!.text.toString().trim()
                            ).apply()
                            Navigation.findNavController(requireView())
                                .navigate(R.id.action_tab_add_to_tab_home)
                            // بعد ذلك، يمكنك استدعاء الميثود لعرض رسالة في وسط الشاشة
                            showCenteredMessage("تم النشر!")
                        }
                    }

                    override fun onFailure(call: Call<Result_AddPost>, t: Throwable) {
                        Log.e("Failed to Register Post ---> ", t.message!!)
                    }
                })
            }
        } else if (binding.cardView2.visibility.equals(View.GONE)) {
            val shared =
                requireActivity().getSharedPreferences("user_data", AppCompatActivity.MODE_PRIVATE)
            var id = shared.getInt("id", 0).toString()
            //retrieve data from Edit texts
            //Here we will handle the http request to insert user to mysql db
            val call: Call<Result_AddPost> =
                RetrofitInsertPsot.getInstance().getMyApi().InsertPosts(
                    binding.CountryPost.editText!!.text.toString().trim(),
                    binding.CityPost.editText!!.text.toString().trim(),
                    binding.streetPost.editText!!.text.toString().trim(),
                    binding.descriptionAddPost.editText!!.text.toString().trim(),
                    binding.pricePost.editText!!.text.toString().toDouble(),
                    getCurrentDateTime(),
                    "null",
                    id
                )
            call.enqueue(object : Callback<Result_AddPost> {
                override fun onResponse(
                    call: Call<Result_AddPost>, response: Response<Result_AddPost>
                ) {
                    Log.d("Response ---> ", "Add Post successfully")
                    if (response.body()!!.error != true) {
                        val shared = requireActivity().getSharedPreferences(
                            "user_data", AppCompatActivity.MODE_PRIVATE
                        )
                        val editor = shared.edit()
                        editor.putString(
                            "descriptionAddPost",
                            binding.descriptionAddPost.editText!!.text.toString().trim()
                        )
                        editor.putString(
                            "CountryPost", binding.CountryPost.editText!!.text.toString().trim()
                        )
                        editor.putString(
                            "CityPost", binding.CityPost.editText!!.text.toString().trim()
                        )
                        editor.putString(
                            "streetPost", binding.streetPost.editText!!.text.toString().trim()
                        ).apply()
                        Navigation.findNavController(requireView())
                            .navigate(R.id.action_tab_add_to_tab_home)
                        // بعد ذلك، يمكنك استدعاء الميثود لعرض رسالة في وسط الشاشة
                        showCenteredMessage("تم النشر!")
                    }
                }

                override fun onFailure(call: Call<Result_AddPost>, t: Throwable) {
                    Log.e("Failed to Register Post ---> ", t.message!!)
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

    private fun setUpAppName() {
        val spannableString = SpannableString(getString(R.string.add_post_title))
        val colorSpan = ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.prim1))
        spannableString.setSpan(colorSpan, 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.title.text = spannableString
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

    @SuppressLint("SetTextI18n")
    override fun onLocationChanged(location: Location) {
        try {
            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            val addresses =
                geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1)
            binding.CityPost.editText!!.setText(addresses!![0].adminArea)
            binding.CountryPost.editText!!.setText(addresses!![0].countryName)
//            tvPin.setText(addresses!![0].postalCode)
//            binding.streetPost.editText!!.setText(addresses!![0].getAddressLine(0))
            binding.streetPost.editText!!.setText(addresses!![0].locality)
            binding.progressSignup.visibility = View.GONE
            binding.getLocationTxt.visibility = View.GONE
            val shared = requireActivity().getSharedPreferences(
                "user_data", AppCompatActivity.MODE_PRIVATE
            )
            val editor = shared.edit()
            editor.putString(
                "CountryPost", binding.CountryPost.editText!!.text.toString().trim()
            )
            editor.putString(
                "CityPost", binding.CityPost.editText!!.text.toString().trim()
            )
            editor.putString(
                "streetPost", binding.streetPost.editText!!.text.toString().trim()
            ).apply()
        } catch (e: java.lang.Exception) {
        }
    }

    private fun getLocation() {
        binding.progressSignup.visibility = View.VISIBLE
        binding.getLocationTxt.visibility = View.VISIBLE
//                coroutineScope.launch {
//                    updateGetLocation()
//                }
        try {
            locationManager =
                requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 500, 5.toFloat(), this as LocationListener
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
            binding.progressSignup.visibility = View.GONE
            binding.getLocationTxt.visibility = View.GONE
        }

    }

    private fun locationEnabled() {
        val lm = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gpsEnabled = false
        var networkEnabled = false
        try {
            gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (!gpsEnabled && !networkEnabled) {
            AlertDialog.Builder(requireContext()).setTitle("Enable GPS Service")
                .setMessage("We need your GPS location to show Near Places around you.")
                .setCancelable(false).setPositiveButton("Enable") { _, _ ->
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }.setNegativeButton("Cancel", null).show()
        }
    }

}