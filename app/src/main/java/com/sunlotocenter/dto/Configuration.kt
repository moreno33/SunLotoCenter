package com.sunlotocenter.dto

import com.sunlotocenter.dao.GameResult
import com.sunlotocenter.dao.GameSchedule
import com.sunlotocenter.dao.User

class Configuration(var connectedUser: User, var gameSchedules: ArrayList<GameSchedule>, var latestGameResult: GameResult)