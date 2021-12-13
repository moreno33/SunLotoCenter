package com.sunlotocenter.dto

import com.sunlotocenter.dao.GameSession

import com.sunlotocenter.dao.GameType




class GametDto (var number: String? = null,
                var type: GameType? = null,
                var session: GameSession? = null ,
                var amount:Double = 0.0)