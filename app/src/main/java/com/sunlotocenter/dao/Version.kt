package com.sunlotocenter.dao

import org.joda.time.DateTime
import java.io.Serializable

class Version (var id: Long? = null,
               var createdDateTime: DateTime? = null,
               var updatedDateTime: DateTime? = null,
               var selected:Boolean= true,
               var versionCode: Int?= null,
               var versionName: String?= null,
               var author: User?= null): Serializable