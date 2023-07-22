package com.example.tasynctask

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.tasynctask.components.TAsyncTask
import com.example.tasynctask.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import java.net.URL


class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val myAsyncTask = object : TAsyncTask<String, Int, Bitmap?>() {
            override fun doInBackground(vararg params: String): Bitmap? {
                for (i in 0 .. 100) {
                    publishProgress(i)
                    Thread.sleep(10)
                }
                return try {
                    for (i in params.indices) {
                        val `in` = URL(params[i]).openStream()
                        publishProgress(
                            ((i + 1.0) * 100 / params.size).toInt(),
                            BitmapFactory.decodeStream(`in`)
                        )
                        Thread.sleep(2000)
                    }
                    BitmapFactory.decodeResource(resources, R.drawable.ic_launcher_background)
                } catch (e: Exception) {
                    Log.e("Error", e.message!!)
                    e.printStackTrace()
                    null
                }
            }

            override fun onProgressUpdate(progress: Int, result: Bitmap?) {
                binding.progress.progress = progress
                binding.img.setImageBitmap(result)
            }

            override fun onDone(result: Bitmap?) {
                Snackbar.make(binding.root, "Load image done!", Snackbar.LENGTH_LONG).show()
                binding.img.setImageBitmap(result)
            }
        }


        binding.btn.setOnClickListener {
            myAsyncTask.execute(
                "https://upload.wikimedia.org/wikipedia/commons/b/b6/Image_created_with_a_mobile_phone.png",
                "https://d1hjkbq40fs2x4.cloudfront.net/2017-08-21/files/landscape-photography_1645.jpg"
            )
        }
    }
}