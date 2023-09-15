package com.example.shyf_.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mvvm_architecture1.viewModel.ApiMethodesViewModel
import com.example.shyf_.Adapter.Posts_Home_Adapter
import com.example.shyf_.databinding.FragmentSaveUserPostBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SaveUserPostFragment : Fragment() {

    private var _binding: FragmentSaveUserPostBinding? = null
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val apiMethodesViewModel: ApiMethodesViewModel by viewModels()


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSaveUserPostBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //... code

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //...=============== code
        coroutineScope.launch {
            getPostsSavedUser()
        }
    }


    private suspend fun getPostsSavedUser() {
        lifecycleScope.launch {
            try {
                val shared =
                    requireActivity().getSharedPreferences(
                        "user_data",
                        AppCompatActivity.MODE_PRIVATE
                    )
                var id = shared.getInt("id", 0).toString().toInt()
                apiMethodesViewModel.getPostsSavedUser(id,binding.NoDataRv,binding.shimmer)
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
                binding.shimmer.setVisibility(View.GONE)
                binding.NoDataRv.visibility = View.VISIBLE
                binding.listPopularPosts.setVisibility(View.VISIBLE)
            }
        }
    }
}