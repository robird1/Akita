package com.ulsee.ulti_a100.ui.device.settings

import android.util.Log
import androidx.lifecycle.*
import com.ulsee.ulti_a100.data.response.getUIConfig
import com.ulsee.ulti_a100.data.response.FaceUIConfig
import kotlinx.coroutines.launch

private val TAG = SettingViewModel::class.java.simpleName

class SettingViewModel(private val repository: SettingRepository) : ViewModel() {
    private var _config = MutableLiveData<FaceUIConfig>()
    val config: LiveData<FaceUIConfig>
        get() = _config

    init{
        getDeviceConfig()
    }

    private fun getDeviceConfig() {
        viewModelScope.launch {
            try {
                val response = repository.getDeviceConfig()
                if (isQuerySuccess(response)) {
                    _config.value = response.data.FaceUIConfig
                } else {
                    _config.value = null
                }

            } catch (e: Exception) {
                Log.d(TAG, "Exception e.message: ${e.message}")
                _config.value = null
            }
        }
    }

    private fun isQuerySuccess(response: getUIConfig) = response.status == 0 && response.detail == "success"

}


class SettingFactory(private val repository: SettingRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}