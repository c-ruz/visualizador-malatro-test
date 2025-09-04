package model.joker.jokers

import model.base.Score
import model.joker.AJoker

class EvenSteven extends AJoker {

  override def applyEven(score: Score): Score = new Score(score.chips, score.multi + 4)
  
}
