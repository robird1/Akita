package com.ulsee.shiba.model

class Device {

     private var mID: String = ""
     fun setID(value: String) {
          mID = value
     }
     fun getID(): String {
          return mID
     }

     private var mIP: String = ""
     fun setIP(value: String) {
          mIP = value
     }
     fun getIP(): String {
          return mIP
     }

     private var mSN: String = ""
     fun setSN(value: String) {
          mSN = value
     }
     fun getSN(): String {
          return mSN
     }

     private var mMAC: String = ""
     fun setMAC(value: String) {
          mMAC = value
     }
     fun getMAC(): String {
          return mMAC
     }

     private var chipID: String = ""
     fun setChipID(value: String) {
          chipID = value
     }
     fun getChipID(): String {
          return chipID
     }

     companion object {
          fun clone (realmDevice: RealmDevice) : Device {
               val device = Device()
               device.setID(realmDevice.getID())
               device.setIP(realmDevice.getIP())
               device.setSN(realmDevice.getSN())
               device.setMAC(realmDevice.getMAC())
               device.setChipID(realmDevice.getChipID())
               return device
          }
     }

}
