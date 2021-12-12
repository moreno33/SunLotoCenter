package com.sunlotocenter.dao

import java.io.Serializable

class GameAlertAndBlock (sequence: Sequence,
                         var borletAlertPrice:Int?= null,
                         var borletBlockPrice:Int?= null,
                         var marriageAlertPrice:Int?= null,
                         var marriageBlockPrice:Int?= null,
                         var loto3AlertPrice:Int?= null,
                         var loto3BlockPrice:Int?= null,
                         var loto4AlertPrice:Int?= null,
                         var loto4BlockPrice:Int?= null,
                         var loto5AlertPrice:Int?= null,
                         var loto5BlockPrice:Int?= null,
                         var author: User, company: Company):
        Entity(sequence = sequence, company = company),
        Serializable