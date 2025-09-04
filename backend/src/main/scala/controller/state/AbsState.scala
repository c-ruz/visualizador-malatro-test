package controller.state

import controller.GameController
import controller.state.states.ChoiceState
import exceptions.state.InvalidTransitionState

abstract class AbsState extends GameState {
  override def chooseCard(c: GameController, index: Int): Unit =
    throw new InvalidTransitionState(this.getClass.getSimpleName, "chooseCard")
  override def removeChoice(c: GameController, index: Int): Unit =
    throw new InvalidTransitionState(this.getClass.getSimpleName, "removeChoice")
  override def play(c: GameController): Unit =
    throw new InvalidTransitionState(this.getClass.getSimpleName, "next")
  override def discard(c: GameController): Unit =
    throw new InvalidTransitionState(this.getClass.getSimpleName, "discard")
  override def availableActions: List[model.actions.Action] = List()
  
  // helper
  protected def handleHandReset(c: GameController): Unit = {
    c.resetChosenCardsIndexes()
    c.drawFromDeck(c.hand.MAX_CARDS - c.hand.cards.length)
    c.changeState(new ChoiceState)
  }
}
