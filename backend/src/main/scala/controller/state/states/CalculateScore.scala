package controller.state.states

import controller.GameController
import controller.state.AbsState
import model.base.{Score, Scorer}

class CalculateScore(c: GameController) extends AbsState {

  private val score = Scorer.score(
    GameController.instance.hand.play(c.getChosenCardsIndexes),
    GameController.instance.hand.jokers
  )

  c.score = score

  override def play(c: GameController): Unit = {
    c.totalScore = c.totalScore + (c.score.chips * c.score.multi)
    c.score = new Score(0, 0)
    c.removeFromHand(c.getChosenCardsIndexes)
    handleHandReset(c)
  }

  override def availableActions: List[model.actions.Action] =
      List(new model.actions.Play())
      
  def messageToShow: String = "Calculating Score... press Play to continue"
}
