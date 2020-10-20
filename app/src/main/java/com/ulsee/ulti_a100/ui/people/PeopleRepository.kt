package com.ulsee.ulti_a100.ui.people

import com.ulsee.ulti_a100.model.People
import com.ulsee.ulti_a100.model.RealmPeople
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PeopleRepository {
    suspend fun deletePeople(people: People) = withContext(Dispatchers.IO) {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        val rows = realm.where(RealmPeople::class.java).equalTo("id", people.getID()).findAll()
        rows.deleteAllFromRealm()
        realm.commitTransaction()
        return@withContext true
    }

    suspend fun loadPeople(): List<People> = withContext(Dispatchers.IO) {
        val realm = Realm.getDefaultInstance()
        val results = realm.where<RealmPeople>().findAll()
        val peopleList = ArrayList<People>()
        for (realmPeople in results) {
            val people = People.clone(realmPeople)
            peopleList.add(people)
        }
        return@withContext peopleList
    }

}