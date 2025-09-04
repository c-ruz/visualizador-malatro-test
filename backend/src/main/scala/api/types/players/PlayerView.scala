package api.types.players

import api.types.cards.CardView

case class PlayerView(
    id: String,
    name: String,
    hand: Seq[CardView],
)
