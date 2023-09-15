package com.example.shyf_.ChatApp

import android.os.Bundle
import android.os.Handler
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shyf_.Adapter.userAdapter
import com.example.shyf_.R
import com.example.shyf_.databinding.ActivityViewContactsBinding
import com.example.shyf_.model.UserChats
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Locale

class ViewContacts : AppCompatActivity() {

    var buiding: ActivityViewContactsBinding? = null
    var list: ArrayList<UserChats>? = null
    var userAdapters: userAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buiding = ActivityViewContactsBinding.inflate(layoutInflater)
        setContentView(buiding!!.root)
        //... code
        buiding!!.recyclerContact.setHasFixedSize(true)
        buiding!!.recyclerContact.layoutManager = LinearLayoutManager(this@ViewContacts)
        buiding!!.searchViewUserC.clearFocus()
        list = ArrayList()
        Handler().postDelayed({ getUser() }, 4000)
        buiding!!.searchViewUserC.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                searchUser(newText)
                return true
            }
        })
        buiding!!.imageBack.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }

    private fun setUpAppName() {
        val spannableString = SpannableString("Select User")
        val colorSpan =
            ForegroundColorSpan(ContextCompat.getColor(applicationContext, R.color.prim1))
        spannableString.setSpan(colorSpan, 2, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        buiding!!.titleChatMain.text = spannableString
    }


    private fun getUser() {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val reference = FirebaseDatabase.getInstance().getReference("users")
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                list!!.clear()
                for (dataSnapshot in snapshot.children) {
                    val user: UserChats? = dataSnapshot.getValue(UserChats::class.java)
                    if (!user!!.id.equals(firebaseUser!!.uid)) {
                        list!!.add(user)
                    }
                    buiding!!.shimmer.stopShimmer()
                    buiding!!.shimmer.visibility = View.GONE
                    buiding!!.recyclerContact.visibility =View.VISIBLE
                    buiding!!.searchViewUserC.setVisibility(View.VISIBLE)
                    userAdapters = userAdapter(this@ViewContacts, list!!, false)
                    buiding!!.recyclerContact.adapter = userAdapters
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun searchUser(str: String) {
        val userL: java.util.ArrayList<UserChats> = java.util.ArrayList<UserChats>()
        for (user1 in list!!) {
            if (user1.nameUser!!.lowercase(Locale.getDefault()).contains(str.lowercase(Locale.getDefault()))) {
                userL.add(user1)
            }
        }
        userAdapters!!.searchList(userL)
    }

}