package api.types.cards

import api.types.cards.CardView

case class PileView(
    id: String,
    ownerId: Option[String],
    cards: Seq[CardView]
)
