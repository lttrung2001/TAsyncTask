package com.example.tasynctask.components

import android.os.Handler
import android.os.Looper
import java.lang.Exception

abstract class TAsyncTask<Params, Progress, Result> {
    private val t = TThread().apply {
        start()
    }
    private lateinit var mainHandler: Handler

    abstract fun doInBackground(vararg params: Params): Result
    open fun publishProgress(progress: Progress, result: Result? = null) {
        mainHandler.post { onProgressUpdate(progress, result) }
    }
    open fun cancel() {
        t.handler.looper.quit()
        mainHandler.post { onCancelled() }
    }
    open fun onPreExecute() {}
    open fun onProgressUpdate(progress: Progress, result: Result?) {}
    open fun onDone(result: Result) {}
    open fun onCancelled() {}
    fun execute(vararg params: Params): TAsyncTask<Params, Progress, Result> {
        params
        try {
            t.handler.post {
                val result = doInBackground(*params)
                t.handler.looper.quit()
                mainHandler.post { onDone(result) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return this
    }

    inner class TThread : Thread() {
        lateinit var handler: Handler
        override fun run() {
            super.run()
            mainHandler = Handler(Looper.getMainLooper())

            Looper.prepare()
            handler = Handler(Looper.myLooper()!!)
            Looper.loop()
        }
    }
}