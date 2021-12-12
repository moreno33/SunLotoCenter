package com.sunlotocenter.dao

import java.io.Serializable

open class Notification(
    sequence: Sequence,
    var message:String?= null,
    var author:User?= null,
    var state: NotificationState = NotificationState.NEW,
    var code: NotificationCode= NotificationCode.ADMIN_BROADCAST, company: Company
) : Entity(sequence = sequence, company = company),
    Serializable{
}