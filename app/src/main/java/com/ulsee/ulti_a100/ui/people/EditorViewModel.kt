package com.ulsee.ulti_a100.ui.people

import android.util.Log
import androidx.lifecycle.*
import com.ulsee.ulti_a100.data.response.AddPerson
import com.ulsee.ulti_a100.data.response.ModifyPerson
import com.ulsee.ulti_a100.model.People
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

private val TAG = EditorViewModel::class.java.simpleName
const val ERROR_CODE_WORK_ID_EXISTS = 2000
const val ERROR_CODE_ADD_FAILED = 2001

class EditorViewModel(private val repository: EditorRepository) : ViewModel() {
    private var _addResult = MutableLiveData<Boolean>()
    val addResult : LiveData<Boolean>
        get() = _addResult
    private var _editResult = MutableLiveData<Boolean>()
    val editResult : LiveData<Boolean>
        get() = _editResult
    private var _addPeopleErrorCode = -1
    val addPeopleErrorCode: Int
        get() = _addPeopleErrorCode


    fun addPeople(people: People) {
        viewModelScope.launch {
            try {
                val response = repository.requestAddPerson(createAddRequestBody(people))
                val isSuccess = isAddSuccess(response)
                if (!isSuccess) {
                    val isWorkIdExist = response.detail == "arstack error, 548(Workid is already exist)"
                    if (isWorkIdExist) {
//                        Log.d(TAG, "[Enter] work id exists")
                        _addPeopleErrorCode = ERROR_CODE_WORK_ID_EXISTS
                    }
                }
                _addResult.value = isSuccess

            } catch (e: Exception) {
                _addResult.value = false
                Log.d(TAG, "e.message: ${e.message}")
            }
        }
    }

    fun editPeople(people: People) {
        viewModelScope.launch {
            try {
                val response = repository.requestModifyPerson(createEditRequestBody(people))
                _editResult.value = isModifySuccess(response)

            } catch (e: Exception) {
                _editResult.value = false
                Log.d(TAG, "e.message: ${e.message}")
            }
        }
    }

    fun resetErrorCode() {
        _addPeopleErrorCode = -1
    }

    private fun isAddSuccess(response: AddPerson) = response.status == 0 && response.detail == "success"
    private fun isModifySuccess(response: ModifyPerson) = response.status == 0 && response.detail == "success"

    private fun createAddRequestBody(p: People): RequestBody {
        val imgBase64 = "data:image/jpeg;base64,"+ p.getFaceImg()
        val tmp = if (p.getAge().isNotEmpty()) {

            "{\r\n    \"personId\": \"${p.getWorkID()}\",\r\n    \"userId\": \"${p.getWorkID()}\",\r\n    " +
                    "\"name\": \"${p.getName()}\",\r\n    \"age\": \"${p.getAge()}\",\r\n    \"gender\": \"${p.getGender()}\",\r\n    " +
                    "\"phone\": \"${p.getPhone()}\",\r\n    \"email\": \"${p.getMail()}\",\r\n    \"address\": \"${p.getAddress()}\",\r\n    \"images\": [\r\n        " +
                    "{\r\n            \"data\": \"$imgBase64\"\r\n        }\r\n    ]\r\n}"

        } else {

            "{\r\n    \"personId\": \"${p.getWorkID()}\",\r\n    \"userId\": \"${p.getWorkID()}\",\r\n    " +
                    "\"name\": \"${p.getName()}\",\r\n    \"gender\": \"${p.getGender()}\",\r\n    " +
                    "\"phone\": \"${p.getPhone()}\",\r\n    \"email\": \"${p.getMail()}\",\r\n    \"address\": \"${p.getAddress()}\",\r\n    \"images\": [\r\n        " +
                    "{\r\n            \"data\": \"$imgBase64\"\r\n        }\r\n    ]\r\n}"
        }
//        Log.d(TAG, "tmp: $tmp")
        return tmp.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
    }

    private fun createEditRequestBody(p: People): RequestBody {
        val tmp = if (p.getAge().isNotEmpty()) {

            "{\r\n    \"personId\": \"${p.getWorkID()}\",\r\n    \"userId\": \"${p.getWorkID()}\",\r\n    \"name\": \"${p.getName()}\",\r\n" +
                    "    \"age\": ${p.getAge()},\r\n    \"gender\": \"${p.getGender()}\",\r\n    \"phone\": \"${p.getPhone()}\",\r\n" +
                    "    \"email\": \"${p.getMail()}\",\r\n    \"address\": \"${p.getAddress()}\"\r\n}\r\n"

        } else {

            "{\r\n    \"personId\": \"${p.getWorkID()}\",\r\n    \"userId\": \"${p.getWorkID()}\",\r\n    \"name\": \"${p.getName()}\",\r\n" +
                    "    \"gender\": \"${p.getGender()}\",\r\n    \"phone\": \"${p.getPhone()}\",\r\n" +
                    "    \"email\": \"${p.getMail()}\",\r\n    \"address\": \"${p.getAddress()}\"\r\n}\r\n"
        }
        Log.d(TAG, "tmp: $tmp")
        return tmp.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

    }

}


class EditorFactory(private val repository: EditorRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EditorViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}