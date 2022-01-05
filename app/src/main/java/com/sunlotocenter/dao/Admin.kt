package com.sunlotocenter.dao

import org.joda.time.DateTime
import org.joda.time.DateTimeZone

//var classType: Class<out Account>? = this::class.java
open class Admin(id:Long?=null,
            sequence: Sequence= Sequence(),
            firstName:String?= null,
            lastName: String?= null,
            phoneNumber:PhoneNumber?= null,
            address:String?= null,
            city:String?= null,
            password:String?= null,
            profilePath:String?= null,
            createdDateTime: DateTime ?= null,
            updatedDateTime: DateTime ?= null,
            status: UserStatus= UserStatus.ACTIVE,
            accountNumber: String?= null,
            fcmTopic: String?= null,
            sex: Sex?= null,
            actor: User?= null,
            company: Company?= null
): User(id,
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
    accountNumber,
    fcmTopic,
    sex,
    actor,
company = company)