package com.sunlotocenter.dao

import org.joda.time.DateTime
import java.io.Serializable

class Blame(var id: Long?= null, var user: User, var author: User, var mesage:String, var createdDateTime: DateTime? = null):
    Serializable