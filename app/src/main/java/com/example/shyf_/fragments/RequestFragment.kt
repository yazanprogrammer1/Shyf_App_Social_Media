package com.example.shyf_.fragments

import android.graphics.Bitmap
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mvvm_architecture1.viewModel.ApiMethodesViewModel
import com.example.shyf_.Adapter.NotesUserAdapter
import com.example.shyf_.Adapter.Posts_Home_Adapter
import com.example.shyf_.Adapter.Request_Adapter_ForUser
import com.example.shyf_.R
import com.example.shyf_.apis.RetrofitInsertNotes
import com.example.shyf_.databinding.FragmentFollowedBinding
import com.example.shyf_.model.Result_AddNotes
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar

class RequestFragment : Fragment() {

    private var _binding: FragmentFollowedBinding? = null
    private val binding get() = _binding!!
    private lateinit var mediaPlayerRefresh: MediaPlayer
    private lateinit var adapter: Request_Adapter_ForUser
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val apiMethodesViewModel: ApiMethodesViewModel by viewModels()
    lateinit var bottomSheetView: View
    lateinit var rootView: ConstraintLayout // تعريف المتغير هنا
    lateinit var name: String
    lateinit var userImage: String
    var idUser: Int = 0
    lateinit var manager: LinearLayoutManager
    lateinit var manager2: LinearLayoutManager

    var bitmap: Bitmap? = null
    var pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            // Image Selection
            bitmap = MediaStore.Images.Media.getBitmap(
                requireActivity().contentResolver, uri
            )
            bottomSheetView.findViewById<ImageView>(R.id.img_Story).setImageBitmap(bitmap)
        } else {
            // No image
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFollowedBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //............... code
        initData()
        onClickItem()
        rootView =
            requireActivity().findViewById(R.id.rootLayout)
        mediaPlayerRefresh = MediaPlayer.create(requireContext(), R.raw.refresh_sh)
        mediaPlayerRefresh.isLooping = false
        coroutineScope.launch {
            getNoteForUser()
        }
        coroutineScope.launch {
            getNotesUserIfFollow()
        }
        coroutineScope.launch {
            getPostsUserIfFollow()
        }
        refreshApp()
        return root
    }

    private fun initData() {
        manager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        manager2 = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        val shared =
            requireActivity().getSharedPreferences("user_data", AppCompatActivity.MODE_PRIVATE)
        idUser = shared.getInt("id", 0).toString().toInt()
        name = shared.getString("name", "").toString()
        userImage = shared.getString("userImage", "").toString()

        binding.username.text = name.toString()
        Glide.with(requireActivity()).load("http://10.0.2.2/db/shyf/${userImage}")
            .apply(RequestOptions().override(600, 600)).error(R.drawable.profile_sh)
            .into(binding.profileImageStory)
    }

    private fun onClickItem() {
        binding.btnAddNote.setOnClickListener {
            val bottomSheetDialog =
                BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
            bottomSheetView = LayoutInflater.from(requireContext()).inflate(
                R.layout.buttom_sheet_layout_add_note,
                requireActivity().findViewById<LinearLayout>(R.id.layout_dialog)
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

    private suspend fun insertNote(note: String) {
        lifecycleScope.launch {
            try {
                val call: Call<Result_AddNotes> =
                    RetrofitInsertNotes.getInstance().myApi.InsertNote(idUser, note)
                call.enqueue(object : Callback<Result_AddNotes> {
                    override fun onResponse(
                        call: Call<Result_AddNotes>,
                        response: Response<Result_AddNotes>
                    ) {
                        if (response.body()!!.error == false) {
                            showCenteredMessage("Added")
                            Log.e("yazan", "Get Note Sucssfully:)")
                        } else {
                            showCenteredMessage("Not added")
                        }
                    }

                    override fun onFailure(call: Call<Result_AddNotes>, t: Throwable) {
                        Log.e("yazan", t.message!!)
                        showCenteredMessage("Not added")
                    }
                })
            } catch (e: Exception) {

            }
        }
    }

    private suspend fun getNoteForUser() {
        lifecycleScope.launch {
            try {
                apiMethodesViewModel.getNoteForUser(idUser)
                    .observe(requireActivity(), Observer { note ->
                        if (note.get(0).note.equals("No Data")) {
                            binding.btnAddNote.setVisibility(View.VISIBLE)
                            binding.cardNotes.visibility = View.INVISIBLE
                        } else {
                            binding.textNotes.text = note.get(0).note.toString()
                            binding.btnAddNote.setVisibility(View.GONE)
                            binding.cardNotes.visibility = View.VISIBLE
                            Log.e("yazan", "Created :)")
                        }
                    })
            } catch (e: Exception) {
                // التعامل مع الأخطاء إذا كانت هناك
                Log.e("yazan", e.message!!)
                binding.btnAddNote.setVisibility(View.VISIBLE)
                binding.cardNotes.visibility = View.GONE
            }
        }
    }

    private suspend fun getNotesUserIfFollow() {
        lifecycleScope.launch {
            try {
                apiMethodesViewModel.getNotesUserIfFollow(
                    idUser,
                    binding.NoDataRv,
                    binding.shimmerStorys
                )
                    .observe(requireActivity(), Observer { notes ->
                        val adapter = NotesUserAdapter(
                            requireActivity(),
                            notes,
                            requireActivity().findViewById(R.id.rootLayout)
                        )
                        val layoutManager: RecyclerView.LayoutManager = manager
                        binding.NoteList.layoutManager = manager
                        binding.NoteList.setHasFixedSize(true)
                        binding.NoteList.adapter = adapter
                        binding.shimmerStorys.stopShimmer()
                        binding.shimmerStorys.visibility = View.GONE
                        binding.NoteList.visibility = View.VISIBLE
                        Log.e("yazan", "Created :)")
                    })
            } catch (e: Exception) {
                // التعامل مع الأخطاء إذا كانت هناك
                Log.e("yazan", e.message!!)
                binding.shimmerStorys.stopShimmer()
                binding.shimmerStorys.visibility = View.GONE
                binding.NoteList.visibility = View.GONE
            }
        }
    }

    private suspend fun getPostsUserIfFollow() {
        lifecycleScope.launch {
            try {
                apiMethodesViewModel.getPostsUserIfFollow(idUser, binding.NoDataRv, binding.shimmer)
                    .observe(requireActivity(), Observer { posts ->
                        val adapter = Posts_Home_Adapter(
                            requireActivity(),
                            posts,
                            requireActivity().findViewById(R.id.rootLayout)
                        )
                        val layoutManager: RecyclerView.LayoutManager = manager2
                        binding.listPostsFollowed.layoutManager = manager2
                        binding.listPostsFollowed.setHasFixedSize(true)
                        binding.listPostsFollowed.adapter = adapter
                        binding.shimmer.stopShimmer()
                        binding.shimmer.visibility = View.GONE
                        binding.listPostsFollowed.visibility = View.VISIBLE
                        binding.NoDataRv.visibility = View.GONE
                        Log.e("yazan", "Created :)")
                    })
            } catch (e: Exception) {
                // التعامل مع الأخطاء إذا كانت هناك
                Log.e("yazan", e.message!!)
                binding.shimmer.stopShimmer()
                binding.shimmer.visibility = View.GONE
                binding.listPostsFollowed.visibility = View.GONE
            }
        }
    }

    private fun getTimeAdd(): String? {
        // الحصول على التاريخ والوقت الحاليين من الجهاز
        val currentDateTime = Calendar.getInstance()
        // تنسيق التاريخ والوقت
        val dateFormat = SimpleDateFormat("MM/dd/yyyy hh:mm:ss a")
        val formattedDateTime = dateFormat.format(currentDateTime.time)
        // إخراج التاريخ والوقت المجتمعين بالتنسيق المطلوب
        return formattedDateTime.toString()
    }

    private fun showCenteredMessage(message: String) {
        val snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_LONG)
        val snackbarView = snackbar.view
        // تعديل حجم الـ snackbarView ليكون حسب حجم الكلمة الموجودة فيه
        snackbarView.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
        snackbarView.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        // تعديل تخطيط الرسالة لتوسيطها في وسط الشاشة
        val params = snackbarView.layoutParams as CoordinatorLayout.LayoutParams
        params.gravity = Gravity.CENTER
        snackbarView.layoutParams = params
        // تعيين مدة ظهور الـ Snackbar إلى ثانيتين
//        snackbar.duration = 2000 // بالمللي ثانية
        snackbar.show()
    }

    private fun refreshApp() {
        binding.swipeToRefresh.setOnRefreshListener {
            binding.swipeToRefresh.isRefreshing = false
            mediaPlayerRefresh.start()
            coroutineScope.launch {
                getNoteForUser()
                getNotesUserIfFollow()
                getPostsUserIfFollow()
            }
        }
    }
}