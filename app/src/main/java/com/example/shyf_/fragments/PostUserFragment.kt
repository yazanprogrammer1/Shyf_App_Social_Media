package com.example.shyf_.fragments

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mvvm_architecture1.viewModel.ApiMethodesViewModel
import com.example.shyf_.Adapter.Posts_Home_Adapter
import com.example.shyf_.R
import com.example.shyf_.databinding.FragmentPostUserBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PostUserFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PostUserFragment : Fragment() {

    private var _binding: FragmentPostUserBinding? = null
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private lateinit var navView: BottomNavigationView
    private lateinit var fadeInAnimation: Animation
    private lateinit var fadeOutAnimation: Animation
    private val apiMethodesViewModel: ApiMethodesViewModel by viewModels()


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPostUserBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //... code
        navView = requireActivity().findViewById(R.id.nav_view)
        fadeInAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in_animation)
        fadeOutAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_out_animation)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //...
        coroutineScope.launch {
            getPostsUser()
        }
        if (!isInternetAvailable()) {
            // إنشاء SnackBar
            val rootView = requireActivity().findViewById<View>(android.R.id.content)
            val snackbar =
                Snackbar.make(rootView, "انقطع الاتصال بالإنترنت", Snackbar.LENGTH_INDEFINITE)
            snackbar.setAction("إعادة المحاولة") {
                // هنا يمكنك إعادة المحاولة لإعادة الاتصال بالإنترنت
                if (isInternetAvailable()) {
                    coroutineScope.launch {
                        getPostsUser()
                    }
                    // استدعاء دالة إعادة تحميل البيانات
                }
            }
            snackbar.show()  // عرض SnackBar
        }

    }

    private fun isInternetAvailable(): Boolean {
        val connectivityManager =
            requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

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


    private suspend fun getPostsUser() {
        lifecycleScope.launch {
            try {
                val shared =
                    requireActivity().getSharedPreferences(
                        "user_data",
                        AppCompatActivity.MODE_PRIVATE
                    )
                var id = shared.getInt("id", 0).toString().toInt()
                apiMethodesViewModel.getPostsUser(id,binding.NoDataRv,binding.shimmer)
                    .observe(requireActivity(), Observer { posts ->
                        val adapter =
                            Posts_Home_Adapter(requireActivity(), posts, binding.rootLayout)
                        val layoutManager: RecyclerView.LayoutManager =
                            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                        binding.listPopularPosts.setLayoutManager(layoutManager)
                        binding.listPopularPosts.setHasFixedSize(true)
                        binding.listPopularPosts.setAdapter(adapter)
                        binding.shimmer.stopShimmer()
                        binding.shimmer.setVisibility(View.GONE)
                        binding.listPopularPosts.setVisibility(View.VISIBLE)
                        binding.NoDataRv.visibility = View.GONE
                        Log.e("yazan", "Created :)")
                    })
            } catch (e: Exception) {
                // التعامل مع الأخطاء إذا كانت هناك
                Log.e("yazan", e.message!!)
                binding.shimmer.stopShimmer()
                binding.noData.visibility = View.VISIBLE
                binding.shimmer.setVisibility(View.GONE)
                binding.listPopularPosts.setVisibility(View.VISIBLE)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // قم بإلغاء الـ CoroutineScope عندما تتم الإنتهاء من الـ Activity
        coroutineScope.cancel()
    }
}