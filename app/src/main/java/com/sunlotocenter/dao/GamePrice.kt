package com.sunlotocenter.dao

import java.io.Serializable

class GamePrice(sequence: Sequence= Sequence(),
                var lo1Price: Int?= null,
                var lo2Price: Int?= null,
                var lo3Price: Int?= null,
                var marriagePrice:Int?= null,
                var loto3Price:Int?= null,
                var loto4Price: Int?= null,
                var loto5Price: Int?= null,
                var author: User, company: Company):
    Entity(sequence = sequence, company = company),
    Serializable