package com.sunlotocenter.dao

import java.io.Serializable

open abstract class Game(var id:Long?= null, var number:String, var amount: Double, var opt: String, var position:Int, var type:Int, var slot: Slot?= null):
    Comparable<Game>, Cloneable, Serializable{
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other== null) return false
        if (javaClass != other?.javaClass) return false
        if(number.isEmpty()) return false
        return number== (other as Game).number
    }

    override fun hashCode(): Int {
        return number.hashCode()
    }

    override fun compareTo(other: Game): Int {
        if(this.position== other.position) {
            if(this.type== other.type) {
                if(opt.isEmpty()){
                    if(this.number== other.number) return 0
                    else return 1
                }else{
                    if(this.number== other.number){
                        if(this.opt== other.opt) return 0
                        else return 1
                    }
                    else return 1
                }

            }
            else if(this.type> other.type) return 1
            else return -1
        }
        else if(this.position> other.position) return 1
        else return -1
    }

    public override fun clone(): Any {
        throw CloneNotSupportedException("Abstract is not suposed to be cloned")
    }

    val classType: Class<out Game?>

    init {
        classType = javaClass
    }

}
