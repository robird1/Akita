package com.ulsee.shiba.ui.record

import android.os.Bundle
import android.util.Base64
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.ulsee.shiba.databinding.ActivityRecordInfoBinding
import com.ulsee.shiba.utils.ImageTemp

class RecordInfoActivity: AppCompatActivity() {
    private lateinit var binding: ActivityRecordInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val data = intent.getBundleExtra("bundle")
        data?.let {
//            showName(it)
            showTemperature(it)
//            showAge(it)
            showMask(it)
//            showGender(it)
            showDate(it)
            showFaceImg()
        }
    }

    private fun showFaceImg() {
        val temp = ImageTemp.imgBase64.split("data:image/jpeg;base64,")
        if (temp.size == 2) {
            val imgBase64 = temp[1]
            Glide.with(this).load(Base64.decode(imgBase64, Base64.DEFAULT))
                .into(binding.recordInfoFace)
        }
    }

//    private fun showGender(it: Bundle) {
//        val genderValue = it.getString("gender", "")
//        if (genderValue == "0") {
//            binding.recordInfoGender.text = "male"
//        } else {
//            binding.recordInfoGender.text = "female"
//        }
//    }
//
//    private fun showAge(it: Bundle) {
//        val ageValue = it.getInt("age", 0)
//        if (ageValue == 0) {
//            binding.recordInfoAge.text = ""
//        } else {
//            binding.recordInfoAge.text = ageValue.toString()
//        }
//    }

    private fun showMask(it: Bundle) {
        val maskValue = it.getInt("mask", 0)
        if (maskValue == 0) {
            binding.recordInfoMask.text = "No"
        } else {
            binding.recordInfoMask.text = "Yes"
        }
    }

    private fun showTemperature(it: Bundle) {
        binding.recordInfoTemperature.text = it.getString("temperature", "")
    }

    private fun showDate(it: Bundle) {
        binding.recordInfoDate.text = it.getString("date", "").split(".")[0]
    }

//    private fun showName(it: Bundle) {
//        binding.recordInfoName.text = it.getString("name", "")
//    }

}