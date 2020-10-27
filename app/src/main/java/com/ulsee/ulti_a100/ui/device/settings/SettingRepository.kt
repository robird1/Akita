package com.ulsee.ulti_a100.ui.device.settings

import com.ulsee.ulti_a100.api.ApiService
import com.ulsee.ulti_a100.data.response.SetUIConfig
import com.ulsee.ulti_a100.data.response.getUIConfig
import okhttp3.RequestBody

class SettingRepository(private val url: String) {
    suspend fun getDeviceConfig(): getUIConfig {
        return ApiService.create(url).getDeviceConfig()
    }

    suspend fun setDeviceConfig(requestBody: RequestBody): SetUIConfig {
        return ApiService.create(url).setDeviceConfig(requestBody)
    }

}