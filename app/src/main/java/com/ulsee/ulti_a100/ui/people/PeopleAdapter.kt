package com.ulsee.ulti_a100.ui.people

import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ulsee.ulti_a100.R
import com.ulsee.ulti_a100.data.response.AllPerson

private val TAG = PeopleAdapter::class.java.simpleName

class PeopleAdapter(private val viewModel: PeopleViewModel): RecyclerView.Adapter<PeopleAdapter.ViewHolder>() {

    private val faceImageCacheMap = hashMapOf<String, String>()
    private var mCallback: OnRecyclerItemCallbackListener? = null

    interface OnRecyclerItemCallbackListener {
        fun onRecyclerItemClick(position: Int, data: AllPerson)
//        fun onRecyclerItemLongClick(position: Int)
    }

    fun setOnRecyclerItemCallbackListener(l: OnRecyclerItemCallbackListener) {
        mCallback = l
    }

    var peopleList: MutableList<AllPerson> = ArrayList()
    fun setList(list: List<AllPerson>) {
        checkFaceImageCache(list)
//        peopleList = list
        peopleList.clear()
        peopleList.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = this.peopleList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(peopleList[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_list_people2, parent, false)
        return ViewHolder(view)
    }

    fun removeItem(position: Int) {
        faceImageCacheMap.remove(peopleList[position].personId)
        peopleList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun editItem(position: Int, item: AllPerson) {
        peopleList[position] = item
        notifyItemChanged(position)
    }

    fun addItem(item: AllPerson) {
        val position = peopleList.size
        peopleList[position] = item
        notifyItemInserted(position)
    }

    /**
     * To remove the certain face image from the cache if the new dataset of the adapter doesn't
     * contain the work ID of face image in cache after reloading the list.
     */
    private fun checkFaceImageCache(list: List<AllPerson>) {
        val personIdSet = mutableSetOf<String>()
        for (i in list) {
            personIdSet.add(i.personId)
        }
        for (i in faceImageCacheMap) {
            if (!personIdSet.contains(i.key)) {
                faceImageCacheMap.remove(i.key)
            }
        }
    }


    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val nameTV = itemView.findViewById<TextView>(R.id.textView_language)
        private val workIdTV = itemView.findViewById<TextView>(R.id.textView_workID)
        private val faceView = itemView.findViewById<ImageView>(R.id.face_img)
//        private val markView = itemView.findViewById<ImageView>(R.id.face_select)
        val viewForeground: ConstraintLayout = itemView.findViewById(R.id.view_foreground)
        val deleteIconRight: ImageView = itemView.findViewById(R.id.delete_icon_right)
        private var data: AllPerson? = null
//        private var onClickData: People2? = null

        init {
            observeFaceRequest(itemView)

            itemView.setOnClickListener {
//                data?.let { it1 -> fragment.openEditor(it1, true) }
                data?.let { it1 -> mCallback?.onRecyclerItemClick(absoluteAdapterPosition, it1) }
//            onClickData?.let { it -> mCallback?.onRecyclerItemClick(absoluteAdapterPosition, it) }

            }
//        itemView.setOnLongClickListener {
//            mCallback?.onRecyclerItemLongClick(absoluteAdapterPosition)
//            return@setOnLongClickListener true
//        }
        }

        private fun observeFaceRequest(itemView: View) {
            val lifecycleOwner = itemView.context as LifecycleOwner
            viewModel.queryFaceResponse.observe(lifecycleOwner, {
                Log.d(TAG, "[Enter] viewModel.base64Face.observe")
                if (data?.personId == it.personId) {
                    val imgBase64 = it.imgBase64.split("data:image/jpeg;base64,")[1]
                    Glide.with(itemView.context).load(Base64.decode(imgBase64, Base64.DEFAULT)).into(faceView)
                    faceImageCacheMap[it.personId] = it.imgBase64
//                    onClickData.faceImg = it.imgBase64
                    data?.faceImg = it.imgBase64
                }
            })
        }

        fun bind(people: AllPerson) {
            if (people != null) {
                data = people
//                configOnClickData(people)
                nameTV.text = people.name
                workIdTV.text = people.personId
//                markView.visibility = if (people.checked) View.VISIBLE else View.INVISIBLE

                val cachedImgBase64 = faceImageCacheMap[people.personId]
                if (cachedImgBase64 == null) {
                    clearFaceView()
                    viewModel.queryPerson(people.personId)
                } else {
                    displayFaceView(cachedImgBase64)
                    data?.faceImg = cachedImgBase64
                }
            }

        }

//        private fun configOnClickData(p: AllPerson) {
//            onClickData = People2("", p.name, p.personId, p.gender, p.email, p.age.toString(), p.phone, p.address)
//        }

        private fun displayFaceView(imgBase64: String) {
            val tmp = imgBase64.split("data:image/jpeg;base64,")[1]
            Glide.with(itemView.context).load(Base64.decode(tmp, Base64.DEFAULT)).into(faceView)
        }

        private fun clearFaceView() {
            Glide.with(faceView).clear(faceView)
        }

    }

}


//data class People2(var faceImg: String, val name: String, val personId: String, val gender:String,
//                   val email: String, val age: String, val phone: String, val address: String)





