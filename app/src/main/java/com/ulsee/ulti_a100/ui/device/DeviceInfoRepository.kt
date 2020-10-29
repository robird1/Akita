package com.ulsee.ulti_a100.ui.device

import com.ulsee.ulti_a100.api.ApiService
import com.ulsee.ulti_a100.data.response.GetDeviceInfo
import com.ulsee.ulti_a100.data.response.Info
import com.ulsee.ulti_a100.model.Device
import com.ulsee.ulti_a100.model.RealmDevice
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val TAG = DeviceInfoRepository::class.java.simpleName

class DeviceInfoRepository {

    suspend fun requestDeviceInfo(baseUrl: String): GetDeviceInfo {
        return ApiService.create(baseUrl).requestDeviceInfo()
    }

    suspend fun addDevice(info: Info, deviceName: String, url:String) = withContext(Dispatchers.IO) {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        val device: RealmDevice = realm.createObject(RealmDevice::class.java)
        device.setID(deviceName)
        device.setIP(url)
        device.setSN(info.productsn)
        device.setMAC(info.mac)
        device.setChipID(info.chipid)
        realm.commitTransaction()
        realm.close()
    }

    suspend fun editDevice(deviceID: String, deviceName: String, url:String) = withContext(Dispatchers.IO) {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        val device = realm.where(RealmDevice::class.java).equalTo("mID", deviceID).findFirst()!!
        device.setID(deviceName)
        device.setIP(url)
        realm.commitTransaction()
        realm.close()
    }

    suspend fun queryDevice(deviceID: String): Device = withContext(Dispatchers.IO) {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        val realmDevice = realm.where(RealmDevice::class.java).equalTo("mID", deviceID).findFirst()!!
        val device = Device.clone(realmDevice)
        realm.commitTransaction()
        realm.close()
        return@withContext device
    }

    suspend fun isDevicePaired(macAddress: String): Boolean = withContext(Dispatchers.IO) {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        val count = realm.where(RealmDevice::class.java).equalTo("mMAC", macAddress).findAll().size
        realm.commitTransaction()
        realm.close()
        return@withContext count > 0
    }

    suspend fun isDeviceNameExisting(id: String): Boolean = withContext(Dispatchers.IO) {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        val count = realm.where(RealmDevice::class.java).equalTo("mID", id).findAll().size
        realm.commitTransaction()
        realm.close()
        return@withContext count > 0
    }


    suspend fun deleteDevice(deviceID: String) = withContext(Dispatchers.IO) {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        val rows = realm.where(RealmDevice::class.java)
            .equalTo("mID", deviceID).findAll()
        rows.deleteAllFromRealm()
        realm.commitTransaction()
        realm.close()
    }

    suspend fun loadDevices(): List<Device> = withContext(Dispatchers.IO) {
        val realm = Realm.getDefaultInstance()
        val results = realm.where<RealmDevice>().findAll()
        val deviceList = ArrayList<Device>()
        for (realmDevice in results) {
            val device = Device.clone(realmDevice)
            deviceList.add(device)
        }
        realm.close()
        return@withContext deviceList
    }

}