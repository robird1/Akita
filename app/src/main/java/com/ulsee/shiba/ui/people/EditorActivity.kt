package com.ulsee.shiba.ui.people

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.SystemClock
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ulsee.shiba.R
import com.ulsee.shiba.databinding.ActivityPeopleEditorBinding
import com.ulsee.shiba.utils.FilePickerHelper
import java.io.ByteArrayOutputStream
import java.io.File

private val TAG = EditorActivity::class.java.simpleName
private const val REQUEST_TAKE_PHOTO = 1235
private const val REQUEST_DEVICE_SYNC = 5678

class EditorActivity: AppCompatActivity() {
    private lateinit var binding: ActivityPeopleEditorBinding
    private lateinit var viewModel: EditorViewModel
    private lateinit var recyclerView: RecyclerView
    private val isEditingMode : Boolean
        get() = intent.getBooleanExtra("is_edit_mode", true)
    private val url: String?
        get() = intent.getStringExtra("url")
    private lateinit var takePhotoIntentUri: Uri
    private var isPhotoTaken: Boolean = false
    private var mImageBase64: String = ""
    private var lastClickTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPeopleEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        initViewModel()
        initRecyclerView()

        binding.addImage.setOnClickListener {
            pickImageFromTakePhoto()
        }
        binding.addBtn.setOnClickListener { addOrEdit() }

        if (isEditingMode) { showFaceImage() }

        observeAddResult()
        observeEditResult()
    }

    private fun initRecyclerView() {
        recyclerView = binding.recyclerView
        recyclerView.adapter = EditorAdapter(this, isEditingMode)
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this, EditorFactory(PeopleRepository(url!!)))
            .get(EditorViewModel::class.java)
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
        } else if (requestCode == REQUEST_DEVICE_SYNC) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK)
                finish()
            } else {
                binding.progressView.visibility = View.INVISIBLE
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        AttributeType.clearAttributeData()
        super.onDestroy()
    }

    private fun observeAddResult() {
        viewModel.addResult.observe(this, {
            binding.progressView.visibility = View.INVISIBLE
            if (it == true) {
                Toast.makeText(this, getString(R.string.create_successfully), Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK, Intent().putExtra("mode", "add"))
                finish()
            } else {
                when (viewModel.errorCode) {
                    ERROR_CODE_WORK_ID_EXISTS -> {
                        Toast.makeText(this, "The input work ID already exists", Toast.LENGTH_SHORT).show()
                    }
                    ERROR_CODE_API_NOT_SUCCESS -> {
                        Toast.makeText(this, "Error($ERROR_CODE_API_NOT_SUCCESS)", Toast.LENGTH_SHORT).show()
                    }
                    else ->  {
                        Toast.makeText(this, "Error($ERROR_CODE_EXCEPTION)", Toast.LENGTH_SHORT).show()
                    }
                }
                viewModel.resetErrorCode()
            }
        })
    }

    private fun observeEditResult() {
        viewModel.editResult.observe(this, {
            binding.progressView.visibility = View.INVISIBLE
            if (it == true) {
                Toast.makeText(this, getString(R.string.update_successfully), Toast.LENGTH_SHORT).show()
                val intent = Intent().putExtra("mode", "edit").putExtra("is_face_changed", isPhotoTaken)
                setResult(RESULT_OK, intent)
                finish()
            } else {
                when (viewModel.errorCode) {
                    ERROR_CODE_API_NOT_SUCCESS -> {
                        Toast.makeText(this, "Error($ERROR_CODE_API_NOT_SUCCESS)", Toast.LENGTH_SHORT).show()
                    }
                    else ->  {
                        Toast.makeText(this, "Error($ERROR_CODE_EXCEPTION)", Toast.LENGTH_SHORT).show()
                    }
                }
                viewModel.resetErrorCode()
            }
        })
    }

    // TODO refactor
    private fun showFaceImage() {
        binding.textViewToolbarTitle.text = "Edit People"

        // clear the default add icon
        binding.addImage.setImageResource(0)

        val tmp = AttributeType.faceImg.split("data:image/jpeg;base64,")
        if (tmp.size == 2) {
            Glide.with(this).load(Base64.decode(tmp[1], Base64.DEFAULT)).into(binding.addImage)
        }
    }

    private fun pickImageFromTakePhoto () {
        val imageFileName = "take_photo"
        val storageDir = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        } else {
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        }
        val image = File.createTempFile(imageFileName, ".jpg", storageDir)
        //takePhotoIntentUri = Uri.fromFile(image);
        takePhotoIntentUri = FileProvider.getUriForFile(this, "$packageName.fileprovider", image)
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, takePhotoIntentUri)
        startActivityForResult(cameraIntent, REQUEST_TAKE_PHOTO)
    }

    private fun addOrEdit() {
//        if (preventDoubleClickBtn()) return
        if (isInputValid()) {
            binding.progressView.visibility = View.VISIBLE
            if (isEditingMode)
                editPeople()
            else
                showSyncDialog()
//                addPeople()
        } else {
            Toast.makeText(this, "Please check your input information", Toast.LENGTH_LONG).show()
        }
    }

    private fun showSyncDialog() {
        AlertDialog.Builder(this)
            .setMessage("Add to multiple devices?")
            .setPositiveButton(getString(R.string.yes))
            { _, _ ->

                val intent = Intent(this, DeviceSyncActivity::class.java)
                intent.putExtra("url", url)
                AttributeType.faceImg = mImageBase64
                startActivityForResult(intent, REQUEST_DEVICE_SYNC)
            }
            .setNegativeButton(getString(R.string.no)
            ) { dialog, _ ->
                dialog.dismiss()
                addPeople()
            }
            .setCancelable(false)
            .create()
            .show()
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
        viewModel.editPeople(people, isPhotoTaken)
    }

    private fun preventDoubleClickBtn(): Boolean {
        if (SystemClock.elapsedRealtime() - lastClickTime < 1000) {
            return true
        }
        lastClickTime = SystemClock.elapsedRealtime()
        return false
    }

}