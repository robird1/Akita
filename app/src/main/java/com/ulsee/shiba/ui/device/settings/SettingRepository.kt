package com.ulsee.shiba.ui.device.settings

import com.ulsee.shiba.api.ApiService
import com.ulsee.shiba.data.response.*
import okhttp3.RequestBody

class SettingRepository(private var url: String) {
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

    // used by capture settings
    suspend fun getComSettings(requestBody: RequestBody): GetComSettings {
        return ApiService.create(url).getComSettings(requestBody)
    }

    // used by capture settings
    suspend fun setComSettings(requestBody: RequestBody): SetComSettings {
        return ApiService.create(url).setComSettings(requestBody)
    }

    suspend fun getWifiConfig(): GetWifiConfig {
        url = url.replace(":8080", ":8081")
        return ApiService.create(url).getWifiConfig()
    }

    suspend fun setWifiConfig(requestBody: RequestBody): SetWifiConfig {
        url = url.replace(":8080", ":8081")
        return ApiService.create(url).setWifiConfig(requestBody)
    }

}