package controller.state.states

import controller.GameController
import controller.state.AbsState
import model.actions.Action

class ChoiceState extends AbsState {

  val MAX_CARDS = 5

  override def chooseCard(c: GameController, index: Int): Unit = {
    if c.getChosenCardsIndexes.contains(index) then
      throw new Exception("You can't choose the same card twice")
    c.addToChosenCardsIndexes(index)
  }

  override def removeChoice(c: GameController, index: Int): Unit = {
    if !c.getChosenCardsIndexes.contains(index) then
      throw new Exception("You can't remove a card that you haven't chosen")
    c.removeChosenCardsIndexes(index)
  }

  private def validatePlay(c: GameController): Unit = {
    if c.getChosenCardsIndexes.isEmpty then
      throw new Exception("You must choose at least one card")
    if c.getChosenCardsIndexes.length > MAX_CARDS then
      throw new Exception("You can't choose more than 5 cards")
  }

  override def play(c: GameController): Unit = {
    validatePlay(c)
    c.changeState(new CalculateScore(c))
  }

  override def discard(c: GameController): Unit = {
    validatePlay(c)
    c.removeFromHand(c.getChosenCardsIndexes)
    handleHandReset(c)
  }

  override def availableActions: List[Action] = List(
    new model.actions.Play(),
    new model.actions.Discard()
  ) ++ (
    for index <- GameController.instance.getChosenCardsIndexes
    yield model.actions.RemoveChoice(index)
  ) ++ (
    for index <- GameController.instance.hand.cards.indices
    if !GameController.instance.getChosenCardsIndexes.contains(index)
    yield model.actions.PlayCard(index)
  )
  
  def messageToShow: String = "Choose cards to play or discard"
}
