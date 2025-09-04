package model.actions

import controller.GameController

class PlayCard(index: Int) extends Action {
  override val name: String = "PlayCard/" + index

  override def doAction(c: GameController): String = {
    c.choseCard(index)
    "Card chosen"
  }
}
