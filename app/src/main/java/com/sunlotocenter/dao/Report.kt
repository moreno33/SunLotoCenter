package com.sunlotocenter.dao

import com.sunlotocenter.dao.Entity
import com.sunlotocenter.dao.Slot

import com.sunlotocenter.dao.GameSession
import com.sunlotocenter.dao.Sequence

import org.joda.time.DateTime


class Report(sequence: Sequence,
             var reportDate: DateTime? = null,
             var slots: MutableList<Slot> = ArrayList(),
             var totalMorning: Double? = null,
             var winMorning: Double? = null,
             var totalNight: Double? = null,
             var winNight: Double? = null,
             var current:Boolean= true,
             var type: GameType): Entity(sequence = sequence) {
    fun addSlot(slot: Slot) {
        slots.add(slot)
    }
}