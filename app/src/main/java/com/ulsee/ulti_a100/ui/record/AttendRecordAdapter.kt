package com.ulsee.ulti_a100.ui.record

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
import com.ulsee.ulti_a100.R
import com.ulsee.ulti_a100.data.response.AttendRecord

private val TAG = AttendRecordAdapter::class.java.simpleName

class AttendRecordAdapter : PagingDataAdapter<AttendRecord, RecyclerView.ViewHolder>(REPO_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AttendRecordViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            (holder as AttendRecordViewHolder).bind(data)
        }
    }

    companion object {
        private val REPO_COMPARATOR = object : DiffUtil.ItemCallback<AttendRecord>() {
            override fun areItemsTheSame(oldItem: AttendRecord, newItem: AttendRecord): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: AttendRecord, newItem: AttendRecord): Boolean =
                oldItem == newItem
        }
    }
}


class AttendRecordViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
    private val faceView = view.findViewById<ImageView>(R.id.face_img)
    private val textName = view.findViewById<TextView>(R.id.textView_peopleName)
    private val textTemprature = view.findViewById<TextView>(R.id.textView_temperature)
    private val textDate = view.findViewById<TextView>(R.id.textView_date)
    private var data: AttendRecord? = null

    init {
        view.setOnClickListener {
            data?.let { it ->
                val bundle = Bundle()
                bundle.putString("img", it.img)
                bundle.putString("name", it.name)
                bundle.putInt("age", it.age)
                bundle.putString("gender", it.gender)
                bundle.putString("country", it.country)
                bundle.putString("date", it.timestamp)
                val intent = Intent(view.context, RecordInfoActivity::class.java)
                intent.putExtra("bundle", bundle)
                view.context.startActivity(intent)
            }
        }
    }

    fun bind(data: AttendRecord?) {
        if (data != null) {
            this.data = data
            textName.text = data.name
            textTemprature.text = data.bodyTemperature
            textDate.text = data.timestamp
            data.img?.let {
                val imgBase64 = it.split("data:image/jpeg;base64,")[1]
                Glide.with(itemView.context).load(Base64.decode(imgBase64, Base64.DEFAULT)).into(faceView)
            }
        }
    }

    companion object {
        fun create(parent: ViewGroup): AttendRecordViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_list_attend_record, parent, false)
            return AttendRecordViewHolder(view)
        }
    }
}