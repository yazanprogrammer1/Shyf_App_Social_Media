package com.example.shyf_.fragments

import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.AbsListView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.mvvm_architecture1.viewModel.ApiMethodesViewModel
import com.example.shyf_.Adapter.Posts_Home_Adapter
import com.example.shyf_.CheckValidation
import com.example.shyf_.R
import com.example.shyf_.databinding.FragmentAllBinding
import com.example.shyf_.model.Posts
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch


class AllPostFragment : Fragment() {
    private var _binding: FragmentAllBinding? = null
    private val binding get() = _binding!!
    private lateinit var mediaPlayerRefresh: MediaPlayer
    private lateinit var sharedPreferences: SharedPreferences
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private lateinit var navView: BottomNavigationView
    private lateinit var fadeInAnimation: Animation
    private lateinit var fadeOutAnimation: Animation
    private val apiMethodesViewModel: ApiMethodesViewModel by viewModels()
    var isScrolling: Boolean = false
    lateinit var manager: LinearLayoutManager
    lateinit var listPosts: ArrayList<Posts>
    var page = 1
    var limit = 5

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAllBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //............... code
        //initMediaPlayer
        navView = requireActivity().findViewById(R.id.nav_view)
        fadeInAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in_animation)
        fadeOutAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_out_animation)

        mediaPlayerRefresh = MediaPlayer.create(requireContext(), R.raw.refresh_sh)
        mediaPlayerRefresh.isLooping = false
        stopLoading(binding.animationView)
        manager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        listPosts = ArrayList()

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //... code
        refreshApp()
        // ابدأ تنفيذ الدالة getPosts() في الخلفية
        coroutineScope.launch {
            getAllPosts()
        }
        if (!CheckValidation(requireActivity()).isInternetAvailable()) {
            // إنشاء SnackBar
            val rootView = requireActivity().findViewById<View>(android.R.id.content)
            val snackbar =
                Snackbar.make(rootView, "انقطع الاتصال بالإنترنت", Snackbar.LENGTH_INDEFINITE)
            snackbar.setAction("إعادة المحاولة") {
                // هنا يمكنك إعادة المحاولة لإعادة الاتصال بالإنترنت
                if (CheckValidation(requireActivity()).isInternetAvailable()) {
                    coroutineScope.launch {
                        getAllPosts()
                    }
                    // استدعاء دالة إعادة تحميل البيانات
                }
            }
            snackbar.show()  // عرض SnackBar
        }

        //
        binding.listPopularPosts.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                // Scroll down
                if (dy > 10 && binding.extendedFab.isExtended) {
                    binding.extendedFab.shrink()
                    navView.visibility = View.GONE
                    hideBottomNavigationView()
                }

                // Scroll up
                if (dy < -10 && !binding.extendedFab.isExtended) {
                    binding.extendedFab.extend()
                    showBottomNavigationView()
                }

                // At the top
                if (!recyclerView.canScrollVertically(-10)) {
                    binding.extendedFab.extend()
                }

                // Pagination With Load More
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                //....
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true
                }
            }
        })


        // extendedFab
        binding.extendedFab.setOnClickListener {
            NavHostFragment.findNavController(this@AllPostFragment)
                .navigate(R.id.action_tab_home_to_requestUserFragment)
        }


    }

    // Method to hide BottomNavigationView with animation
    private fun hideBottomNavigationView() {
        if (navView.visibility == View.VISIBLE) {
            navView.startAnimation(fadeOutAnimation)
            navView.visibility = View.INVISIBLE
        }
    }

    // Method to show BottomNavigationView with animation
    private fun showBottomNavigationView() {
        if (navView.visibility != View.VISIBLE) {
            navView.startAnimation(fadeInAnimation)
            navView.visibility = View.VISIBLE
        }
    }


    private suspend fun getAllPosts() {
        lifecycleScope.launch {
            try {
                apiMethodesViewModel.getAllPost(binding.NoDataRv, binding.shimmerAllPost)
                    .observe(requireActivity(), Observer { posts ->
                        val adapter = Posts_Home_Adapter(
                            requireActivity(),
                            posts as ArrayList<Posts>,
                            requireActivity().findViewById(R.id.rootLayout)
                        )
                        val layoutManager: RecyclerView.LayoutManager = manager
                        binding.listPopularPosts.setLayoutManager(layoutManager)
                        binding.listPopularPosts.setHasFixedSize(true)
                        binding.listPopularPosts.setAdapter(adapter)
                        binding.shimmerAllPost.stopShimmer()
                        binding.shimmerAllPost.setVisibility(View.GONE)
                        binding.listPopularPosts.setVisibility(View.VISIBLE)
                        binding.NoDataRv.visibility = View.GONE
//                // انتظر بضعة ثوان قبل إيقاف التحميل الانمي (لغرض الاختبار)
//                Thread.sleep(5000)
                        // انتهت العملية، قم بإيقاف التحميل الانمي وإخفاء LottieAnimationView
                        stopLoading(binding.animationView)
                        Log.e("yazan", "Created :)")
                    })
            } catch (e: Exception) {
                // التعامل مع الأخطاء إذا كانت هناك
                Log.e("yazan", e.message!!)
                binding.shimmerAllPost.stopShimmer()
                binding.shimmerAllPost.setVisibility(View.GONE)
                binding.noData.visibility = View.VISIBLE
                binding.listPopularPosts.setVisibility(View.GONE)
//                // انتظر بضعة ثوان قبل إيقاف التحميل الانمي (لغرض الاختبار)
//                Thread.sleep(5000)
                // انتهت العملية، قم بإيقاف التحميل الانمي وإخفاء LottieAnimationView
                stopLoading(binding.animationView)
            }
        }
    }


    private fun refreshApp() {
        binding.swipeToRefresh.setOnRefreshListener {
            // ابدأ تنفيذ الدالة getPosts() في الخلفية             // إنشاء SnackBar
            // هنا يمكنك إعادة المحاولة لإعادة الاتصال بالإنترنت
            coroutineScope.launch {
                getAllPosts()
            }
            // استدعاء دالة إعادة تحميل البيانات
            binding.swipeToRefresh.isRefreshing = false
            mediaPlayerRefresh.start()
            // بدأ عملية التحميل
            startLoading(binding.animationView)
        }
    }


    private fun startLoading(lottieAnimationView: LottieAnimationView) {
        // عرض LottieAnimationView لبدء التحميل
        lottieAnimationView.visibility = View.VISIBLE
        // تشغيل الانمي
        lottieAnimationView.playAnimation()
    }

    private fun stopLoading(lottieAnimationView: LottieAnimationView) {
        // إيقاف التحميل الانمي وإخفاء LottieAnimationView
        lottieAnimationView.cancelAnimation()
        lottieAnimationView.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        // قم بإلغاء الـ CoroutineScope عندما تتم الإنتهاء من الـ Activity
        coroutineScope.cancel()
    }

}