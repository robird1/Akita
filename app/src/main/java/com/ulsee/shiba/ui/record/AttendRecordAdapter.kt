package com.ulsee.shiba.ui.record

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ulsee.shiba.R
import com.ulsee.shiba.data.response.AttendRecord
import com.ulsee.shiba.utils.ImageTemp

private val TAG = AttendRecordAdapter::class.java.simpleName

class AttendRecordAdapter : PagingDataAdapter<AttendRecord, RecyclerView.ViewHolder>(REPO_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AttendRecordViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        Log.d(TAG, "[Enter] onBindViewHolder  position: $position")
        val data = getItem(position)
        if (data != null) {
            (holder as AttendRecordViewHolder).bind(data, temperatureUnit)
        }
    }

    fun setTemperatureUnit(unit: String) {
        temperatureUnit = unit
    }

    fun getTemperatureUnit() = temperatureUnit

    fun setLastTemperatureUnit(unit: String) {
        lastTemperatureUnit = unit
    }

    companion object {
        private var temperatureUnit = ""
        private var lastTemperatureUnit = ""

        private val REPO_COMPARATOR = object : DiffUtil.ItemCallback<AttendRecord>() {
            override fun areItemsTheSame(oldItem: AttendRecord, newItem: AttendRecord): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: AttendRecord, newItem: AttendRecord): Boolean {
                val areTheSame =
                    oldItem == newItem && temperatureUnit == lastTemperatureUnit

//                Log.d(TAG, "[Enter] areItemsTheSame() areTheSame: $areTheSame oldItem.id: ${oldItem.id} newItem.id: ${newItem.id}")
                return areTheSame
            }
        }
    }


    class AttendRecordViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val faceView = view.findViewById<ImageView>(R.id.face_img)
        private val textName = view.findViewById<TextView>(R.id.textView_peopleName)
        private val textTemperature = view.findViewById<TextView>(R.id.textView_temperature)
        private val textDate = view.findViewById<TextView>(R.id.textView_date)
        private var data: AttendRecord? = null
        private var temperatureUnit = ""

        init {
            view.setOnClickListener {
                data?.let { it ->
                    val bundle = Bundle()
//                bundle.putString("img", it.img)
                    ImageTemp.imgBase64 = it.img
                    bundle.putString("name", it.name)
                    bundle.putInt("mask", it.respirator)
                    bundle.putString("gender", it.gender)
                    bundle.putString("country", it.country)
                    bundle.putString("date", it.timestamp)
                    bundle.putString("temperature", getTransformedTemperature(it.bodyTemperature))
                    val intent = Intent(view.context, RecordInfoActivity::class.java)
                    intent.putExtra("bundle", bundle)
                    view.context.startActivity(intent)
                }
            }
        }

        fun bind(data: AttendRecord?, unit: String) {
            if (data != null) {
                this.data = data
                this.temperatureUnit = unit
                textName.text = data.name
                textTemperature.text = getTransformedTemperature(data.bodyTemperature)
                textDate.text = data.timestamp.split(".")[0]
                data.img?.let {
                    val imgBase64 = it.split("data:image/jpeg;base64,")[1]
                    Glide.with(itemView.context).load(Base64.decode(imgBase64, Base64.DEFAULT))
                        .into(faceView)
                }
            }
        }

        /**
         * The body temperature from backend is always in Celcius format no matter what unit user selects.
         */
        private fun getTransformedTemperature(value: String): String {
            return if (value.isNotEmpty()) {
                if (temperatureUnit == "F") {
                    String.format("%.1f", celciusToFahrenheit(value.toFloat())) + " °F"
                } else {
                    String.format("%.1f", value.toFloat()) + " °C"
                }
            } else {
                ""
            }
        }

        private fun celciusToFahrenheit(celsius: Float): Float {
            return celsius * 9 / 5 + 32
        }

        companion object {
            fun create(parent: ViewGroup): AttendRecordViewHolder {
                val view =
                    LayoutInflater.from(parent.context).inflate(R.layout.item_list_attend_record, parent, false)
                return AttendRecordViewHolder(view)
            }
        }
    }
}
