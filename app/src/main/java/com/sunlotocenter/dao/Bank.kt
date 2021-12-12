package com.sunlotocenter.dao

import java.io.Serializable

class Bank(sequence: Sequence= Sequence(),
           var name:String?= null,
           var profilePath: String?= null,
           var code:String?= null,
           var address:String?= null,
           var city:String?= null,
           var author: User?= null,
           var worker: User?= null, company: Company):
    Entity(sequence = sequence, company = company),
    Serializable