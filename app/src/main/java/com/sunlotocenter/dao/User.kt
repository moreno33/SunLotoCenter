package com.sunlotocenter.dao

import com.sunlotocenter.utils.isNotEmpty
import org.joda.time.DateTime
import java.io.Serializable

abstract class User (
    id: Long?= null,
    sequence: Sequence= Sequence(),
    var firstName:String= "",
    var lastName: String= "",
    var phoneNumber:PhoneNumber?= null,
    var address:String= "",
    var city:String= "",
    var password:String= "",
    var profilePath: String= "",
    createdDateTime:DateTime= DateTime(),
    updatedDateTime:DateTime= DateTime(),
    var status: UserStatus= UserStatus.ACTIVE,
    var current: Boolean= true,
    var accountNumber: String= "",
    var fcmTopic: String= "",
    var sex: Sex= Sex.MALE,
    var actor: User?= null,
    var blames:List<Blame> = ArrayList()
): Entity(id, sequence, createdDateTime, updatedDateTime), Serializable{
    override fun toString(): String {
        return "${firstName} ${lastName}" + if(isNotEmpty(accountNumber)) "(${accountNumber})" else ""
    }
}