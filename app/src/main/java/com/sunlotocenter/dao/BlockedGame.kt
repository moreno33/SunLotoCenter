package com.sunlotocenter.dao

class BlockedGame(sequence: Sequence, var number: String, var author: User, var type: GameType?= null, company: Company): Entity(sequence = sequence, company = company)