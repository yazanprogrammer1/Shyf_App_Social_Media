package com.example.shyf_.ChatApp

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mvvm_architecture1.viewModel.ApiMethodesViewModel
import com.example.shyf_.Adapter.NotesUserAdapter
import com.example.shyf_.Adapter.userAdapter
import com.example.shyf_.R
import com.example.shyf_.apis.RetrofitInsertNotes
import com.example.shyf_.apis.RetrofitInsertStory
import com.example.shyf_.databinding.ActivityChatMainBinding
import com.example.shyf_.model.Chat
import com.example.shyf_.model.Result_AddNotes
import com.example.shyf_.model.Result_AddStory
import com.example.shyf_.model.UserChats
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputLayout
import com.google.common.graph.ElementOrder.sorted
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Locale

class ChatMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatMainBinding
    lateinit var userAdapter: userAdapter
    lateinit var mUserList: ArrayList<UserChats>
    lateinit var userReceiverList: ArrayList<String>
    var firebaseUser: FirebaseUser? = null
    var reference: DatabaseReference? = null
    var auth: FirebaseAuth? = null
    val userid = FirebaseAuth.getInstance().uid
    var id: Int = 0
    lateinit var name: String
    lateinit var userImage: String
    private val apiMethodesViewModel: ApiMethodesViewModel by viewModels()
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    lateinit var bottomSheetView: View
    lateinit var manager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // code chat main app
        mUserList = ArrayList()
        auth = FirebaseAuth.getInstance()
        getToken()
        userReceiverList = ArrayList()
        binding.searchList.setHasFixedSize(true)
        binding.searchList.setLayoutManager(
            LinearLayoutManager(
                applicationContext
            )
        )
        firebaseUser = FirebaseAuth.getInstance().currentUser
        reference = FirebaseDatabase.getInstance().getReference("chats")
        reference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userReceiverList.clear()
                for (dataSnapshot in snapshot.children) {
                    val chat: Chat? = dataSnapshot.getValue(Chat::class.java)
                    if (chat!!.sender.equals(firebaseUser!!.uid)) {
                        if (!userReceiverList.contains(chat.reciever)) {
                            userReceiverList.add(chat.reciever!!)
                        }
                    } else if (chat.reciever.equals(firebaseUser!!.uid)) {
                        if (!userReceiverList.contains(chat.sender)) {
                            userReceiverList.add(chat.sender!!)
                        }
                    }
                }
                readChats()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        setUpAppName()
        clickItem()
        initData()
//        coroutineScope.launch {
//            getNoteForUser()
//        }
//        coroutineScope.launch {
//            getNotesUserIfFollow()
//        }
    }

    private suspend fun insertNote(note: String) {
        lifecycleScope.launch {
            try {
                val call: Call<Result_AddNotes> =
                    RetrofitInsertNotes.getInstance().myApi.InsertNote(id, note)
                call.enqueue(object : Callback<Result_AddNotes> {
                    override fun onResponse(
                        call: Call<Result_AddNotes>,
                        response: Response<Result_AddNotes>
                    ) {
                        if (response.body()!!.error == false) {
                            Toast.makeText(applicationContext, "add done", Toast.LENGTH_SHORT)
                                .show()
                            Log.e("yazan", "Get Note Sucssfully:)")
                        } else {
                            Toast.makeText(applicationContext, "not add done", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }

                    override fun onFailure(call: Call<Result_AddNotes>, t: Throwable) {
                        Log.e("yazan", t.message!!)
                    }
                })
            } catch (e: Exception) {

            }
        }
    }

    private fun initData() {
        manager = LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)
        val shared = getSharedPreferences("user_data", AppCompatActivity.MODE_PRIVATE)
        id = shared.getInt("id", 0).toString().toInt()
        name = shared.getString("name", "").toString()
        userImage = shared.getString("userImage", "").toString()

        binding.nameUser.text = name.toString()
        Glide.with(applicationContext).load("http://10.0.2.2/db/shyf/${userImage}")
            .apply(RequestOptions().override(600, 600)).error(R.drawable.profile_sh)
            .into(binding.imgProfile)
    }

//    private suspend fun getStoryForUser() {
//        lifecycleScope.launch {
//            try {
//                apiMethodesViewModel.getStoryForUser(idUser)
//                    .observe(requireActivity(), Observer { story ->
//                        if (story[0].Subtitle.equals("No Data")) {
//
//                        } else {
//                            val shared =
//                                requireActivity().getSharedPreferences(
//                                    "user_data",
//                                    AppCompatActivity.MODE_PRIVATE
//                                )
//                            currentStory.url =
//                                "https://firebasestorage.googleapis.com/v0/b/shyf-f9631.appspot.com/o/USER_IMAGE1694283333997.jpg?alt=media&token=4d5356ec-c28c-4ea2-9ff9-2df3185349c4"
//                            currentStory.date =
//                                SimpleDateFormat("MM/dd/yyyy hh:mm:ss a").parse(story[0].TimeAdd.toString())
//                            currentStory.description = story[0].description.toString()
//                            subtitle = story[0].Subtitle.toString()
//
//                            Log.e("yazan", "Created :)")
//                        }
//                    })
//            } catch (e: Exception) {
//                // التعامل مع الأخطاء إذا كانت هناك
//                Log.e("yazan", e.message!!)
//            }
//        }
//    }
//
//
//    private suspend fun insertStory(subtitle: String, description: String) {
//        lifecycleScope.launch {
//            val byteArrayOutputStream = ByteArrayOutputStream()
//            val base: String? = null
//            if (bitmap == null) {
////                bitmap = binding.imgFoodPost.getDrawable().toBitmap()
////                binding.imgFoodPost.setImageBitmap(bitmap)
//                showCenteredMessage("Select Photo")
//            } else {
//                bitmap!!.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
//                val bytes = byteArrayOutputStream.toByteArray()
//                val base46Image = Base64.encodeToString(bytes, Base64.DEFAULT)
//                try {
//                    val call: Call<Result_AddStory> =
//                        RetrofitInsertStory.getInstance().myApi.insertStory(
//                            idUser,
//                            subtitle,
//                            description,
//                            base46Image,
//                            getTimeAdd()
//                        )
//                    call.enqueue(object : Callback<Result_AddStory> {
//                        override fun onResponse(
//                            call: Call<Result_AddStory>,
//                            response: Response<Result_AddStory>
//                        ) {
//                            if (response.body()!!.error == false) {
//                                showCenteredMessage("Done")
//                                Log.e("yazan", "Add Story Sucssfully:)")
//                            } else {
//                                Toast.makeText(
//                                    requireContext(),
//                                    "Not Add Story",
//                                    Toast.LENGTH_SHORT
//                                )
//                                    .show()
//                                showCenteredMessage("Not Add Story")
//                            }
//                        }
//
//                        override fun onFailure(call: Call<Result_AddStory>, t: Throwable) {
//                            Log.e("yazan", t.message!!)
//                        }
//                    })
//                } catch (e: Exception) {
//
//                }
//            }
//        }
//    }

    private fun readChats() {
        mUserList = ArrayList()
        reference = FirebaseDatabase.getInstance().getReference("users")
        reference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mUserList.clear()
                for (dataSnapshot in snapshot.children) {
                    val user: UserChats? = dataSnapshot.getValue(UserChats::class.java)
                    for (i in userReceiverList.indices) {
                        if (user!!.id.equals(userReceiverList[i])) {
                            if (mUserList.size != 0) {
                                for (j in mUserList.indices) {
                                    if (!user.id.equals(mUserList[j].id)) {
                                        mUserList.add(user)
                                    }
                                }
                            } else {
                                mUserList.add(user)
                            }
                        }
                    }
                }

                // هنا يمكنك وضع الكود لفرز mUserList
                mUserList.sortBy { it.timestamp } // حسب اسم الخاصية التي تحمل الوقت
                mUserList.reverse() // اعتمادا على الوقت الأحدث أولاً

                if (!mUserList.isEmpty()) {
                    binding.searchList.setVisibility(View.VISIBLE)
                    binding.searchViewUserC.setVisibility(View.VISIBLE)
                    binding.noData.setVisibility(View.GONE)
                    userAdapter = userAdapter(this@ChatMainActivity, mUserList, true)
                    binding.searchList.setAdapter(userAdapter)
                    binding.shimmer.stopShimmer()
                    binding.shimmer.setVisibility(View.GONE)
                } else {
                    binding.noData.setVisibility(View.VISIBLE)
                    binding.searchList.setVisibility(View.GONE)
                    binding.searchViewUserC.setVisibility(View.GONE)
                    binding.shimmer.stopShimmer()
                    binding.shimmer.setVisibility(View.GONE)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
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
//                    this@ChatMainActivity, "update token successfully", Toast.LENGTH_SHORT
//                ).show()
                Log.d("yazan", "update token successfully")
            }
        }
    }

    private fun searchUser(str: String) {
        val userL: java.util.ArrayList<UserChats> = java.util.ArrayList<UserChats>()
        for (user1 in mUserList) {
            if (user1.nameUser!!.toLowerCase(Locale.getDefault())
                    .contains(str.lowercase(Locale.getDefault()))
            ) {
                userL.add(user1)
            }
        }
        userAdapter.searchList(userL)
    }

    private fun clickItem() {
        binding.imageBack.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
        binding.cardNotes.setOnClickListener {

        }
        //start Contact Activity
        binding.fab.setOnClickListener(View.OnClickListener {
            startActivity(
                Intent(
                    this@ChatMainActivity, ViewContacts::class.java
                )
            )
        })
        binding.searchViewUserC.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                searchUser(newText)
                return true
            }
        })
        binding.btnAddNote.setOnClickListener {
            val bottomSheetDialog =
                BottomSheetDialog(this@ChatMainActivity, R.style.BottomSheetDialogTheme)
            bottomSheetView = LayoutInflater.from(this@ChatMainActivity).inflate(
                R.layout.buttom_sheet_layout_add_note,
                findViewById<LinearLayout>(R.id.layout_dialog)
            )
            var note =
                bottomSheetView.findViewById<TextInputLayout>(R.id.Note_Add).editText!!.text.toString()
            bottomSheetView.findViewById<Button>(R.id.btn_Add_Note).setOnClickListener {
                if (bottomSheetView.findViewById<TextInputLayout>(R.id.Note_Add).editText!!.text.toString()
                        .isNotEmpty()
                ) {
                    coroutineScope.launch {
                        insertNote(
                            bottomSheetView.findViewById<TextInputLayout>(R.id.Note_Add).editText!!.text.toString()
                        )
                    }
                }
                bottomSheetDialog.dismiss()
            }
            bottomSheetDialog.setContentView(bottomSheetView)
            bottomSheetDialog.show()
        }
    }

    private fun setUpAppName() {
        val spannableString = SpannableString("Chat")
        val colorSpan =
            ForegroundColorSpan(ContextCompat.getColor(applicationContext, R.color.prim1))
        spannableString.setSpan(colorSpan, 2, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.titleChatMain.text = spannableString
    }


    private fun state(status: String) {
        val reference: DatabaseReference = if (FirebaseAuth.getInstance().currentUser == null) {
            FirebaseDatabase.getInstance().getReference("users").child(userid!!)
        } else {
            FirebaseDatabase.getInstance().getReference("users").child(auth!!.uid!!)
        }
        val hashMap = java.util.HashMap<String, Any>()
        hashMap["status"] = status
        reference.updateChildren(hashMap)
    }


    override fun onResume() {
        super.onResume()
        state("online")
    }

    override fun onPause() {
        super.onPause()
        state("offline")
    }
}