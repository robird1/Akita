package com.ulsee.ulti_a100.ui.people

import com.ulsee.ulti_a100.model.People
import com.ulsee.ulti_a100.model.RealmPeople
import io.realm.Realm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val TAG = EditorRepository::class.java.simpleName

class EditorRepository {
    suspend fun addPeople(p: People): Boolean = withContext(Dispatchers.IO) {
//        Log.d(TAG, "[Enter] addPeople")
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        val people: RealmPeople = realm.createObject(RealmPeople::class.java)
        people.setID(getRecordID(realm))
        people.setFaceImg(p.getFaceImg())
        people.setWorkID(p.getWorkID())
        people.setName(p.getName())
        people.setGender(p.getGender())
        people.setMail(p.getMail())
        people.setAge(p.getAge())
        people.setPhone(p.getPhone())
        people.setAddress(p.getAddress())
        realm.commitTransaction()
        return@withContext true
    }

    suspend fun editPeople(p: People): Boolean = withContext(Dispatchers.IO) {
//        Log.d(TAG, "[Enter] editPeople")
//        Log.d(TAG, "[Enter] p.getID: ${p.getID()}")

        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        val people = realm.where(RealmPeople::class.java).equalTo("id", p.getID()).findFirst()!!
        people.setFaceImg(p.getFaceImg())
        people.setWorkID(p.getWorkID())
        people.setName(p.getName())
        people.setGender(p.getGender())
        people.setMail(p.getMail())
        people.setAge(p.getAge())
        people.setPhone(p.getPhone())
        people.setAddress(p.getAddress())
        realm.commitTransaction()
        return@withContext true
    }

    private fun getRecordID(realm: Realm): Int {
        val currentIdNum = realm.where(RealmPeople::class.java).max("id")
        return if (currentIdNum == null) {
            1
        } else {
            currentIdNum.toInt() + 1
        }
    }

}