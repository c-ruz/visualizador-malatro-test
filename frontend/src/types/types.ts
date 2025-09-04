export interface Card {
  id: string
  name: string
  faceUp: boolean
  attributes: CardAttribute[]
}

export interface CardPile {
  id: string
  ownerId?: string
  cards: Card[]
}

export interface Player {
  id: string
  name: string
  hand: Card[]
  score: number
}

export interface CardAttribute {
  name: string
  value: string
}

export interface Score {
  name: string
  value: string
}
