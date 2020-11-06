package com.ulsee.ulti_a100.ui.people

import android.util.Log
import androidx.lifecycle.*
import com.ulsee.ulti_a100.data.response.AddPerson
import com.ulsee.ulti_a100.data.response.GetDeviceInfo
import com.ulsee.ulti_a100.model.Device
import com.ulsee.ulti_a100.model.People
import com.ulsee.ulti_a100.ui.device.DeviceInfoRepository
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

private val TAG = DeviceSyncViewModel::class.java.simpleName

class DeviceSyncViewModel(private val repo: DeviceInfoRepository) : ViewModel() {
    private var _onlineList = MutableLiveData<ArrayList<Device>>()
    val onlineList: LiveData<ArrayList<Device>>
        get() = _onlineList
    private var _syncResult = MutableLiveData<Boolean>()
    val syncResult : LiveData<Boolean>
        get() = _syncResult
    private var _errorCode = -1
    val errorCode: Int
        get() = _errorCode


    init {
        viewModelScope.launch {
            try {
                val list = ArrayList<Device>()
                for (i in repo.loadDevices()) {
                    val deviceInfo = repo.requestDeviceInfo(i.getIP())
                    val isConnected = isDeviceOnline(deviceInfo)
                    if (isConnected) {
                        list.add(i)
                    }
                }
                _onlineList.value = list

                Log.d(TAG, "list.size: ${list.size}")

            } catch (e: Exception) {
//                _errorCode = ERROR_CODE_EXCEPTION
//                _onlineList.value = null
                Log.d(TAG, "e.message: ${e.message}")
            }
        }

    }

    fun resetErrorCode() {
        _errorCode = -1
    }

    fun synFace(people: People, ipList: ArrayList<String>) {
        viewModelScope.launch {
            try {
                for (ip in ipList) {
                    Log.d(TAG, "[Enter] ip: $ip ")

                    val repository = EditorRepository(ip)
                    val response = repository.requestAddPerson(createAddRequestBody(people))
                    val isSuccess = isAddSuccess(response)
                    if (!isSuccess) {
                        val isWorkIdExist = response.detail == "arstack error, 548(Workid is already exist)"
                        if (isWorkIdExist) {
//                        Log.d(TAG, "[Enter] work id exists")
                            _errorCode = ERROR_CODE_WORK_ID_EXISTS
                            _syncResult.value = false
                            break
                        } else {
                            _errorCode = ERROR_CODE_API_NOT_SUCCESS
                            _syncResult.value = false
                        }

                        Log.d(TAG, "[Enter] sync failed -> ip: $ip")

                    }
                    else {
                        Log.d(TAG, "[Enter] sync success -> ip: $ip")
                    }
                }
                _syncResult.value = true

            } catch (e: Exception) {
                _errorCode = ERROR_CODE_EXCEPTION
                _syncResult.value = false
                Log.d(TAG, "[Exception] e.message: ${e.message}")
            }
        }
    }


    private fun isDeviceOnline(deviceInfo: GetDeviceInfo) = deviceInfo.status == 0 && deviceInfo.detail == "OK"
    private fun isAddSuccess(response: AddPerson) = response.status == 0 && response.detail == "success"

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

}

class DeviceSyncFactory(private val repository: DeviceInfoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DeviceSyncViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DeviceSyncViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
