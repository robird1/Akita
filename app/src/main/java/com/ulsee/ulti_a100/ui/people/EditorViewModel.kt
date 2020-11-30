package com.ulsee.ulti_a100.ui.people

import android.util.Log
import androidx.lifecycle.*
import com.ulsee.ulti_a100.data.response.AddFaces
import com.ulsee.ulti_a100.data.response.AddPerson
import com.ulsee.ulti_a100.data.response.DeleteFaces
import com.ulsee.ulti_a100.data.response.ModifyPerson
import com.ulsee.ulti_a100.model.People
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

private val TAG = EditorViewModel::class.java.simpleName
const val ERROR_CODE_WORK_ID_EXISTS = 2000
const val ERROR_CODE_API_NOT_SUCCESS = 2001
const val ERROR_CODE_EXCEPTION = 2002

class EditorViewModel(private val repository: PeopleRepository) : ViewModel() {
    private var _addResult = MutableLiveData<Boolean>()
    val addResult : LiveData<Boolean>
        get() = _addResult
    private var _editResult = MutableLiveData<Boolean>()
    val editResult : LiveData<Boolean>
        get() = _editResult
    private var _errorCode = -1
    val errorCode: Int
        get() = _errorCode


    fun addPeople(people: People) {
        viewModelScope.launch {
            try {
                val response = repository.requestAddPerson(people)
                val isSuccess = isAddSuccess(response)
                if (!isSuccess) {
                    val isWorkIdExist = response.detail == "arstack error, 548(Workid is already exist)"
                    _errorCode = if (isWorkIdExist) {
                        ERROR_CODE_WORK_ID_EXISTS
                    } else {
                        ERROR_CODE_API_NOT_SUCCESS
                    }
                }
                _addResult.value = isSuccess

            } catch (e: Exception) {
                _errorCode = ERROR_CODE_EXCEPTION
                _addResult.value = false
                Log.d(TAG, "e.message: ${e.message}")
            }
        }
    }

    fun editPeople(people: People, isPhotoTaken: Boolean) {
        viewModelScope.launch {
            val modifyInfoResult = async(Dispatchers.IO) {
                modifyInfo(people)
            }
            var modifyFaceResult: Deferred<Boolean>? = null
            if (isPhotoTaken) {
                modifyFaceResult = async(Dispatchers.IO) {
                    modifyFace(people)
                }
            }
            if (modifyInfoResult.await()) {
                if (isPhotoTaken) {
                    _editResult.value = modifyFaceResult!!.await()
                } else {
                    _editResult.value = true
                }
            } else {
                _editResult.value = false
            }
        }
    }

    private suspend fun modifyInfo(people: People): Boolean {
        return try {
            val response = repository.requestModifyPerson(people)
            val isSuccess = isModifySuccess(response)
            if (!isSuccess) {
                _errorCode = ERROR_CODE_API_NOT_SUCCESS
            }
            isSuccess
        } catch (e: Exception) {
            Log.d(TAG, "e.message: ${e.message}")
            _errorCode = ERROR_CODE_EXCEPTION
            false
        }
    }

    private suspend fun modifyFace(people: People): Boolean {
        return try {
            val response = repository.requestDeleteFace(people)
            val isSuccess = isDeleteFaceSuccess(response)
            if (isSuccess) {
                val response2 = repository.requestAddFace(people)
                val isSuccess2 = isAddFaceSuccess(response2)
                if (!isSuccess2) {
                    _errorCode = ERROR_CODE_API_NOT_SUCCESS
                }
                isSuccess
            } else {
                _errorCode = ERROR_CODE_API_NOT_SUCCESS
                false
            }
        } catch (e: Exception) {
            _errorCode = ERROR_CODE_EXCEPTION
            false
        }
    }

    fun resetErrorCode() {
        _errorCode = -1
    }

    private fun isAddSuccess(response: AddPerson) = response.status == 0 && response.detail == "success"
    private fun isModifySuccess(response: ModifyPerson) = response.status == 0 && response.detail == "success"
    private fun isDeleteFaceSuccess(response: DeleteFaces) = response.status == 0 && response.detail == "success"
    private fun isAddFaceSuccess(response: AddFaces) = response.status == 0 && response.detail == "success"

}


class EditorFactory(private val repository: PeopleRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EditorViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}