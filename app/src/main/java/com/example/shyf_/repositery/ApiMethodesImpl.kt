package com.example.mvvm_architecture1.repositery

import android.util.Log
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.MutableLiveData
import com.example.shyf_.apis.RetrofitGetNoteForUser
import com.example.shyf_.apis.RetrofitGetNotesUserIfFollow
import com.example.shyf_.apis.RetrofitGetPosts
import com.example.shyf_.apis.RetrofitGetPostsByContry
import com.example.shyf_.apis.RetrofitGetPostsForUser
import com.example.shyf_.apis.RetrofitGetPostsUserIfFollow
import com.example.shyf_.apis.RetrofitGetRequestssForUser
import com.example.shyf_.apis.RetrofitGetSavedPost
import com.example.shyf_.apis.RetrofitGetStoryForUser
import com.example.shyf_.model.Notes
import com.example.shyf_.model.Posts
import com.example.shyf_.model.Request
import com.example.shyf_.model.Result_AddNotes
import com.example.shyf_.model.Storys
import com.facebook.shimmer.ShimmerFrameLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ApiMethodesImpl : ApiMethodes {
    // هذا الكلاس سيعمل امليمنت لكلاس ال interface


    override fun getAllPost(noData: ConstraintLayout,shimmer: ShimmerFrameLayout): MutableLiveData<ArrayList<Posts>> {
        val infoMutLiveData = MutableLiveData<ArrayList<Posts>>()
        val call: Call<List<Posts>> = RetrofitGetPosts.getInstance().myApi.getPosts()
        call.enqueue(object : Callback<List<Posts>> {
            override fun onResponse(call: Call<List<Posts>>, response: Response<List<Posts>>) {
                val productsList: ArrayList<Posts> = response.body() as ArrayList<Posts>
                infoMutLiveData.postValue(productsList)
                Log.e("yazan", "Created :)")
            }

            override fun onFailure(call: Call<List<Posts>>, t: Throwable) {
                Log.e("yazan", t.message!!)
                noData.visibility = View.VISIBLE
                shimmer.visibility = View.GONE
            }
        })
        return infoMutLiveData
    }

    override fun getRequestsUser(id: Int,noData: ConstraintLayout,shimmer: ShimmerFrameLayout): MutableLiveData<ArrayList<Request>> {
        val infoMutLiveData = MutableLiveData<ArrayList<Request>>()
        val call: Call<List<Request>> =
            RetrofitGetRequestssForUser.getInstance().myApi.getRequestOfUser(id)
        call.enqueue(object : Callback<List<Request>> {
            override fun onResponse(call: Call<List<Request>>, response: Response<List<Request>>) {
                val productsList: ArrayList<Request> = response.body() as ArrayList
                infoMutLiveData.postValue(productsList)
                Log.e("yazan", "Created :)")
            }

            override fun onFailure(call: Call<List<Request>>, t: Throwable) {
                Log.e("yazan", t.message!!)
                noData.visibility = View.VISIBLE
                shimmer.visibility = View.GONE
            }
        })
        return infoMutLiveData
    }

    override fun getPostsUser(id: Int,noData: ConstraintLayout,shimmer: ShimmerFrameLayout): MutableLiveData<ArrayList<Posts>> {
        val infoMutLiveData = MutableLiveData<ArrayList<Posts>>()
        val call: Call<List<Posts>> = RetrofitGetPostsForUser.getInstance().myApi.getPostsOfUser(id)
        call.enqueue(object : Callback<List<Posts>> {
            override fun onResponse(call: Call<List<Posts>>, response: Response<List<Posts>>) {
                val productsList: ArrayList<Posts> = response.body() as ArrayList
                infoMutLiveData.postValue(productsList)
                Log.e("yazan", "Created :)")
            }

            override fun onFailure(call: Call<List<Posts>>, t: Throwable) {
                Log.e("yazan", t.message!!)
                noData.visibility = View.VISIBLE
                shimmer.visibility = View.GONE
            }
        })
        return infoMutLiveData
    }

    override fun getPostsUserIfFollow(id: Int,noData: ConstraintLayout,shimmer: ShimmerFrameLayout): MutableLiveData<ArrayList<Posts>> {
        val infoMutLiveData = MutableLiveData<ArrayList<Posts>>()
        val call: Call<List<Posts>> =
            RetrofitGetPostsUserIfFollow.getInstance().myApi.getPostsUserIfFollow(id)
        call.enqueue(object : Callback<List<Posts>> {
            override fun onResponse(call: Call<List<Posts>>, response: Response<List<Posts>>) {
                val productsList: ArrayList<Posts> = response.body() as ArrayList
                infoMutLiveData.postValue(productsList)
                Log.e("yazan", "Get Note Sucssfully:)")
            }

            override fun onFailure(call: Call<List<Posts>>, t: Throwable) {
                Log.e("yazan", t.message!!)
                noData.visibility = View.VISIBLE
                shimmer.visibility = View.GONE
            }
        })
        return infoMutLiveData
    }

    override fun getPostsSavedUser(id: Int,noData: ConstraintLayout,shimmer: ShimmerFrameLayout): MutableLiveData<ArrayList<Posts>> {
        val infoMutLiveData = MutableLiveData<ArrayList<Posts>>()
        val call: Call<List<Posts>> = RetrofitGetSavedPost.getInstance().myApi.getSavedPosts(id)
        call.enqueue(object : Callback<List<Posts>> {
            override fun onResponse(call: Call<List<Posts>>, response: Response<List<Posts>>) {
                val productsList: ArrayList<Posts> = response.body() as ArrayList
                infoMutLiveData.postValue(productsList)
                Log.e("yazan", "Created save:)")
            }

            override fun onFailure(call: Call<List<Posts>>, t: Throwable) {
                Log.e("yazan", t.message!!)
                noData.visibility = View.VISIBLE
                shimmer.visibility = View.GONE
            }
        })
        return infoMutLiveData
    }

    override fun getPostsByCountry(nameCountry: String,noData: ConstraintLayout,shimmer: ShimmerFrameLayout): MutableLiveData<ArrayList<Posts>> {
        val infoMutLiveData = MutableLiveData<ArrayList<Posts>>()
        val call: Call<List<Posts>> =
            RetrofitGetPostsByContry.getInstance().myApi.getPostsByContry(nameCountry)
        call.enqueue(object : Callback<List<Posts>> {
            override fun onResponse(call: Call<List<Posts>>, response: Response<List<Posts>>) {
                val productsList: ArrayList<Posts> = response.body() as ArrayList<Posts>
                infoMutLiveData.postValue(productsList)
                Log.e("yazan", "Created :)")
            }

            override fun onFailure(call: Call<List<Posts>>, t: Throwable) {
                Log.e("yazan", t.message!!)
                noData.visibility = View.VISIBLE
                shimmer.visibility = View.GONE
            }
        })
        return infoMutLiveData
    }

    override fun getNoteForUser(id: Int): MutableLiveData<ArrayList<Notes>> {
        val infoMutLiveData = MutableLiveData<ArrayList<Notes>>()
        val call: Call<List<Notes>> = RetrofitGetNoteForUser.getInstance().myApi.getNoteForUser(id)
        call.enqueue(object : Callback<List<Notes>> {
            override fun onResponse(call: Call<List<Notes>>, response: Response<List<Notes>>) {
                val productsList: ArrayList<Notes> = response.body() as ArrayList
                infoMutLiveData.postValue(productsList)
                Log.e("yazan", "Get Note Sucssfully:)")
            }

            override fun onFailure(call: Call<List<Notes>>, t: Throwable) {
                Log.e("yazan", t.message!!)
            }
        })
        return infoMutLiveData
    }

    override fun insertNote(id: Int, note: String): MutableLiveData<ArrayList<Result_AddNotes>> {
        val infoMutLiveData = MutableLiveData<ArrayList<Result_AddNotes>>()
        return infoMutLiveData
    }

    override fun getNotesUserIfFollow(id: Int,noData: ConstraintLayout,shimmer: ShimmerFrameLayout): MutableLiveData<ArrayList<Notes>> {
        val infoMutLiveData = MutableLiveData<ArrayList<Notes>>()
        val call: Call<List<Notes>> =
            RetrofitGetNotesUserIfFollow.getInstance().myApi.getNotesUserIfFollow(id)
        call.enqueue(object : Callback<List<Notes>> {
            override fun onResponse(call: Call<List<Notes>>, response: Response<List<Notes>>) {
                val productsList: ArrayList<Notes> = response.body() as ArrayList
                infoMutLiveData.postValue(productsList)
                Log.e("yazan", "Get Note Sucssfully:)")
            }

            override fun onFailure(call: Call<List<Notes>>, t: Throwable) {
                Log.e("yazan", t.message!!)
                noData.visibility = View.VISIBLE
                shimmer.visibility =View.GONE
            }
        })
        return infoMutLiveData
    }

    override fun getStoryForUser(id: Int,noData: ConstraintLayout,shimmer: ShimmerFrameLayout): MutableLiveData<ArrayList<Storys>> {
        val infoMutLiveData = MutableLiveData<ArrayList<Storys>>()
        val call: Call<List<Storys>> =
            RetrofitGetStoryForUser.getInstance().myApi.getStoryForUser(id)
        call.enqueue(object : Callback<List<Storys>> {
            override fun onResponse(call: Call<List<Storys>>, response: Response<List<Storys>>) {
                val productsList: ArrayList<Storys> = response.body() as ArrayList
                infoMutLiveData.postValue(productsList)
                Log.e("yazan", "Get Story Sucssfully:)")
            }

            override fun onFailure(call: Call<List<Storys>>, t: Throwable) {
                Log.e("yazan", t.message!!)
                noData.visibility = View.VISIBLE
                shimmer.visibility = View.GONE
            }
        })
        return infoMutLiveData
    }


}