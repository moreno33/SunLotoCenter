package com.sunlotocenter.dao

import org.joda.time.DateTime
import java.io.Serializable

class Company(var id: Long? = null,
    var sequence: Sequence?= null,
    var createdDateTime: DateTime? = null,
    var updatedDateTime: DateTime? = null,
    var current:Boolean= true,
    var name:String?= null,
    var phoneNumber:PhoneNumber?= null,
    var address:String?= null,
    var city:String?= null,
    var profilePath:String?= null,
              var author: User?= null,
              var status: CompanyStatus= CompanyStatus.ACTIVE) : Serializable