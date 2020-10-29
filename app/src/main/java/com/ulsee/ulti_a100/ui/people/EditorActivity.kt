package com.ulsee.ulti_a100.ui.people

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.SystemClock
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ulsee.ulti_a100.databinding.ActivityPeopleEditorBinding
import com.ulsee.ulti_a100.utils.FilePickerHelper
import java.io.ByteArrayOutputStream
import java.io.File

private val TAG = EditorActivity::class.java.simpleName
private const val REQUEST_TAKE_PHOTO = 1235

class EditorActivity: AppCompatActivity() {
    private lateinit var binding: ActivityPeopleEditorBinding
    private lateinit var viewModel: EditorViewModel
    private lateinit var recyclerView: RecyclerView
    private val isEditingMode : Boolean
        get() {
            return intent.getBooleanExtra("is_edit_mode", true)
        }
    private lateinit var takePhotoIntentUri: Uri
    private var isPhotoTaken: Boolean = false
    private var mImageBase64: String = ""
    private var lastClickTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPeopleEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        viewModel = ViewModelProvider(this, EditorFactory(EditorRepository()))
            .get(EditorViewModel::class.java)

        recyclerView = binding.recyclerView
        recyclerView.adapter = EditorAdapter(this)
        recyclerView.layoutManager = LinearLayoutManager(this)
//        mProgressView = findViewById(R.id.progress_view)

        binding.addImage.setOnClickListener {
            pickImageFromTakePhoto()
        }

        binding.saveBtn.setOnClickListener { save() }

        if (isEditingMode) { showFaceImage() }

        observeResult()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        Log.d(TAG, "[Enter] onActivityResult")
        if (requestCode == REQUEST_TAKE_PHOTO) {
            if (resultCode == RESULT_OK) {
                val file = FilePickerHelper.shared().putPickedFile(this, takePhotoIntentUri)
                val bm = BitmapFactory.decodeFile(file.path)
                val bOut = ByteArrayOutputStream()
                bm.compress(Bitmap.CompressFormat.JPEG, 100, bOut)
                val imageBase64 = Base64.encodeToString(
                    bOut.toByteArray(),
                    Base64.NO_WRAP
                )
                mImageBase64 = imageBase64
                binding.addImage.setImageBitmap(bm)
                isPhotoTaken = true
            } else {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        AttributeType.clearAttributeData()
        super.onDestroy()
    }

    private fun observeResult() {
        viewModel.result.observe(this, {
            if (it == true) {
                setResult(RESULT_OK)
                finish()
            }
        })
    }

    private fun showFaceImage() {
        binding.textViewToolbarTitle.text = "Edit People"
        Glide.with(this).load(Base64.decode(AttributeType.faceImg, Base64.DEFAULT)).into(binding.addImage)
    }

    private fun pickImageFromTakePhoto () {
        val imageFileName = "take_photo"
        val storageDir = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        } else {
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        }

//        Log.d(TAG, "storageDir: $storageDir")

        val image = File.createTempFile(imageFileName, ".jpg", storageDir)
        //takePhotoIntentUri = Uri.fromFile(image);
        takePhotoIntentUri = FileProvider.getUriForFile(this, "$packageName.fileprovider", image)
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, takePhotoIntentUri)
        startActivityForResult(cameraIntent, REQUEST_TAKE_PHOTO)
    }

    private fun save() {
//        if (preventDoubleClickBtn()) return

        if (isInputValid()) {
            if (isEditingMode)
                editPeople()
            else
                addPeople()
        } else {
            Toast.makeText(this, "Please check your input information", Toast.LENGTH_LONG).show()
        }
    }

    private fun isInputValid(): Boolean {
        if (!isEditingMode) {
            if (!isPhotoTaken)
                return false
        }

        for (attribute in AttributeType.values()) {
//            Log.d(TAG, "index: ${attribute.ordinal} isInputValid: ${attribute.isInputValid}")
            if (!attribute.isInputValid)
                return false
        }

        return true
    }
    private fun addPeople () {
        val people = AttributeType.getAttributeData()
        people.setFaceImg(mImageBase64)
        viewModel.addPeople(people)
    }

    private fun editPeople () {
        val people = AttributeType.getAttributeData()

        if (mImageBase64.isNotEmpty()) {
            people.setFaceImg(mImageBase64)
        }
        viewModel.editPeople(people)
    }

    private fun preventDoubleClickBtn(): Boolean {
        if (SystemClock.elapsedRealtime() - lastClickTime < 1000) {
            return true
        }
        lastClickTime = SystemClock.elapsedRealtime()
        return false
    }

}