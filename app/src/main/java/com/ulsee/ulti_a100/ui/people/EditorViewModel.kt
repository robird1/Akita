package com.ulsee.ulti_a100.ui.people

import android.util.Log
import androidx.lifecycle.*
import com.ulsee.ulti_a100.model.People
import kotlinx.coroutines.launch

private val TAG = EditorViewModel::class.java.simpleName

class EditorViewModel(private val repository: EditorRepository) : ViewModel() {
    private var _result = MutableLiveData<Boolean>()
    val result : LiveData<Boolean>
        get() = _result


    fun addPeople(people: People) {
        viewModelScope.launch {
            try {
                _result.value = repository.addPeople(people)
            } catch (e: Exception) {
                Log.d(TAG, "e.message: ${e.message}")
            }
        }
    }

    fun editPeople(people: People) {
        viewModelScope.launch {
            try {
                _result.value = repository.editPeople(people)
            } catch (e: Exception) {
                Log.d(TAG, "e.message: ${e.message}")
            }
        }
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