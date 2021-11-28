package com.sunlotocenter.dao

import com.sunlotocenter.utils.isNotEmpty

class Loto5(number:String, amount:Double, option:String, type: Int): Game(null, number, amount, option,4, type){

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other== null) return false
        if (javaClass != other?.javaClass) return false
        return this.number== (other as Game).number && this.opt== other.opt
    }

    override fun hashCode(): Int {
        return number.hashCode()*amount.hashCode()*opt.hashCode()*type.hashCode()
//        return number.hashCode()
    }


    override fun clone(): Any {
        var loto5: Loto5 = try {
            super.clone() as Loto5
        } catch (e: CloneNotSupportedException) {
            Loto5(this.number, this.amount, this.opt, this.type)
        }
        return loto5
    }

    override fun compareTo(other: Game): Int {
        if(this.position== other.position) {
            if(this.type== other.type) {
                if(this.number== other.number){
                    if(this.opt== other.opt) return 0
                    else return 1
                }
                else return 1
            }
            else if(this.type> other.type) return 1
            else return -1
        }
        else if(this.position> other.position) return 1
        else return -1
    }
}