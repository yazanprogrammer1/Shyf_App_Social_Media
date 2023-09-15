package com.example.shyf_.pagination

import androidx.paging.PageKeyedDataSource
import com.example.shyf_.apis.ApiService
import com.example.shyf_.model.Posts

// خطوة 1: إعداد DataSource
class PostsDataSource(private val apiService: ApiService) : PageKeyedDataSource<Int, Posts>() {
    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Posts>
    ) {
        // قم باستدعاء الواجهة لجلب البيانات الأولى هنا واستخدم callback لتحميلها.
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Posts>) {
        // لاحقًا إذا كنت بحاجة إلى دعم التمرير للخلف.
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Posts>) {
        // قم بالاستدعاء لجلب الصفحة التالية من البيانات هنا واستخدم callback لتحميلها.
    }
}