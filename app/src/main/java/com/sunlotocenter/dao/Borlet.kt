package com.sunlotocenter.dao

class Borlet(number:String, amount:Double, option:String, type: Int): Game(null, number, amount, option,0, type){
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
        var borlet: Borlet = try {
            super.clone() as Borlet
        } catch (e: CloneNotSupportedException) {
            Borlet(this.number, this.amount, this.opt, this.type)
        }
        return borlet
    }
}