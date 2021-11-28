package com.sunlotocenter.dao

class Slot(var games: List<Game>, sequence: Sequence, var author:User, var type:GameType, var session: GameSession, var uniq:String,
           var current: Boolean= true, var status: SlotStatus= SlotStatus.ACTIVE, var gamePrice: GamePrice?= null):Entity(sequence = sequence)