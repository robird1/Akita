package com.ulsee.shiba.ui.people

import android.util.Log
import com.ulsee.shiba.R
import com.ulsee.shiba.data.response.AllPerson
import com.ulsee.shiba.model.People

enum class AttributeType(val fieldName: String, val position: Int, val viewType: Int, val helperText: String, var inputValue: String, var isInputValid: Boolean) {

    NAME("Name", 0, R.layout.item_list_people_text, "*Required", "", false),
    WORK_ID("Work ID", 1, R.layout.item_list_people_text, "*Required","", false),
    MAIL("Email", 2, R.layout.item_list_people_text, "*Required", "", false),
    GENDER("Gender", 3, R.layout.item_list_people_gender, "*Required", "", false),
    AGE("Age", 4, R.layout.item_list_people_text, "", "", true),
    PHONE("Phone", 5, R.layout.item_list_people_text, "", "", true),
    ADDRESS("Address", 6, R.layout.item_list_people_text, "", "", true);

    companion object {
//        var id = 0         // DB record ID
        var faceImg = ""

        fun fromPosition(position: Int): AttributeType {
//            Log.d("AttributeType", "[Enter] fromPosition position: $position")

            for (instance in values()) {
                if (instance.position == position)
                    return instance
            }
            return NAME
        }

        fun clearAttributeData() {
            faceImg = ""
            for (i in values()) {
                when(i) {
                    NAME ->  i.isInputValid = false
                    WORK_ID -> i.isInputValid = false
                    GENDER -> i.isInputValid = false
                    MAIL -> i.isInputValid = false
                    AGE -> i.isInputValid = true
                    PHONE -> i.isInputValid = true
                    ADDRESS -> i.isInputValid = true
                }
                i.inputValue = ""
            }
        }

        fun setAttributeData(people: AllPerson) {
//            id = people.getID()
            if (people.faceImg != null) {
                faceImg = people.faceImg
            }
            NAME.inputValue = people.name
            WORK_ID.inputValue = people.userId
            GENDER.inputValue = people.gender
            MAIL.inputValue = people.email
            AGE.inputValue = if (people.age != 0) people.age.toString() else ""
            PHONE.inputValue = people.phone
            ADDRESS.inputValue = people.address
        }


        fun getAttributeData(): People {
            val people = People()
//            people.setID(id)
            people.setFaceImg(faceImg)
            people.setName(NAME.inputValue)
            people.setWorkID(WORK_ID.inputValue)
            people.setGender(GENDER.inputValue)
            people.setMail(MAIL.inputValue)
            people.setAge(AGE.inputValue)
            people.setAddress(ADDRESS.inputValue)
            people.setPhone(PHONE.inputValue)
            return people
        }
    }
}
