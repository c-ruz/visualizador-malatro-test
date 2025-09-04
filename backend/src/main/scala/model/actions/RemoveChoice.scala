package model.actions

import controller.GameController

class RemoveChoice(index: Int) extends Action {

  override val name: String = "RemoveChoice/" + GameController.instance.hand.cards(index).toString

  override def doAction(c: GameController): String = {
    c.removeChoice(index)
    "Choice removed"
  }
}
