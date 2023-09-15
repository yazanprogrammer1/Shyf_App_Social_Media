package com.example.shyf_.Service

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService

class MyJobIntentService : JobIntentService() {

    companion object {
        fun enqueueWork(context: Context, intent: Intent) {
            enqueueWork(context, MyJobIntentService::class.java, 123, intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.e("hzm", "onCreate")
    }

    override fun onHandleWork(intent: Intent) {
        Log.e("hzm", "onHandleWork")
        Log.e("hzm", Thread.currentThread().name)
//        val msg = intent!!.getStringExtra("msg")
//
//        for (i in 0..9) {
//            Log.e("hzm", "i = $i , $msg")
//            Thread.sleep(1000)
//        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("hzm", "onDestroy")
    }
}