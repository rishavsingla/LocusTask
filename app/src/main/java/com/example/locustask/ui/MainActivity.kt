package com.example.locustask.ui

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.locustask.R
import com.example.locustask.databinding.ActivityMainBinding
import com.example.locustask.models.LocusType
import com.example.locustask.ui.adapters.LocusAdapter
import com.example.locustask.viewmodel.MainActivityViewModel
import java.io.File

class MainActivity : AppCompatActivity(), LocusAdapter.ItemClickListener {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainActivityViewModel by viewModels()
    private val locusAdapter = LocusAdapter(this)
    private var itemPosition = -1
    private var photoFilePath = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.recyclerView.apply {
            adapter = locusAdapter
        }

        viewModel.readJsonResponseFromFile()

        viewModel.getResponseData().observe(this) {
            binding.recyclerView.post {
                locusAdapter.submitList(it)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_submit, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.submit) {
            logListItemsIds()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun logListItemsIds() {
        val list = viewModel.getResponseData().value ?: return
        for (item in list) {
            Log.d("item id : ", item.id)
            if (item.type == LocusType.COMMENT.name) {
                Log.d("user comment : ", item.title)
            }
        }
    }

    override fun onPhotoItemClick(position: Int) {
        launchCamera(position)
    }


    private fun launchCamera(position: Int) {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile = getPhotoFileUri("" + System.currentTimeMillis() + ".jpg")
        val uriForFile =
            FileProvider.getUriForFile(this, "com.example.locustask.fileprovider", photoFile)
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriForFile)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            itemPosition = position
            photoFilePath = photoFile.absolutePath
            cameraResultLauncher.launch(takePictureIntent)
        }
    }

    private fun getPhotoFileUri(fileName: String): File {
        val mediaStorageDir = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "LocusTask")
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs()
        }
        return File(mediaStorageDir.path + File.separator + fileName)
    }

    private val cameraResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it != null && it.resultCode == RESULT_OK) {
                viewModel.setFilePath(itemPosition, photoFilePath)
            }
        }

    override fun onRemovePhoto(position: Int) {
        viewModel.removePhoto(position)
    }

    override fun openPhoto(path: String?) {
        Intent(this, ImageViewActivity::class.java).apply {
            putExtra(ImageViewActivity.EXTRA_IMAGE_PATH, path)
        }.run {
            startActivity(this)
        }
    }

    override fun onOptionChoice(checkedId: Int, itemPosition: Int) {
        viewModel.setOptionsChoice(checkedId, itemPosition)
    }
}