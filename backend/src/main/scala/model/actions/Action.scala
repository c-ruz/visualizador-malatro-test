package model.actions

import controller.GameController


trait Action:
  val name: String
  def doAction(c: GameController): String
