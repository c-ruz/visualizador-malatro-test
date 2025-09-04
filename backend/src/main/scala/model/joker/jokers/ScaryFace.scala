package model.joker.jokers

import model.base.Score
import model.joker.AJoker

class ScaryFace extends AJoker {

  override def applyFace(score: Score): Score = new Score(score.chips + 30, score.multi)

}
