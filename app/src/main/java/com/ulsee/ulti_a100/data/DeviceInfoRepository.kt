package com.ulsee.ulti_a100.data

import android.content.Context
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import com.ulsee.ulti_a100.api.ApiService
import com.ulsee.ulti_a100.data.response.DeviceInfo
import com.ulsee.ulti_a100.data.response.Info
import com.ulsee.ulti_a100.model.Device
import com.ulsee.ulti_a100.model.RealmDevice
import io.realm.Realm
import io.realm.kotlin.where


class DeviceInfoRepository {

    suspend fun requestDeviceInfo(baseUrl: String): DeviceInfo {
        return ApiService.create(baseUrl).requestDeviceInfo()
    }

    fun saveDeviceInfo(deviceName: String, url:String) {
        saveToDB(deviceName, url)
    }

    fun addDevice(info: Info, deviceName: String, url:String, liveDataResult: MutableLiveData<Boolean>) {
//        saveToDB(info, deviceName, url)
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        val device: RealmDevice = realm.createObject(RealmDevice::class.java)
        device.setID(deviceName)
        device.setIP(url)
        device.setSN(info.sn)
        device.setMAC(info.mac)
        device.setChipID(info.chipid)
        realm.commitTransaction()

        liveDataResult.value = true
    }

    fun editDevice(deviceID: String, deviceName: String, url:String, liveDataResult: MutableLiveData<Boolean>) {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        val device: RealmDevice = realm.where(RealmDevice::class.java).equalTo("mID", deviceID).findFirst()!!
        device.setID(deviceName)
        device.setIP(url)
        realm.commitTransaction()

        liveDataResult.value = true
    }

    fun deleteDevice(context: Context, deviceID: String) {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        val rows = realm.where(RealmDevice::class.java)
            .equalTo("mID", deviceID).findAll()
        rows.deleteAllFromRealm()
        realm.commitTransaction()

        val intent = Intent("Device removed")
        intent.putExtra("device_id", deviceID)
        context.sendBroadcast(intent)
    }

    fun loadDevices(): List<Device> {
        val realm = Realm.getDefaultInstance()
        val results = realm.where<RealmDevice>().findAll()
        val deviceList = ArrayList<Device>()

        for (realmDevice in results) {
            val device = Device.clone(realmDevice)
            deviceList.add(device)
        }
        return deviceList
    }

    private fun saveToDB(deviceName: String, url:String) {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        val device: RealmDevice = realm.createObject(RealmDevice::class.java)
        device.setID(deviceName)
        device.setIP(url)
        realm.commitTransaction()
    }

}