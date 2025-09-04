package controller.state

import controller.GameController
import model.actions.Action

trait GameState {
  def chooseCard(c: GameController, index: Int): Unit
  def removeChoice(c: GameController, index: Int): Unit
  def play(c: GameController): Unit
  def discard(c: GameController): Unit
  def availableActions: List[Action]
  def messageToShow: String
}
