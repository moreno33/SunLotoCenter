package com.sunlotocenter.dao

class Loto5(number:String, amount:Double, option:String, type: Int): Game(null, number, amount, option,4, type){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other== null) return false
        if (javaClass != other?.javaClass) return false
        return number== (other as Game).number
    }

    override fun hashCode(): Int {
        if(number.isEmpty()) return number.hashCode()*amount.hashCode()*opt.hashCode()*type.hashCode()
        return number.hashCode()
    }


    override fun clone(): Any {
        var loto5: Loto5 = try {
            super.clone() as Loto5
        } catch (e: CloneNotSupportedException) {
            Loto5(this.number, this.amount, this.opt, this.type)
        }
        return loto5
    }
}