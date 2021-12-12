package com.sunlotocenter.dao

import org.joda.time.DateTime
import java.io.Serializable

abstract class Entity(
    var id: Long? = null,
    var sequence: Sequence= Sequence(),
    var createdDateTime: DateTime? = null,
    var updatedDateTime: DateTime? = null,
    var current:Boolean= true,
    var company: Company?= null) : Serializable{

    val classType: Class<out Entity?>

    init {
        classType = javaClass
    }
}