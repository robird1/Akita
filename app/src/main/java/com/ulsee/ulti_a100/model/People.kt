package com.ulsee.ulti_a100.model

open class People {

    // record ID of phone DB
    private var id = 0
    fun setID(value: Int) {
        id = value
    }
    fun getID(): Int {
        return id
    }

    private var mFaceImg: String = ""
    fun setFaceImg(value: String) {
        mFaceImg = value
    }
    fun getFaceImg(): String {
        return mFaceImg
    }

    private var mWorkID: String = ""
    fun setWorkID(value: String) {
        mWorkID = value
    }
    fun getWorkID(): String {
        return mWorkID
    }

    private var mName: String = ""
    fun setName(value: String) {
        mName = value
    }
    fun getName(): String {
        return mName
    }

    private var mGender: String = ""
    fun setGender(value: String) {
        mGender = value
    }
    fun getGender(): String {
        return mGender
    }

    private var mMail: String = ""
    fun setMail(value: String) {
        mMail = value
    }
    fun getMail(): String {
        return mMail
    }

    private var mAge: String = ""
    fun setAge(value: String) {
        mAge = value
    }
    fun getAge(): String {
        return mAge
    }

    private var mPhone: String = ""
    fun setPhone(value: String) {
        mPhone = value
    }
    fun getPhone(): String {
        return mPhone
    }

    private var mAddress: String = ""
    fun setAddress(value: String) {
        mAddress = value
    }
    fun getAddress(): String {
        return mAddress
    }

    // for record sorting of phone DB
    private var mCreatedAt: Long = 0
    fun setCreatedAt(value: Long) {
        mCreatedAt = value
    }
    fun getCreatedAt(): Long {
        return mCreatedAt
    }

    var checked = false

    companion object {
        fun clone (realmPeople: RealmPeople) : People {
            val people = People()
            people.setID(realmPeople.getID())
            people.setFaceImg(realmPeople.getFaceImg())
            people.setWorkID(realmPeople.getWorkID())
            people.setName(realmPeople.getName())
            people.setGender(realmPeople.getGender())
            people.setMail(realmPeople.getMail())
            people.setAge(realmPeople.getAge())
            people.setPhone(realmPeople.getPhone())
            people.setAddress(realmPeople.getAddress())
            people.setCreatedAt(realmPeople.getCreatedAt())
            return people
        }
    }
}