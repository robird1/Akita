package com.ulsee.ulti_a100.ui.people

import android.util.Log
import com.ulsee.ulti_a100.api.ApiService
import com.ulsee.ulti_a100.data.response.*
import com.ulsee.ulti_a100.model.People
import com.ulsee.ulti_a100.model.RealmPeople
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

private val TAG = PeopleRepository::class.java.simpleName

class PeopleRepository(private val url: String) {

    suspend fun requestPerson(requestBody: RequestBody): QueryPerson {
        return ApiService.create(url).requestPerson(requestBody)
    }

    suspend fun requestAllPerson(requestBody: RequestBody): QueryAllPerson {
        return ApiService.create(url).requestAllPerson(requestBody)
    }

    suspend fun requestDeletePerson(requestBody: RequestBody): DeletePerson {
        return ApiService.create(url).requestDeletePerson(requestBody)
    }

    suspend fun requestAddPerson(p: People): AddPerson {
        return ApiService.create(url!!).requestAddPerson(createAddRequestBody(p))
    }

    suspend fun requestModifyPerson(p: People): ModifyPerson {
        return ApiService.create(url!!).requestModifyPerson(createEditRequestBody(p))
    }

    suspend fun requestDeleteFace(p: People): DeleteFaces {
        return ApiService.create(url!!).deleteFaces(createDeleteFaceRequest(p))
    }

    suspend fun requestAddFace(p: People): AddFaces {
        return ApiService.create(url!!).addFaces(createAddFaceRequest(p))
    }

    private fun createAddRequestBody(p: People): RequestBody {
        val imgBase64 = "data:image/jpeg;base64,"+ p.getFaceImg()
        val tmp = if (p.getAge().isNotEmpty()) {

            "{\r\n    \"personId\": \"${p.getWorkID()}\",\r\n    \"userId\": \"${p.getWorkID()}\",\r\n    " +
                    "\"name\": \"${p.getName()}\",\r\n    \"age\": \"${p.getAge()}\",\r\n    \"gender\": \"${p.getGender()}\",\r\n    " +
                    "\"phone\": \"${p.getPhone()}\",\r\n    \"email\": \"${p.getMail()}\",\r\n    \"address\": \"${p.getAddress()}\",\r\n    \"images\": [\r\n        " +
                    "{\r\n            \"data\": \"$imgBase64\"\r\n        }\r\n    ],\r\n    \"accessInfo\": {\r\n        \"cardNum\": \"\",\r\n        \"password\": \"\",\r\n        \"authType\": 0,\r\n        \"roleType\": 0\r\n    }\r\n}"

        } else {

            "{\r\n    \"personId\": \"${p.getWorkID()}\",\r\n    \"userId\": \"${p.getWorkID()}\",\r\n    " +
                    "\"name\": \"${p.getName()}\",\r\n    \"gender\": \"${p.getGender()}\",\r\n    " +
                    "\"phone\": \"${p.getPhone()}\",\r\n    \"email\": \"${p.getMail()}\",\r\n    \"address\": \"${p.getAddress()}\",\r\n    \"images\": [\r\n        " +
                    "{\r\n            \"data\": \"$imgBase64\"\r\n        }\r\n    ],\r\n    \"accessInfo\": {\r\n        \"cardNum\": \"\",\r\n        \"password\": \"\",\r\n        \"authType\": 0,\r\n        \"roleType\": 0\r\n    }\r\n}"
        }
        Log.d(TAG, "tmp: $tmp")
        return tmp.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
    }

    private fun createEditRequestBody(p: People): RequestBody {
        val tmp = if (p.getAge().isNotEmpty()) {

            "{\r\n    \"personId\": \"${p.getWorkID()}\",\r\n    \"userId\": \"${p.getWorkID()}\",\r\n    \"name\": \"${p.getName()}\",\r\n" +
                    "    \"age\": ${p.getAge()},\r\n    \"gender\": \"${p.getGender()}\",\r\n    \"phone\": \"${p.getPhone()}\",\r\n" +
                    "    \"email\": \"${p.getMail()}\",\r\n    \"address\": \"${p.getAddress()}\"\r\n}\r\n"

        } else {

            "{\r\n    \"personId\": \"${p.getWorkID()}\",\r\n    \"userId\": \"${p.getWorkID()}\",\r\n    \"name\": \"${p.getName()}\",\r\n" +
                    "    \"gender\": \"${p.getGender()}\",\r\n    \"phone\": \"${p.getPhone()}\",\r\n" +
                    "    \"email\": \"${p.getMail()}\",\r\n    \"address\": \"${p.getAddress()}\"\r\n}\r\n"
        }
//        Log.d(TAG, "tmp: $tmp")
        return tmp.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

    }

    private fun createDeleteFaceRequest(people: People): RequestBody {
        val tmp = "{\r\n    \"personId\" : \"${people.getWorkID()}\"\r\n}\r\n"
        return tmp.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
    }

    private fun createAddFaceRequest(people: People): RequestBody {
        val imgBase64 = "data:image/jpeg;base64,"+ people.getFaceImg()
        val tmp = "{\r\n    \"personId\" : \"${people.getWorkID()}\",\r\n    \"images\": " +
                "[\r\n        {\r\n            \"data\": \"${imgBase64}\"\r\n        }\r\n    ]\r\n}\r\n"
        return tmp.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
    }
//    suspend fun deletePeople(people: People) = withContext(Dispatchers.IO) {
//        val realm = Realm.getDefaultInstance()
//        realm.beginTransaction()
//        val rows = realm.where(RealmPeople::class.java).equalTo("id", people.getID()).findAll()
//        rows.deleteAllFromRealm()
//        realm.commitTransaction()
//        realm.close()
//        return@withContext true
//    }
//
//    suspend fun loadPeople(): List<People> = withContext(Dispatchers.IO) {
//        val realm = Realm.getDefaultInstance()
//        val results = realm.where<RealmPeople>().findAll()
//        val peopleList = ArrayList<People>()
//        for (realmPeople in results) {
//            val people = People.clone(realmPeople)
//            peopleList.add(people)
//        }
//        realm.close()
//        return@withContext peopleList
//    }
//
//    suspend fun searchPeople(keyword: String): List<People> = withContext(Dispatchers.IO) {
//        val realm = Realm.getDefaultInstance()
//        val results: RealmResults<RealmPeople>
//        var where: RealmQuery<RealmPeople> = realm.where(RealmPeople::class.java).beginGroup()
//        where = where.contains("mName", keyword, Case.INSENSITIVE)
//        results = where.endGroup().sort("mCreatedAt", Sort.DESCENDING).findAll()
//        val peopleList = ArrayList<People>()
//        for (realmPeople in results) {
//            val people = People.clone(realmPeople)
//            peopleList.add(people)
//        }
//        realm.close()
//        return@withContext peopleList
//    }
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