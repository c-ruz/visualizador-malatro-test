package model.actions

class Play extends Action:

  override val name: String = "Play"

  override def doAction(c: controller.GameController): String =
    c.play()
    "Played"
