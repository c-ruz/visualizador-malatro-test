package api.types

import api.types.cards.PileView
import api.types.players.PlayerView
import api.types.score.ScoreView

case class GameView(
    players: List[PlayerView],
    cardPiles: List[PileView],
    currentPlayerId: Option[String],
    scores: List[ScoreView],
    messageToShow: Option[String]
)
