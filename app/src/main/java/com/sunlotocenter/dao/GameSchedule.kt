package com.sunlotocenter.dao

import org.joda.time.DateTime
import org.joda.time.LocalTime
import java.io.Serializable

class GameSchedule (sequence: Sequence,
                    var type: GameType?= null,
                    var morningTime: LocalTime?= null,
                    var nightTime: LocalTime?= null,
                    var secInterval: Long?= null,
                    var current:Boolean= true, var author:User,
                    var status:GameScheduleStatus= GameScheduleStatus.ACTIVE):
    Entity(sequence = sequence),
        Serializable, Cloneable{
            public override fun clone(): Any {
                var gameSchedule: GameSchedule = try {
                    super.clone() as GameSchedule
                } catch (e: CloneNotSupportedException) {
                    GameSchedule(this.sequence, this.type, this.morningTime, this.nightTime, this.secInterval, this.current, this.author, this.status)
                }
                return gameSchedule
            }
        }