package com.example.shyf_.ui.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mvvm_architecture1.viewModel.ApiMethodesViewModel
import com.example.shyf_.Adapter.Posts_Home_Adapter
import com.example.shyf_.R
import com.example.shyf_.databinding.FragmentSearchBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var bottomSheetView: View
    lateinit var nameCountry: String
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val apiMethodesViewModel: ApiMethodesViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val profileViewModel = ViewModelProvider(this).get(SearchViewModel::class.java)

        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //...................
        val shared =
            requireActivity().getSharedPreferences(
                "user_data",
                AppCompatActivity.MODE_PRIVATE
            )
        nameCountry = shared.getString("CountryPost", "").toString()
        if (nameCountry == null) {
            nameCountry = "فلسطين"
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //.....
        binding.imageFilter.setOnClickListener {
            val bottomSheetDialog =
                BottomSheetDialog(requireActivity(), R.style.BottomSheetDialogTheme)
            bottomSheetView = LayoutInflater.from(requireActivity()).inflate(
                R.layout.buttom_sheet_layout_filter,
                requireActivity().findViewById<LinearLayout>(R.id.layout_dialog)
            )
            bottomSheetView.findViewById<TextInputLayout>(R.id.Country_filter).editText!!.setText(
                nameCountry
            )
            bottomSheetView.findViewById<Button>(R.id.btn_Filter).setOnClickListener {
                nameCountry =
                    bottomSheetView.findViewById<TextInputLayout>(R.id.Country_filter).editText!!.text.toString()
                        .trim()
                coroutineScope.launch {
                    getPostsByCountry()
                }
                bottomSheetDialog.dismiss()
            }
            bottomSheetDialog.setContentView(bottomSheetView)
            bottomSheetDialog.show()
        }
        coroutineScope.launch {
            getPostsByCountry()
        }
        refreshApp()
    }

    private suspend fun getPostsByCountry() {
        lifecycleScope.launch {
            try {
                apiMethodesViewModel.getPostsByCountry(nameCountry,binding.NoDataRv,binding.shimmerAllPost)
                    .observe(requireActivity(), Observer { posts ->
                        val adapter =
                            Posts_Home_Adapter(requireActivity(), posts, binding.rootLayout)
                        val layoutManager: RecyclerView.LayoutManager =
                            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                        binding.listPopularPosts.setLayoutManager(layoutManager)
                        binding.listPopularPosts.setHasFixedSize(true)
                        binding.listPopularPosts.setAdapter(adapter)
                        binding.shimmerAllPost.stopShimmer()
                        binding.shimmerAllPost.setVisibility(View.GONE)
                        binding.listPopularPosts.setVisibility(View.VISIBLE)
                        binding.NoDataRv.visibility = View.GONE
                        adapter.notifyDataSetChanged()
                        Log.e("yazan", "Created :)")
                    })
            } catch (e: Exception) {
                Log.e("yazan", e.message!!)
                binding.shimmerAllPost.stopShimmer()
                binding.shimmerAllPost.setVisibility(View.GONE)
                binding.NoDataRv.visibility = View.VISIBLE
                binding.listPopularPosts.setVisibility(View.GONE)
                Toast.makeText(requireContext(), e.message.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun refreshApp() {
        binding.swipeToRefresh.setOnRefreshListener {
            coroutineScope.launch {
                getPostsByCountry()
            }
            binding.swipeToRefresh.isRefreshing = false
        }
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
}