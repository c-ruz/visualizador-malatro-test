package model.actions

import controller.GameController

class NextRound extends Action {

  override val name: String = "Next Round"

  override def doAction(c: GameController): String = {
    c.next()
    "Next round started"
  }
}
