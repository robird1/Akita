/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ulsee.shiba.api

import com.ulsee.shiba.data.response.*
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

interface ApiService {

    @POST("api/v1/face/getDeviceInfo")
    suspend fun requestDeviceInfo(): GetDeviceInfo

//    @POST("api/v1/face/querySnapFace")
//    suspend fun requestSnapshots(): Snapshot

    @POST("api/v1/face/getAttendRecordCount")
    suspend fun requestAttendRecordCount(): GetAttendRecordCount

    @POST("api/v1/face/queryAttendRecord")
    suspend fun requestAttendRecord(@Body params: RequestBody): QueryAttendRecord

    @POST("api/v1/face/queryAllPerson")
    suspend fun requestAllPerson(@Body params: RequestBody): QueryAllPerson

    @POST("api/v1/face/queryPerson")
    suspend fun requestPerson(@Body params: RequestBody): QueryPerson

    @POST("api/v1/face/addPerson")
    suspend fun requestAddPerson(@Body params: RequestBody): AddPerson

    @POST("api/v1/face/modifyPerson")
    suspend fun requestModifyPerson(@Body params: RequestBody): ModifyPerson

    @POST("api/v1/face/deletePerson")
    suspend fun requestDeletePerson(@Body params: RequestBody): DeletePerson

    @POST("api/v1/face/getUIConfig")
    suspend fun getDeviceConfig(): getUIConfig

    @POST("api/v1/face/setUIConfig")
    suspend fun setDeviceConfig(@Body params: RequestBody): SetUIConfig

    @POST("api/v1/face/clearAttendRecord")
    suspend fun clearAttendRecord(@Body params: RequestBody): ClearAttendRecord

    @POST("api/v1/face/getTime")
    suspend fun getTime(): GetTime

    @POST("api/v1/face/setTime")
    suspend fun setTime(@Body params: RequestBody): SetTime

    @POST("api/v1/face/getComSettings")
    suspend fun getComSettings(@Body params: RequestBody): GetComSettings

    @POST("api/v1/face/setComSettings")
    suspend fun setComSettings(@Body params: RequestBody): SetComSettings

    @POST("api/v1/face/deleteFaces")
    suspend fun deleteFaces(@Body params: RequestBody): DeleteFaces

    @POST("api/v1/face/addFaces")
    suspend fun addFaces(@Body params: RequestBody): AddFaces

    @POST("api/v1/face/getWifiConfig")
    suspend fun getWifiConfig(): GetWifiConfig

    @POST("api/v1/face/setWifiConfig")
    suspend fun setWifiConfig(@Body params: RequestBody): SetWifiConfig

    companion object {
        fun create(baseUrl: String): ApiService {
            val logger = HttpLoggingInterceptor()
            logger.level = Level.BASIC

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .build()
            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
    }
}
