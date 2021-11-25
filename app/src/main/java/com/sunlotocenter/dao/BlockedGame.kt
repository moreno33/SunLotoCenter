package com.sunlotocenter.dao

class BlockedGame(sequence: Sequence, var number: String, var author: User, var type: GameType?= null, var current: Boolean= true): Entity(sequence = sequence)