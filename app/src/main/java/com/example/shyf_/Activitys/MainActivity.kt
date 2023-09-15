package com.example.shyf_.Activitys

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.shyf_.R
import com.example.shyf_.Receiver.MyReceiver
import com.example.shyf_.databinding.ActivityMainBinding
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var myReceiver: MyReceiver
    private lateinit var connectivityManager: ConnectivityManager
    val userid = FirebaseAuth.getInstance().uid
    var auth: FirebaseAuth? = null

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //..... code
        auth = FirebaseAuth.getInstance()
//        getToken()

        val rootView = findViewById<View>(android.R.id.content)  // الحصول على العنصر الجذري للتخطيط
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.tab_home,
                R.id.tab_notification,
                R.id.tab_profile,
                R.id.tab_add,
                R.id.tab_search
            )
        )
        navView.setupWithNavController(navController)


        var badeDrawable: BadgeDrawable
        badeDrawable = navView.getOrCreateBadge(R.id.tab_notification)
        badeDrawable.setVisible(true)
        badeDrawable.number = 5
        badeDrawable.badgeTextColor = getColor(R.color.white)

        // Internet
        if (!isInternetAvailable()) {
            showInternetDialog()
        }
        isConectionInternet()
    }

    private fun isConectionInternet() {
        myReceiver = MyReceiver()
        connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                val intent = Intent("INTERNET_AVAILABLE")
                LocalBroadcastManager.getInstance(this@MainActivity).sendBroadcast(intent)
                //  يوجد اتصال بالإنترنت
                showNoInternetMessage(true)
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                // لا يوجد اتصال بالإنترنت
                showNoInternetMessage(false)
            }
        }

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }


    override fun onResume() {
        super.onResume()
        val filter = IntentFilter("INTERNET_AVAILABLE")
        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver, filter)
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver)
    }


    fun getToken() {
        FirebaseMessaging.getInstance().token.addOnSuccessListener { s -> updateToken(s) }
    }

    @SuppressLint("CommitPrefEdits")
    fun updateToken(token: String?) {
        val shared = getSharedPreferences("user_data", MODE_PRIVATE)
        val editor = shared.edit()
        editor.putString(
            "userToken", token
        )
        val reference1 = FirebaseDatabase.getInstance().getReference("users").child(
            auth!!.uid!!
        )
        val hashMap = HashMap<String, Any?>()
        hashMap["userToken"] = token
        reference1.updateChildren(hashMap).addOnCompleteListener { task ->
            if (task.isSuccessful) {
//                Toast.makeText(
//                    this@MainActivity, "update token successfully", Toast.LENGTH_SHORT
//                ).show()
                Log.d("yazan","update token successfully")

                val shared = getSharedPreferences("user_data", MODE_PRIVATE)
                val editor = shared.edit()
                editor.putString(
                    "userToken",
                    token
                )
                editor.apply()
            }
        }
    }

    private fun showInternetDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val btn: AppCompatButton = dialog.findViewById(R.id.try_again)
        btn.setOnClickListener {
            dialog.dismiss()
            finish()
        }
        dialog.show()
    }

    private fun isInternetAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val networkCapabilities =
                connectivityManager.getNetworkCapabilities(network) ?: return false

            return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }
    }

    private fun showNoInternetMessage(isConaected: Boolean) {
        if (isConaected) {
            val rootView = findViewById<View>(android.R.id.content)
            val snackbar = Snackbar.make(rootView, "تم الاتصال بالانترنت", Snackbar.LENGTH_LONG)
            snackbar.show()
        } else if (!isConaected) {
            val rootView = findViewById<View>(android.R.id.content)
            val snackbar =
                Snackbar.make(rootView, "الإنترنت غير متاح حاليًا", Snackbar.LENGTH_INDEFINITE)
            snackbar.setAction("إعادة المحاولة") {
                isConectionInternet()
            }
            snackbar.show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}