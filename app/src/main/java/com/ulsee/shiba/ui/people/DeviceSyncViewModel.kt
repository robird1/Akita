package com.ulsee.shiba.ui.people

import android.util.Log
import androidx.lifecycle.*
import com.ulsee.shiba.data.response.AddPerson
import com.ulsee.shiba.data.response.GetDeviceInfo
import com.ulsee.shiba.model.Device
import com.ulsee.shiba.model.People
import com.ulsee.shiba.ui.device.DeviceInfoRepository
import kotlinx.coroutines.*

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
        getOnlineDevices()
    }

    private fun getOnlineDevices() {
        viewModelScope.launch {
            val deferredList = repo.loadDevices().map { device ->
                async(Dispatchers.IO) {
                    try {
                        Log.d(TAG, "[Before] repo.requestDeviceInfo() ip: ${device.getIP()}")
                        val deviceInfo = repo.requestDeviceInfo(device.getIP())
                        Log.d(TAG, "[After] repo.requestDeviceInfo() ip: ${device.getIP()}")
                        if (isDeviceOnline(deviceInfo)) return@async device
                        else return@async null
                    } catch (e: Exception) {
                        Log.d(TAG, "e.message: ${e.message}")
                        return@async null
                    }
                }
            }
            _onlineList.value = obtainOnlineList(deferredList)
        }
    }

    private suspend fun obtainOnlineList(deferredList: List<Deferred<Device?>>): ArrayList<Device> {
        val list = ArrayList<Device>()
        for (i in deferredList) {
            val device = i.await()
            device?.let { list.add(it) }
        }
        return list
    }

    fun synFace(people: People, ipList: ArrayList<String>) {
        viewModelScope.launch {
            val deferredList = ipList.map { ip ->
                async(Dispatchers.IO) {
                    Log.d(TAG, "[Enter] ip: $ip ")
                    requestAddPerson(ip, people)
                }
            }
            deferredList.awaitAll()
            _syncResult.value = true
        }
    }

    private suspend fun requestAddPerson(ip: String,  people: People) {
        Log.d(TAG, "[Before] repository.requestAddPerson")
        try {
            val repository = PeopleRepository(ip)
            val response = repository.requestAddPerson(people)
            val isSuccess = isAddSuccess(response)
            if (!isSuccess) {
                withContext(Dispatchers.Main) {
                    if (isWorkIdExist(response)) {
                        _errorCode = ERROR_CODE_WORK_ID_EXISTS
                        _syncResult.value = false
                    } else {
                        _errorCode = ERROR_CODE_API_NOT_SUCCESS
                        _syncResult.value = false
                    }
                }
                Log.d(TAG, "[Enter] sync failed -> ip: $ip")
            } else {
                Log.d(TAG, "[Enter] sync success -> ip: $ip")
            }
        } catch (e: Exception) {
            Log.d(TAG, "[Exception] e.message: ${e.message}")
            withContext(Dispatchers.Main) {
                _errorCode = ERROR_CODE_EXCEPTION
                _syncResult.value = false
            }
        }
    }

    fun resetErrorCode() {
        _errorCode = -1
    }

    private fun isWorkIdExist(response: AddPerson) = response.detail == "arstack error, 548(Workid is already exist)"
    private fun isDeviceOnline(deviceInfo: GetDeviceInfo) = deviceInfo.status == 0 && deviceInfo.detail == "OK"
    private fun isAddSuccess(response: AddPerson) = response.status == 0 && response.detail == "success"

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
