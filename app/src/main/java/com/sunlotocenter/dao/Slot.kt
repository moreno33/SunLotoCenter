package com.sunlotocenter.dao

class Slot(var games: List<Game>,
           sequence: Sequence,
           var author:User,
           var type:GameType,
           var session: GameSession,
           var uniq:String,
           var status: SlotStatus= SlotStatus.ACTIVE,
           var gamePrice: GamePrice?= null,
           var total: Double,
           var totalWin: Double?= null, company: Company,
           var report: Report?= null):
    Entity(sequence = sequence, company = company), Cloneable{

        public override fun clone(): Any {
            var slot: Slot = try {
                super.clone() as Slot
            } catch (e: CloneNotSupportedException) {
                Slot(sequence = this.sequence!!,
                    author = this.author,
                    type = this.type,
                    session = this.session,
                    uniq = this.uniq,
                    status = this.status,
                    gamePrice = this.gamePrice,
                    total = this.total,
                    totalWin = this.totalWin,
                    company = this.company!!,
                    games = this.games,
                    report = this.report)
            }
            return slot;
        }

    }