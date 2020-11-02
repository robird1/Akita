package com.ulsee.ulti_a100.ui.people

import android.util.Log
import androidx.lifecycle.*
import com.ulsee.ulti_a100.data.response.*
import com.ulsee.ulti_a100.model.People
import com.ulsee.ulti_a100.utils.Event
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

private val TAG = PeopleViewModel::class.java.simpleName

class PeopleViewModel(private val repository: PeopleRepository) : ViewModel() {
    private var _peopleList = MutableLiveData<List<AllPerson>>()
    val peopleList : LiveData<List<AllPerson>>
        get() = _peopleList
//    private var _base64Face = MutableLiveData<String>()
//    val base64Face : LiveData<String>
//        get() = _base64Face
    private var _queryResponse = MutableLiveData<QueryFaceResponse>()
    val queryFaceResponse : LiveData<QueryFaceResponse>
        get() = _queryResponse

    private var _result = MutableLiveData<Event<Boolean>>()
    val result : LiveData<Event<Boolean>>
        get() = _result
//    private var _searchPeopleList = MutableLiveData<Event<List<AllPerson>>>()
//    val searchPeopleList : LiveData<Event<List<AllPerson>>>
//        get() = _searchPeopleList


    init {
        Log.d(TAG, "[Enter] init() -> loadRecord()")
        loadRecord()
    }

    fun loadRecord() {
        viewModelScope.launch {
            try {
//                _peopleList.value = repository.loadPeople()
                val response= repository.requestAllPerson(createQueryAllRequestBody())
                if (isQueryAllSuccess(response)) {
                    _peopleList.value = response.data
                } else {
                    // TODO
                }
            } catch (e: Exception) {
                Log.d(TAG, "[Enter] Exception: ${e.message}")
                // TODO
            }
        }
    }

    fun deletePerson(people: AllPerson) {
        viewModelScope.launch {
            try{

                val response = repository.requestDeletePerson(createDeleteRequestBody(people))
                _result.value = Event(isDeleteSuccess(response))
//                if (isDeleteSuccess(response)) {
//                    _result.value = Event(true)
//                } else {
//                    _result.value = Event(false)
//                }

            } catch (e: Exception) {
                Log.d(TAG, "[Enter] Exception: ${e.message}")
                _result.value = Event(false)
            }
        }
    }

    fun queryPerson(id: String) {
        viewModelScope.launch {
            try {
                val response = repository.requestPerson(createQueryRequestBody(id))
                if (isQuerySuccess(response)) {
//                    Log.d(TAG, "[Enter] isQuerySuccess()")
                    if (response.data.faces != null) {
                        val imgBase64 = response.data.faces[0].orgimg
                        _queryResponse.value = QueryFaceResponse(response.data.personId, imgBase64)
                    }
                } else {
                    // do nothing
                }
            } catch (e: Exception) {
                Log.d(TAG, "[Enter] Exception: ${e.message}")
            }
        }

    }

//    fun searchPeople(keyword: String) {
//        viewModelScope.launch {
//            try{
//                _searchPeopleList.value = Event(repository.searchPeople(keyword))
//            } catch (e: Exception) {
//                Log.d(TAG, "[Enter] Exception: ${e.message}")
//            }
//        }
//    }

    private fun isQueryAllSuccess(response: QueryAllPerson) = response.status == 0 && response.detail == "success"
    private fun isDeleteSuccess(response: DeletePerson) = response.status == 0 && response.detail == "success"
    private fun isQuerySuccess(response: QueryPerson) = response.status == 0 && response.detail == "success"


    private fun isDeviceOnline(deviceInfo: GetDeviceInfo) = deviceInfo.status == 0 && deviceInfo.detail == "OK"
    private fun isFaceExisted(response: QueryPerson) = response.status == 0 && response.detail == "success"
    private fun isFaceNotExisted(response: QueryPerson) = response.status == -1 && response.detail == "arstack error, 531(No valid information found)"
    private fun isAddSuccess(response: AddPerson) = response.status == 0 && response.detail == "success"

    private fun createJsonRequestBody(vararg params: Pair<String, String>): RequestBody {
        val tmp = JSONObject(mapOf(*params)).toString()
//        Log.d(TAG, "JSONObject toString: $tmp")
        return tmp.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
    }

    private fun createQueryAllRequestBody(): RequestBody {
        val tmp = "{\r\n    \"pageSize\" : 0\r\n}\r\n"
//        Log.d(TAG, "tmp: $tmp")
        return tmp.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
    }

    private fun createDeleteRequestBody(person: AllPerson): RequestBody {
        val tmp = "{\r\n    \"personId\" : \"${person.userId}\"\r\n}"
        Log.d(TAG, "tmp: $tmp")
        return tmp.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
    }

    private fun createQueryRequestBody(id: String): RequestBody {
        val tmp = "{\r\n    \"personId\" : \"${id}\"\r\n}"
//        Log.d(TAG, "tmp: $tmp")
        return tmp.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
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

data class QueryFaceResponse(val personId: String, val imgBase64: String)