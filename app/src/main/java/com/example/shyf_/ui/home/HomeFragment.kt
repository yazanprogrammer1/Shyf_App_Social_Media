package com.example.shyf_.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ActivityCompat.recreate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.example.shyf_.Adapter.ViewPagerAdapter
import com.example.shyf_.ChatApp.ChatMainActivity
import com.example.shyf_.R
import com.example.shyf_.databinding.FragmentHomeBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.thekhaeng.pushdownanim.PushDownAnim
import java.util.Locale


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var pager: ViewPager // creating object of ViewPager
    private lateinit var tab: TabLayout  // creating object of TabLayout
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //............... code

        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //.... code home
        PushDownAnim.setPushDownAnimTo(
            binding.imgSettingApp
        ).setScale(PushDownAnim.MODE_SCALE, 0.90f)
        setUpAppName()
        setupViewPager()
        clickItem()
    }


    @SuppressLint("MissingInflatedId")
    private fun clickItem() {
        binding.imgSettingApp.setOnClickListener {
            val bottomSheetDialog =
                BottomSheetDialog(requireActivity(), R.style.BottomSheetStyle)
            var bottomSheetView = LayoutInflater.from(requireActivity()).inflate(
                R.layout.buttom_sheet_layout_settings,
                requireActivity().findViewById<LinearLayout>(R.id.layout_dialog)
            )
            sharedPreferences =
                requireActivity().getSharedPreferences(
                    "user_data",
                    AppCompatActivity.MODE_PRIVATE
                )
            var isNightMode = sharedPreferences.getBoolean("NightMode", false)
            bottomSheetView.findViewById<SwitchCompat>(R.id.nightModeSwitch).isChecked = isNightMode
            bottomSheetView.findViewById<SwitchCompat>(R.id.nightModeSwitch)
                .setOnCheckedChangeListener { _, isChecked ->
                    setNightMode(isChecked)
                    // حفظ الحالة الجديدة في SharedPreferences
                    sharedPreferences.edit().putBoolean("NightMode", isChecked).apply()
                }
            bottomSheetView.findViewById<ImageView>(R.id.Go_To_Change_the_language)
                .setOnClickListener {
                    toggleLanguage()
                    recreate(requireActivity()) // إعادة تحميل التطبيق لتطبيق تغيير اللغة
                }
            bottomSheetDialog.setContentView(bottomSheetView)
            bottomSheetDialog.show()
        }
        binding.imgMessageing.setOnClickListener {
//            // احصل على اسم التطبيق المراد فتحه
//            val appPackageName = "com.example.shyf_message"
//            // تحقق مما إذا كان التطبيق مثبتًا على الجهاز
//            val packageManager = requireContext().packageManager
//            val isAppInstalled = packageManager.getLaunchIntentForPackage(appPackageName) != null
//            // إذا كان التطبيق مثبتًا، فافتحه
//            if (isAppInstalled) {
//                // احصل على نية فتح التطبيق
//                val intent = packageManager.getLaunchIntentForPackage(appPackageName)
//                // افتح التطبيق
//                startActivity(intent!!)
//            } else {
//                // إذا لم يكن التطبيق مثبتًا، فانتقل إلى المتجر لتنزيله
//                val uri = Uri.parse("market://details?id=${appPackageName}")
//                val intent = Intent(Intent.ACTION_VIEW, uri)
//                startActivity(intent)
//            }


        }
    }


    private fun toggleLanguage() {
        val currentLocale = Locale.getDefault()
        val newLocale = if (currentLocale.language == "ar") {
            Locale.ENGLISH // التغيير من العربية إلى الإنجليزية
        } else {
            Locale("ar") // التغيير من الإنجليزية إلى العربية
        }

        // تطبيق تغيير اللغة المحددة للتطبيق
        val configuration = Configuration()
        configuration.setLocale(newLocale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }

    private fun setNightMode(isNightMode: Boolean) {
        if (isNightMode) {
            // قالب الوضع الليلي
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            // قالب الوضع النهاري
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        // إعادة إنشاء الـ Activity الحالي لتطبيق التغييرات
        recreate(requireActivity())
    }

    private fun setupViewPager() {
        // إعداد ViewPager2 مع أداة تحكم TabLayout
        binding.viewPager.adapter = ViewPagerAdapter(requireActivity())
        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "For you"
                1 -> tab.text = getString(R.string.following)
            }
        }.attach()
    }


    private fun setUpAppName() {
        val spannableString = SpannableString("Shyf")
        val colorSpan = ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.prim1))
        spannableString.setSpan(colorSpan, 2, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.title.text = spannableString
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}