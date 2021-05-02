package com.ljw.galleryapp

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import kotlin.concurrent.timer

class PhotoActivity : AppCompatActivity() {
    private val photoUrlList: MutableList<Uri> = mutableListOf()
    private var currentPosition: Int = 0
    private var timer: Timer? = null

    private val backgroundImageview: ImageView by lazy {
        findViewById<ImageView>(R.id.iv_background_image)
    }

    private val photoImageview: ImageView by lazy {
        findViewById<ImageView>(R.id.iv_photo_image)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo)

        getPhotoUriFromMainActivity()
    }

    override fun onStart() {
        super.onStart()

        setTimer()
    }

    override fun onStop() {
        super.onStop()

        timer?.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()

        timer?.cancel()
    }

    //////////////////////////////////////////////////////////////////////////////////////////

    private fun getPhotoUriFromMainActivity() {
        val size = intent.getIntExtra("photoListSize", 0)
        for (i in 0..size) {
            intent.getStringExtra("photo$i")?.let {
                photoUrlList.add(Uri.parse(it))
            }
        }
    }

    private fun setTimer() {
        timer = timer(period = 5 * 1000) {
            runOnUiThread {
                val current = currentPosition
                val next = if (photoUrlList.size <= current + 1) 0 else current + 1

                backgroundImageview.setImageURI(photoUrlList[current])

                photoImageview.alpha = 0f
                photoImageview.setImageURI(photoUrlList[next])
                photoImageview.animate().alpha(1.0f).setDuration(1000).start()

                currentPosition = next
            }
        }
    }
}
