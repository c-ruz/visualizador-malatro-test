package model.joker.jokers

import model.base.Score
import model.joker.{AJoker, Joker}

class DeviousJoker extends AJoker {

  override def applyStraight(score: Score): Score = new Score(score.chips + 100, score.multi)

}
