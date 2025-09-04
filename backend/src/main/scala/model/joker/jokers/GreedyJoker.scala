package model.joker.jokers

import model.base.Score
import model.joker.{AJoker, Joker}

class GreedyJoker extends AJoker {

  override def applyDiamonds(score: Score): Score = new Score(score.chips, score.multi + 3)

}
