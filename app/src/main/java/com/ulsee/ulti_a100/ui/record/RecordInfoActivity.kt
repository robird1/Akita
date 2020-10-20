package com.ulsee.ulti_a100.ui.record

import android.os.Bundle
import android.util.Base64
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.ulsee.ulti_a100.databinding.ActivityRecordInfoBinding

class RecordInfoActivity: AppCompatActivity() {
    private lateinit var binding: ActivityRecordInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val data = intent.getBundleExtra("bundle")
        data?.let {
            binding.recordInfoName.text = it.getString("name", "")

            val ageValue = it.getInt("age", -1)
            if (ageValue == -1) {
                binding.recordInfoAge.text = ""
            } else {
                binding.recordInfoAge.text = ageValue.toString()
            }

            val genderValue = it.getString("gender", "")
            if (genderValue == "0") {
                binding.recordInfoGender.text = "male"
            } else {
                binding.recordInfoGender.text = "female"
            }

            binding.recordInfoCountry.text = it.getString("country", "")
            binding.recordInfoDate.text = it.getString("date", "")

            val imgBase64 = it.getString("img", "").split("data:image/jpeg;base64,")[1]
            Glide.with(this).load(Base64.decode(imgBase64, Base64.DEFAULT)).into(binding.recordInfoFace)
        }
    }
}