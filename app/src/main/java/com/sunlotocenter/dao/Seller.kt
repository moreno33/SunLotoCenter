package com.sunlotocenter.dao

import org.joda.time.DateTime

class Seller(
     id:Long=-1,
     sequence: Sequence= Sequence(),
     firstName:String="",
     lastName: String="",
     phoneNumber:PhoneNumber?= null,
     address:String="",
     city:String="",
     password:String="",
     profilePath:String= "",
     createdDateTime: DateTime = DateTime(),
     updatedDateTime: DateTime = DateTime(),
     status: UserStatus= UserStatus.ACTIVE,
     current: Boolean= true,
     accountNumber: String= "",
     fcmTopic: String= "",
     sex: Sex= Sex.MALE,
     actor: User?= null
):
User(id,
    sequence,
    firstName,
    lastName,
    phoneNumber,
    address,
    city,
    password,
    profilePath,
    createdDateTime,
    updatedDateTime,
    status,
    current,
    accountNumber,
    fcmTopic,
    sex,
    actor
)