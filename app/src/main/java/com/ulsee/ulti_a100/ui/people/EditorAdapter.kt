package com.ulsee.ulti_a100.ui.people

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputLayout
import com.ulsee.ulti_a100.R


private val TAG = EditorAdapter::class.java.simpleName

class EditorAdapter(private val context: Context, private val isEditingMode: Boolean): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        Log.d(TAG, "[Enter] onCreateViewHolder")
        val holder: RecyclerView.ViewHolder
        val layoutInflater = LayoutInflater.from(parent.context)
        holder = when (viewType) {
            AttributeType.GENDER.viewType -> {
                GenderViewHolder(
                    layoutInflater.inflate(
                        AttributeType.GENDER.viewType,
                        parent,
                        false
                    )
                )
            }
            else -> {
                TextViewHolder(layoutInflater.inflate(AttributeType.NAME.viewType, parent, false))
            }
        }
        return holder
    }

    override fun getItemCount(): Int = AttributeType.values().size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        Log.d(TAG, "[Enter] onBindViewHolder position: $position")
        if (position == AttributeType.GENDER.position) {
            (holder as GenderViewHolder).bind(position)
        } else {
            (holder as TextViewHolder).bind(position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return AttributeType.fromPosition(position).viewType
    }


    inner class TextViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textInputLayout: TextInputLayout = itemView.findViewById(R.id.textInputLayout_name)
        private var mPosition = -1

        init {
            addTextChangedListener()
        }

        fun bind(position: Int) {
            mPosition = position
            textInputLayout.hint = AttributeType.fromPosition(position).fieldName
            textInputLayout.helperText = AttributeType.fromPosition(position).helperText
            textInputLayout.editText?.setText(AttributeType.fromPosition(position).inputValue)
            if (isEditingMode && position == AttributeType.WORK_ID.position) {
                textInputLayout.editText?.isEnabled = false
            }
        }

        private fun addTextChangedListener() {
            textInputLayout.editText?.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
                override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
                override fun afterTextChanged(editable: Editable) {
//                    Log.d(TAG, "[Enter] afterTextChanged position: $mPosition text: ${editable.toString()}")
                    AttributeType.fromPosition(mPosition).inputValue = editable.toString()

                    if (mPosition == AttributeType.NAME.position || mPosition == AttributeType.WORK_ID.position || mPosition == AttributeType.MAIL.position) {
                        AttributeType.fromPosition(mPosition).isInputValid =
                            editable.toString().isNotEmpty()

                    } else if (mPosition == AttributeType.AGE.position) {
                        if (!isAgeValid()) {
                            textInputLayout.error = "Error: Invalid Age"
                            AttributeType.AGE.isInputValid = false

                        } else {
                            textInputLayout.isErrorEnabled = false
                            AttributeType.AGE.isInputValid = true
                        }

                    } else {
                        AttributeType.fromPosition(mPosition).isInputValid = true
                    }
                }
            })
        }

        private fun isAgeValid(): Boolean {
            val inputString = textInputLayout.editText?.text.toString().trim()
            if (inputString.isEmpty())
                return true

            try {
                inputString.toInt()
            } catch (e: Exception) {
                return false
            }
            return inputString.toInt() in 1..115
        }

    }


    inner class GenderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textInputLayout =
            itemView.findViewById<TextInputLayout>(R.id.textInputLayout4_gender)
        private var mPosition = -1

        init {
            addTextChangedListener()
        }

        fun bind(position: Int) {
            mPosition = position
            textInputLayout.helperText = "*Required"
            textInputLayout.editText?.setText(AttributeType.GENDER.inputValue)
            setGenderAdapter()
        }

        private fun addTextChangedListener() {
            textInputLayout.editText?.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
                override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
                override fun afterTextChanged(editable: Editable) {
//                    Log.d(TAG, "[Enter] afterTextChanged position: $mPosition text: ${editable.toString()}")

                    AttributeType.GENDER.inputValue = editable.toString()
                    AttributeType.GENDER.isInputValid = true
                }
            })
        }

        private fun setGenderAdapter() {
            val items = listOf("male", "female")
            val adapter = ArrayAdapter(context, R.layout.list_item_gender, items)
            (textInputLayout.editText as? AutoCompleteTextView)?.setAdapter(adapter)
        }
    }
}
