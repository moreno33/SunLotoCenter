package com.sunlotocenter.dto

import com.sunlotocenter.dao.*

class Configuration(var connectedUser: User?= null,
                    var gameSchedules: ArrayList<GameSchedule>?= null,
                    var latestGameResult: GameResult?= null,
                    var company: Company?= null,
                    var version: Version?= null)