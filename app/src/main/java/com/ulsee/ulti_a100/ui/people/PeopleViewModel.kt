package com.ulsee.ulti_a100.ui.people

import android.util.Log
import androidx.lifecycle.*
import com.ulsee.ulti_a100.model.People
import com.ulsee.ulti_a100.utils.Event
import kotlinx.coroutines.launch

private val TAG = PeopleViewModel::class.java.simpleName

class PeopleViewModel(private val repository: PeopleRepository) : ViewModel() {
    private var _peopleList = MutableLiveData<List<People>>()
    val peopleList : LiveData<List<People>>
        get() = _peopleList
    private var _result = MutableLiveData<Event<Boolean>>()
    val result : LiveData<Event<Boolean>>
        get() = _result


    init {
        loadRecord()
    }

    fun loadRecord() {
        viewModelScope.launch {
            try {
                _peopleList.value = repository.loadPeople()

            } catch (e: Exception) {
                Log.d(TAG, "[Enter] Exception: ${e.message}")
            }
        }
    }

    fun deleteRecord(people: People) {
        viewModelScope.launch {
            try{
                _result.value = Event(repository.deletePeople(people))
            } catch (e: Exception) {
                Log.d(TAG, "[Enter] Exception: ${e.message}")
            }
        }
    }

}


class PeopleFactory(private val repository: PeopleRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PeopleViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PeopleViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}