package com.ulsee.ulti_a100.ui.device.settings

import com.ulsee.ulti_a100.api.ApiService
import com.ulsee.ulti_a100.data.response.*
import okhttp3.RequestBody

class SettingRepository(private val url: String) {
    suspend fun getDeviceConfig(): getUIConfig {
        return ApiService.create(url).getDeviceConfig()
    }

    suspend fun setDeviceConfig(requestBody: RequestBody): SetUIConfig {
        return ApiService.create(url).setDeviceConfig(requestBody)
    }

    suspend fun getTime(): GetTime {
        return ApiService.create(url).getTime()
    }

    suspend fun setTime(requestBody: RequestBody): SetTime {
        return ApiService.create(url).setTime(requestBody)
    }

    suspend fun getComSettings(requestBody: RequestBody): GetComSettings {
        return ApiService.create(url).getComSettings(requestBody)
    }

    suspend fun setComSettings(requestBody: RequestBody): SetComSettings {
        return ApiService.create(url).setComSettings(requestBody)
    }

}