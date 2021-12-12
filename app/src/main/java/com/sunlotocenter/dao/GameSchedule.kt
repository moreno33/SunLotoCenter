package com.sunlotocenter.dao

import com.sunlotocenter.MyApplication
import org.joda.time.DateTime
import org.joda.time.LocalTime
import java.io.Serializable

class GameSchedule (sequence: Sequence,
                    var type: GameType?= null,
                    var morningTime: LocalTime?= null,
                    var nightTime: LocalTime?= null,
                    var secInterval: Long?= null,
                    var author:User,
                    var status:GameScheduleStatus= GameScheduleStatus.ACTIVE, company: Company?= null):
    Entity(sequence = sequence, company = company),
        Serializable, Cloneable{
            public override fun clone(): Any {
                var gameSchedule: GameSchedule = try {
                    super.clone() as GameSchedule
                } catch (e: CloneNotSupportedException) {
                    GameSchedule(this.sequence, this.type, this.morningTime, this.nightTime, this.secInterval, this.author, this.status, company = company)
                }
                return gameSchedule
            }
        }