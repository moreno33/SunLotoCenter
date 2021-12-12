package com.sunlotocenter.dao

import com.sunlotocenter.MyApplication
import com.sunlotocenter.utils.isNotEmpty
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import java.io.Serializable

abstract class User (
    id: Long?= null,
    sequence: Sequence= Sequence(),
    var firstName:String?= null,
    var lastName: String?= null,
    var phoneNumber:PhoneNumber?= null,
    var address:String?= null,
    var city:String?= null,
    var password:String?= null,
    var profilePath: String?= null,
    createdDateTime:DateTime?= null,
    updatedDateTime:DateTime?= null,
    var status: UserStatus= UserStatus.ACTIVE,
    var accountNumber: String?= null,
    var fcmTopic: String?= null,
    var sex: Sex?= null,
    var actor: User?= null,
    var blames:List<Blame>?= null,
    var fcmToken:String?= null, company: Company?= null
): Entity(id, sequence, createdDateTime, updatedDateTime, company = company), Serializable{
    override fun toString(): String {
        return "${firstName} ${lastName}" + if(isNotEmpty(accountNumber)) "(${accountNumber})" else ""
    }
}