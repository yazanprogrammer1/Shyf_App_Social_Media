package com.example.mvvm_architecture1.repositery

import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.MutableLiveData
import com.example.shyf_.model.Notes
import com.example.shyf_.model.Posts
import com.example.shyf_.model.Request
import com.example.shyf_.model.Result_AddNotes
import com.example.shyf_.model.Storys
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerFrameLayout

interface ApiMethodes {
    fun getAllPost(noData: ConstraintLayout,shimmer: ShimmerFrameLayout): MutableLiveData<ArrayList<Posts>>
    fun getRequestsUser(id: Int,noData: ConstraintLayout,shimmer: ShimmerFrameLayout): MutableLiveData<ArrayList<Request>>
    fun getPostsUser(id: Int,noData: ConstraintLayout,shimmer: ShimmerFrameLayout): MutableLiveData<ArrayList<Posts>>
    fun getPostsUserIfFollow(id: Int,noData: ConstraintLayout,shimmer: ShimmerFrameLayout): MutableLiveData<ArrayList<Posts>>
    fun getPostsSavedUser(id: Int,noData: ConstraintLayout,shimmer: ShimmerFrameLayout): MutableLiveData<ArrayList<Posts>>
    fun getPostsByCountry(nameCountry: String,noData: ConstraintLayout,shimmer: ShimmerFrameLayout): MutableLiveData<ArrayList<Posts>>
    fun getNoteForUser(id: Int): MutableLiveData<ArrayList<Notes>>
    fun insertNote(id: Int, note: String): MutableLiveData<ArrayList<Result_AddNotes>>
    fun getNotesUserIfFollow(id:Int,noData: ConstraintLayout,shimmer: ShimmerFrameLayout): MutableLiveData<ArrayList<Notes>>
    fun getStoryForUser(id: Int,noData: ConstraintLayout,shimmer: ShimmerFrameLayout): MutableLiveData<ArrayList<Storys>>

}