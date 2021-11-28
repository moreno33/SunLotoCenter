package com.sunlotocenter.dao

import com.sunlotocenter.utils.isNotEmpty

class Loto3(number:String, amount:Double, option:String, type: Int): Game(null, number, amount, option, 2, type){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other== null) return false
        if (javaClass != other?.javaClass) return false
        return number== (other as Game).number
    }

    override fun hashCode(): Int {
        return number.hashCode()*amount.hashCode()*opt.hashCode()*type.hashCode()
//        return number.hashCode()
    }


    override fun clone(): Any {
        var loto3: Loto3 = try {
            super.clone() as Loto3
        } catch (e: CloneNotSupportedException) {
            Loto3(this.number, this.amount, this.opt, this.type)
        }
        return loto3
    }

    override fun compareTo(other: Game): Int {
        if(this.position== other.position) {
            if(this.type== other.type) {
                if(this.number== other.number) return 0
                else return 1
            }
            else if(this.type> other.type) return 1
            else return -1
        }
        else if(this.position> other.position) return 1
        else return -1
    }
}