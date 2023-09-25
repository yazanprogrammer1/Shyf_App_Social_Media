package com.example.mvvm_architecture1.viewModel

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.mvvm_architecture1.repositery.ApiMethodes
import com.example.mvvm_architecture1.repositery.ApiMethodesImpl
import com.example.shyf_.model.Notes
import com.example.shyf_.model.Posts
import com.example.shyf_.model.Request
import com.example.shyf_.model.Storys
import com.example.shyf_.notification.Constants
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ApiMethodesViewModel : ViewModel() {

    private lateinit var database: FirebaseFirestore
    val optionsRepo: ApiMethodes = ApiMethodesImpl()

    suspend fun getAllPost(noData: ConstraintLayout,shimmer: ShimmerFrameLayout): LiveData<ArrayList<Posts>> {
        return withContext(Dispatchers.IO) {
            optionsRepo.getAllPost(noData,shimmer)
        }
    }

    suspend fun getRequestsUser(id: Int,noData: ConstraintLayout,shimmer: ShimmerFrameLayout): LiveData<ArrayList<Request>> {
        return withContext(Dispatchers.IO) {
            optionsRepo.getRequestsUser(id,noData,shimmer)
        }
    }

    suspend fun getPostsUser(id: Int,noData: ConstraintLayout,shimmer: ShimmerFrameLayout): LiveData<ArrayList<Posts>> {
        return withContext(Dispatchers.IO) {
            optionsRepo.getPostsUser(id,noData,shimmer)
        }
    }

    suspend fun getPostsUserIfFollow(id: Int,noData: ConstraintLayout,shimmer: ShimmerFrameLayout): LiveData<ArrayList<Posts>> {
        return withContext(Dispatchers.IO) {
            optionsRepo.getPostsUserIfFollow(id,noData,shimmer)
        }
    }

    suspend fun getPostsSavedUser(id: Int,noData: ConstraintLayout,shimmer: ShimmerFrameLayout): LiveData<ArrayList<Posts>> {
        return withContext(Dispatchers.IO) {
            optionsRepo.getPostsSavedUser(id,noData,shimmer)
        }
    }

    suspend fun getPostsByCountry(nameCountry: String,noData: ConstraintLayout,shimmer: ShimmerFrameLayout): LiveData<ArrayList<Posts>> {
        return withContext(Dispatchers.IO) {
            optionsRepo.getPostsByCountry(nameCountry,noData,shimmer)
        }
    }

    suspend fun getNoteForUser(id: Int): LiveData<ArrayList<Notes>> {
        return withContext(Dispatchers.IO) {
            optionsRepo.getNoteForUser(id)
        }
    }

    suspend fun getStoryForUser(id: Int,noData: ConstraintLayout,shimmer: ShimmerFrameLayout): LiveData<ArrayList<Storys>> {
        return withContext(Dispatchers.IO) {
            optionsRepo.getStoryForUser(id,noData,shimmer)
        }
    }


    suspend fun getNotesUserIfFollow(id: Int,noData: ConstraintLayout,shimmer: ShimmerFrameLayout): LiveData<ArrayList<Notes>> {
        return withContext(Dispatchers.IO) {
            optionsRepo.getNotesUserIfFollow(id,noData,shimmer)
        }
    }



}