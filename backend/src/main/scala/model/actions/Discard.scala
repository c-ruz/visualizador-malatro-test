package model.actions

class Discard extends Action {
  override val name: String = "Discard"

  override def doAction(c: controller.GameController): String =
    c.discard()
    "Discarded"
}
