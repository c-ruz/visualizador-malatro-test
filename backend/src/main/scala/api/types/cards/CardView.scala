package api.types.cards

case class CardView(
    id: String,
    faceUp: Boolean,
    name: String,
    attributes: List[CardAttribute]
)
