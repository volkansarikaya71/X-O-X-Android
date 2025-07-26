package com.vs.xoxgame

data class ButtonState(val buttonId: Int, val tag: String?)

class GameInfo {
    companion object {
        var firstPlayerSkor: Int = 0
        var secondPlayerSkor: Int = 0
        var startPlayerSecond: Boolean? = null
        var checkwinorloseordraw:Int=0
        var winnername:String?=null
        val buttonStates = mutableListOf<ButtonState>()
    }
}
