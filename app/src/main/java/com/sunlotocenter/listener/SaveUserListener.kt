package com.sunlotocenter.listener

import com.sunlotocenter.dao.User

interface SaveUserListener {
    fun save(user: User)
}