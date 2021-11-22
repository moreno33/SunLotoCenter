package com.sunlotocenter.dao

class GameSchedule (sequence: Sequence,
                    var type: GameType?= null,
                    var morningTime: String?= null,
                    var nightTime: String?= null,
                    var secInterval: Long?= null,
                    var current:Boolean= true, var author:User): Entity(sequence = sequence)