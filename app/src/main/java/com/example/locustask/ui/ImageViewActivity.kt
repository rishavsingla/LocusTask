package com.example.locustask.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.locustask.databinding.ActivityImageViewBinding

class ImageViewActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_IMAGE_PATH = "image_path"
    }

    private lateinit var binding: ActivityImageViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imagePath = intent.getStringExtra(EXTRA_IMAGE_PATH)

        Glide.with(this).load(imagePath).into(binding.ivPhoto)
    }
}