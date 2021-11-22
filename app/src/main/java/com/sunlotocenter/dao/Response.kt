package com.sunlotocenter.dao

open class Response<T> constructor(
        /**
         * To keep track whether the request is successful is not
         */
        val success: Boolean = true,
        val message: String?= null,
        /**
         * If we are responding with a list, this variable will
         * hold how many of this item we have in database
         */
        val total: Long = 0,

        /**
         * This is the data that we are going to transfer
         */
        val data: T? = null

)