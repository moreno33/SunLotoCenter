package com.sunlotocenter.dto

import com.sunlotocenter.dao.Company
import com.sunlotocenter.dao.GameResult
import com.sunlotocenter.dao.GameSchedule
import com.sunlotocenter.dao.User

class Configuration(var connectedUser: User?= null,
                    var gameSchedules: ArrayList<GameSchedule>?= null,
                    var latestGameResult: GameResult?= null,
                    var company: Company?= null)