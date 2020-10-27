package com.ulsee.ulti_a100.ui.people

import com.ulsee.ulti_a100.api.ApiService
import com.ulsee.ulti_a100.data.response.AddPerson
import com.ulsee.ulti_a100.data.response.QueryPerson
import com.ulsee.ulti_a100.model.People
import com.ulsee.ulti_a100.model.RealmPeople
import io.realm.*
import io.realm.kotlin.where
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.RequestBody


class PeopleRepository {
//    suspend fun requestAllPerson(baseUrl: String): AllPerson {
//        return ApiService.create(baseUrl).requestAllPerson()
//    }

    suspend fun requestPerson(baseUrl: String, requestBody: RequestBody): QueryPerson {
        return ApiService.create(baseUrl).requestPerson(requestBody)
    }

    suspend fun requestAddPerson(baseUrl: String, requestBody: RequestBody): AddPerson {
        return ApiService.create(baseUrl).requestAddPerson(requestBody)
    }


    suspend fun deletePeople(people: People) = withContext(Dispatchers.IO) {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        val rows = realm.where(RealmPeople::class.java).equalTo("id", people.getID()).findAll()
        rows.deleteAllFromRealm()
        realm.commitTransaction()
        realm.close()
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
        realm.close()
        return@withContext peopleList
    }

    suspend fun searchPeople(keyword: String): List<People> = withContext(Dispatchers.IO) {
        val realm = Realm.getDefaultInstance()
        val results: RealmResults<RealmPeople>
        var where: RealmQuery<RealmPeople> = realm.where(RealmPeople::class.java).beginGroup()
        where = where.contains("mName", keyword, Case.INSENSITIVE)
        results = where.endGroup().sort("mCreatedAt", Sort.DESCENDING).findAll()
        val peopleList = ArrayList<People>()
        for (realmPeople in results) {
            val people = People.clone(realmPeople)
            peopleList.add(people)
        }
        realm.close()
        return@withContext peopleList
    }

}