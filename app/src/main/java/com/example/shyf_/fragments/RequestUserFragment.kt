package com.example.shyf_.fragments

import android.media.MediaPlayer
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
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mvvm_architecture1.viewModel.ApiMethodesViewModel
import com.example.shyf_.Adapter.Request_Adapter_ForUser
import com.example.shyf_.R
import com.example.shyf_.databinding.FragmentRequestUserBinding
import com.example.shyf_.model.SwipeListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class RequestUserFragment : Fragment() {


    private var _binding: FragmentRequestUserBinding? = null
    private val binding get() = _binding!!
    private lateinit var mediaPlayerRefresh: MediaPlayer
    private lateinit var adapter: Request_Adapter_ForUser
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val apiMethodesViewModel: ApiMethodesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRequestUserBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //............... code
        //initMediaPlayer
        mediaPlayerRefresh = MediaPlayer.create(requireContext(), R.raw.refresh_sh)
        mediaPlayerRefresh.isLooping = false
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //..
        coroutineScope.launch {
            getRequestsUser()
        }
        refreshApp()
        // ربط ItemTouchHelper بـ RecyclerView

        val swipeListener = object : SwipeListener(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                //......
                when (direction) {
                    ItemTouchHelper.LEFT -> {
//                        val message = getString(R.string.messageDelete)
                        val p = getString(R.string.delete)
//                        val n = getString(R.string.messageDelete)
                        adapter.deleteItem(
                            viewHolder.adapterPosition,
                            "Are you sure to delete ?",
                            p,
                            "no"
                        )

                    }

                    ItemTouchHelper.RIGHT -> {

                    }
                }
            }
        }

        val touchHelper = ItemTouchHelper(swipeListener)
        touchHelper.attachToRecyclerView(binding.listRequest)

        binding.imageBack.setOnClickListener {
            NavHostFragment.findNavController(this@RequestUserFragment)
                .navigate(R.id.action_requestUserFragment_to_tab_home)
        }

    }

    // = callbackFlow<ArrayList<Request>>
    private suspend fun getRequestsUser()  {
        lifecycleScope.launch {
            try {
                val shared =
                    requireActivity().getSharedPreferences("user_data", AppCompatActivity.MODE_PRIVATE)
                var id = shared.getInt("id", 0).toString().toInt()
                apiMethodesViewModel.getRequestsUser(id,binding.NoDataRv,binding.shimmer)
                    .observe(requireActivity(), Observer { requests ->
                        adapter = Request_Adapter_ForUser(
                            requireActivity(),
                            requests
                        )
                        val layoutManager: RecyclerView.LayoutManager =
                            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                        binding.listRequest.setLayoutManager(layoutManager)
                        binding.listRequest.setHasFixedSize(true)
                        binding.listRequest.setAdapter(adapter)
                        binding.shimmer.stopShimmer()
                        binding.shimmer.setVisibility(View.GONE)
                        binding.listRequest.setVisibility(View.VISIBLE)
                        binding.NoDataRv.visibility = View.GONE
                        Log.e("yazan", "Created :)")
                    })
            } catch (e: Exception) {
                // التعامل مع الأخطاء إذا كانت هناك
                Log.e("yazan", e.message!!)
                binding.shimmer.stopShimmer()
                binding.NoDataRv.visibility = View.VISIBLE
                binding.shimmer.setVisibility(View.GONE)
                binding.listRequest.setVisibility(View.GONE)
            }
        }
    }


    private fun refreshApp() {
        binding.swipeToRefresh.setOnRefreshListener {
            binding.swipeToRefresh.isRefreshing = false
            mediaPlayerRefresh.start()
            coroutineScope.launch {
                getRequestsUser()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // قم بإلغاء الـ CoroutineScope عندما تتم الإنتهاء من الـ Activity
        coroutineScope.cancel()
    }


}