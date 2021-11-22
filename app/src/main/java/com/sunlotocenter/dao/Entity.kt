package com.sunlotocenter.dao

import org.joda.time.DateTime
import java.io.Serializable

abstract class Entity(
    var id: Long? = null,
    var sequence: Sequence,
    var createdDateTime: DateTime? = null,
    var updatedDateTime: DateTime? = null
) : Serializable{

    val classType: Class<out Entity?>

    init {
        classType = javaClass
    }
}