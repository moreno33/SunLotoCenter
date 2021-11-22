package com.sunlotocenter.dao

class Marriage(number:String, amount:Double, option:String, type: Int): Game(null, number, amount, option,1, type){

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
        var marriage: Marriage = try {
            super.clone() as Marriage
        } catch (e: CloneNotSupportedException) {
            Marriage(this.number, this.amount, this.opt, this.type)
        }
        return marriage
    }

    override fun compareTo(other: Game): Int {
        if(this.position== other.position) {
            if(this.type== other.type) {
                if(this.number== other.number || this.number== other.number.split("X")[1] +"X"+other.number.split("X")[0]){
                    return 0
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