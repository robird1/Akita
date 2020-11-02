package com.ulsee.ulti_a100.ui.people

import com.ulsee.ulti_a100.api.ApiService
import com.ulsee.ulti_a100.data.response.AddPerson
import com.ulsee.ulti_a100.data.response.ModifyPerson
import okhttp3.RequestBody

private val TAG = EditorRepository::class.java.simpleName

class EditorRepository(private val url: String?) {
    suspend fun requestAddPerson(requestBody: RequestBody): AddPerson {
        return ApiService.create(url!!).requestAddPerson(requestBody)
    }

    suspend fun requestModifyPerson(requestBody: RequestBody): ModifyPerson {
        return ApiService.create(url!!).requestModifyPerson(requestBody)
    }

//    suspend fun addPeople(p: People): Boolean = withContext(Dispatchers.IO) {
//        val realm = Realm.getDefaultInstance()
//        realm.beginTransaction()
//        val people: RealmPeople = realm.createObject(RealmPeople::class.java)
//        people.setID(getRecordID(realm))
//        people.setFaceImg(p.getFaceImg())
//        people.setWorkID(p.getWorkID())
//        people.setName(p.getName())
//        people.setGender(p.getGender())
//        people.setMail(p.getMail())
//        people.setAge(p.getAge())
//        people.setPhone(p.getPhone())
//        people.setAddress(p.getAddress())
//        people.setCreatedAt(System.currentTimeMillis())
//        realm.commitTransaction()
//        realm.close()
//        return@withContext true
//    }
//
//    suspend fun editPeople(p: People): Boolean = withContext(Dispatchers.IO) {
//        val realm = Realm.getDefaultInstance()
//        realm.beginTransaction()
//        val people = realm.where(RealmPeople::class.java).equalTo("id", p.getID()).findFirst()!!
//        people.setFaceImg(p.getFaceImg())
//        people.setWorkID(p.getWorkID())
//        people.setName(p.getName())
//        people.setGender(p.getGender())
//        people.setMail(p.getMail())
//        people.setAge(p.getAge())
//        people.setPhone(p.getPhone())
//        people.setAddress(p.getAddress())
//        people.setCreatedAt(System.currentTimeMillis())
//        realm.commitTransaction()
//        realm.close()
//        return@withContext true
//    }
//
//    private fun getRecordID(realm: Realm): Int {
//        val currentIdNum = realm.where(RealmPeople::class.java).max("id")
//        return if (currentIdNum == null) {
//            1
//        } else {
//            currentIdNum.toInt() + 1
//        }
//    }

}