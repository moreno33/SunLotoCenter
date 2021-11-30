package com.sunlotocenter.dao

import org.joda.time.DateTime
import java.io.Serializable

class GameResult(sequence: Sequence,
                 var resultDate: DateTime? = null,
                 var session: GameSession? = null ,
                 var type: GameType? = null,
                 var lo1: String? = null,
                 var lo2: String? = null,
                 var lo3: String? = null,
                 var author: User?= null
) : Entity(sequence = sequence), Serializable