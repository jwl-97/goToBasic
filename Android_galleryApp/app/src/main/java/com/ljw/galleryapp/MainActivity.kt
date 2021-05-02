package com.ljw.galleryapp

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private val imageViewList: List<ImageView> by lazy {
        mutableListOf<ImageView>().apply {
            add(findViewById(R.id.iv_photo1))
            add(findViewById(R.id.iv_photo2))
            add(findViewById(R.id.iv_photo3))
            add(findViewById(R.id.iv_photo4))
            add(findViewById(R.id.iv_photo5))
            add(findViewById(R.id.iv_photo6))
        }
    }

    private val imageUrlList: MutableList<Uri> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    //////////////////////////////////////////////////////////////////////////////////////////

    private fun showPermissionPopup() {
        AlertDialog.Builder(this)
            .setTitle("권한 필요")
            .setMessage("앱에서 사진 권한이 필요합니다.")
            .setPositiveButton("확인") { _, _ -> requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), EXTERNAL_PERMISSION_REQUEST_CODE) }
            .setNegativeButton("취소") { _, _ -> }
            .show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            EXTERNAL_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED) {
                    getPhotosFromAlbum()
                } else {
                    Toast.makeText(this, "권한이 거부되었습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getPhotosFromAlbum() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_FROM_ALBUM)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return
        }

        when (requestCode) {
            PICK_FROM_ALBUM -> {
                if (imageUrlList.size >= 6) {
                    Toast.makeText(this, "이미 사진을 전부 채웠습니다.", Toast.LENGTH_SHORT).show()
                    return
                }

                val selectPhotoUri = data?.data

                selectPhotoUri?.let {
                    imageUrlList.add(it)
                    imageViewList[imageUrlList.size - 1].setImageURI(it)
                }
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_add_picture -> {
                when {
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PERMISSION_GRANTED -> getPhotosFromAlbum()

                    shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE) -> showPermissionPopup()

                    else -> requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), EXTERNAL_PERMISSION_REQUEST_CODE)
                }
            }

            R.id.btn_start_animation -> {
                val intent = Intent(this, PhotoActivity::class.java)
                imageUrlList.forEachIndexed { index, uri ->
                    intent.putExtra("photo$index", uri.toString())
                }

                intent.putExtra("photoListSize", imageUrlList.size)
                startActivity(intent)
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////

    companion object {
        private const val EXTERNAL_PERMISSION_REQUEST_CODE = 1000
        private const val PICK_FROM_ALBUM = 2000
    }
}
