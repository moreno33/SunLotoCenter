package com.sunlotocenter.dao

import com.sunlotocenter.utils.isNotEmpty
import java.io.Serializable

open abstract class Game(var id:Long?= null,
                         var number:String,
                         var amount: Double,
                         var opt: String,
                         var position:Int,
                         var type:Int,
                         var slot: Slot?= null,
                         var amountWin: Double= 0.0):
    Comparable<Game>, Cloneable, Serializable{
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other== null) return false
        if (javaClass != other.javaClass) return false
        if(!isNotEmpty(number)) return false
        return number== (other as Game).number
    }

    override fun hashCode(): Int {
        return number.hashCode()
    }

    override fun compareTo(other: Game): Int {
        throw NoSuchMethodException("Method should not be called here")
    }

    public override fun clone(): Any {
        throw CloneNotSupportedException("Abstract is not suposed to be cloned")
    }

    val classType: Class<out Game?>

    init {
        classType = javaClass
    }

}
