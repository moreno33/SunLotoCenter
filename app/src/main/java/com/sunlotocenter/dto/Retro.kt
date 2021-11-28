package com.sunlotocenter.dto

import com.sunlotocenter.dao.Slot

import com.sunlotocenter.dao.GameSession

import org.joda.time.DateTime


class Report(var `when`: DateTime? = null,
             var slotsMorning: MutableList<Slot> = ArrayList(),
             var totalMorning: Double? = null,
             var winMorning: Double? = null,
             var slotsNight: MutableList<Slot> = ArrayList(),
             var totalNight: Double? = null,
             var winNight: Double? = null,) {
    fun addSlotMorning(slot: Slot) {
        slotsMorning.add(slot)
    }

    fun addSlotNight(slot: Slot) {
        slotsNight.add(slot)
    }
}
